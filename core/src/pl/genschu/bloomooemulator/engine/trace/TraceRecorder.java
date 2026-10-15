package pl.genschu.bloomooemulator.engine.trace;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import pl.genschu.bloomooemulator.interpreter.ast.*;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionFrame;
import pl.genschu.bloomooemulator.logic.BuildInfo;

/** Owned by one game and called only on its execution thread. Failures disable recording. */
public final class TraceRecorder implements AutoCloseable {
    public interface Scope extends AutoCloseable { @Override void close(); }
    private static final Scope NO_SCOPE = () -> {};
    private record Binding(TraceRecorder recorder, Supplier<Context> context) {}
    private static final ThreadLocal<Binding> ACTIVE = new ThreadLocal<>();
    private OutputStream output;
    private final long limit;
    private long bytes, sequence, frame, invocation, lastWrittenSequence;
    private final IdentityHashMap<Object, Integer> identities = new IdentityHashMap<>();
    private final Deque<Long> calls = new ArrayDeque<>();
    private final Deque<Long> signals = new ArrayDeque<>();
    private long signalSequence;
    private final Set<Integer> scripts = new HashSet<>();
    private Map<String, Object> previous = new LinkedHashMap<>();
    private String failure;

    public TraceRecorder(File path, long limit, Map<String, Object> metadata) throws IOException {
        if (limit < 1024) throw new IllegalArgumentException("Trace limit must be at least 1024 bytes");
        this.limit = limit;
        File parent = path.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.isDirectory() && !parent.mkdirs())
            throw new IOException("Cannot create trace directory: " + parent);
        if (!path.createNewFile()) throw new IOException("Trace file already exists: " + path);
        output = new BufferedOutputStream(new FileOutputStream(path));
        write(Map.of("kind", "header", "format", "rex-trace", "version", 2,
                "build", BuildInfo.displayVersion(), "metadata", metadata));
    }

    public boolean isActive() { return output != null; }
    public String failure() { return failure; }

    public static TraceRecorder forContext(Context context) {
        if (context == null) return null;
        if (context.exec().getTraceRecorder() != null) return context.exec().getTraceRecorder();
        return context.getGame() == null ? null : context.getGame().getTraceRecorder();
    }

    /** Restores the previous binding even for reentrant scripts and failed handlers. */
    public Scope activate(Supplier<Context> context) {
        if (!isActive()) return NO_SCOPE;
        Binding previous = ACTIVE.get();
        ACTIVE.set(new Binding(this, context));
        return () -> {
            if (previous == null) ACTIVE.remove();
            else ACTIVE.set(previous);
        };
    }

    public static Scope signal(Variable source, String emitted, String bindingName, Value qualifier,
                               List<Value> payload, boolean explicit, boolean handled) {
        Binding active = ACTIVE.get();
        if (active == null || !active.recorder.isActive()) return NO_SCOPE;
        TraceRecorder recorder = active.recorder;
        try {
            Context context = active.context.get();
            long signalId = ++recorder.signalSequence;
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("signalId", signalId);
            details.put("parentSignal", recorder.signals.isEmpty() ? 0 : recorder.signals.peek());
            details.put("parentCall", recorder.calls.isEmpty() ? 0 : recorder.calls.peek());
            details.put("source", source.name());
            details.put("sourceType", source.type().name());
            details.put("emitted", emitted);
            details.put("binding", handled ? bindingName : null);
            details.put("handled", handled);
            details.put("qualifier", qualifier == null ? null : TraceValues.value(qualifier));
            List<Object> values = new ArrayList<>();
            for (Value value : payload) values.add(TraceValues.value(value));
            details.put("payload", values);
            details.put("explicitPayload", explicit);
            recorder.event("signal", context, details);
            if (!recorder.isActive()) return NO_SCOPE;
            recorder.signals.push(signalId);
            return () -> {
                recorder.event("signalEnd", context, Map.of("signalId", signalId));
                if (!recorder.signals.isEmpty()) recorder.signals.pop();
            };
        } catch (RuntimeException error) {
            recorder.disable(error);
            return NO_SCOPE;
        }
    }

    public void condition(Context context, String subject, ASTNode node, Value rawValue,
                          boolean result, String evaluationError) {
        if (!isActive()) return;
        try {
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("subject", subject);
            details.put("call", calls.isEmpty() ? 0 : calls.peek());
            details.put("causeSignal", signals.isEmpty() ? 0 : signals.peek());
            if (node != null) details.put("node", id(node));
            details.put("result", result);
            details.put("rawValue", rawValue == null ? null : TraceValues.value(rawValue));
            details.put("evaluationError", evaluationError);
            if (evaluationError != null) details.put("outcome", "error: " + evaluationError);
            event("condition", context, details);
        } catch (RuntimeException error) { disable(error); }
    }

    private int id(Object object) {
        return identities.computeIfAbsent(object, ignored -> identities.size() + 1);
    }

    public long enter(Context context, ASTNode node) {
        if (!isActive()) return 0;
        long call = ++invocation;
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("call", call);
        details.put("parent", calls.isEmpty() ? 0 : calls.peek());
        details.put("causeSignal", signals.isEmpty() ? 0 : signals.peek());
        details.put("node", id(node));
        details.put("nodeType", node.getClass().getSimpleName());
        details.put("line", node.location().line());
        details.put("column", node.location().column());
        details.put("snippet", node.location().snippet());
        event("enter", context, details);
        if (isActive()) calls.push(call);
        return call;
    }

    public void leave(Context context, long call, String outcome) {
        if (call == 0) return;
        event("leave", context, Map.of("call", call, "outcome", outcome));
        if (!calls.isEmpty()) calls.pop();
    }

    public void boundary(String kind, Context context, long time) {
        if (!isActive()) return;
        if (kind.equals("updateBegin")) frame++;
        event(kind, context, Map.of("engineTimeMs", time));
        try { if (output != null) output.flush(); }
        catch (IOException error) { disable(error); }
    }

    public void script(String name, String source, ASTNode root) {
        if (!isActive()) return;
        try {
            int rootId = id(root);
            if (scripts.add(rootId)) write(Map.of("kind", "script", "name", name,
                    "source", source == null ? "" : source, "root", astData(root)));
        } catch (RuntimeException error) { disable(error); }
    }

    // Explicit schema also works on Android versions without record reflection APIs.
    private Object astData(Object value) {
        if (value instanceof ASTNode node) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", id(node));
            data.put("type", node.getClass().getSimpleName());
            switch (node) {
                case LiteralNode n -> fields(data, "value", n.value());
                case VariableNode n -> fields(data, "name", n.name());
                case BlockNode n -> fields(data, "statements", n.statements());
                case ArithmeticNode n -> fields(data, "left", n.left(), "right", n.right(), "operator", n.operator().name());
                case ComparisonNode n -> fields(data, "left", n.left(), "right", n.right(), "operator", n.operator().name());
                case LogicalNode n -> fields(data, "left", n.left(), "right", n.right(), "operator", n.operator().name());
                case IfNode n -> fields(data, "condition", n.condition(), "thenBranch", n.thenBranch(), "elseBranch", n.elseBranch());
                case LoopNode n -> fields(data, "start", n.start(), "diff", n.diff(), "step", n.step(), "body", n.body());
                case WhileNode n -> fields(data, "condition", n.condition(), "body", n.body());
                case ReturnNode n -> fields(data, "value", n.value());
                case MethodCallNode n -> fields(data, "target", n.target(), "methodName", n.methodName(), "arguments", n.arguments());
                case PointerDerefNode n -> fields(data, "expression", n.expression());
                case StructAccessNode n -> fields(data, "structName", n.structName(), "fieldName", n.fieldName());
                case VarDefNode n -> fields(data, "varType", n.varType(), "varName", n.varName(), "initialValue", n.initialValue());
                case ConvNode n -> fields(data, "variable", n.variable(), "targetType", n.targetType());
                case BreakNode n -> { }
                case OneBreakNode n -> { }
            }
            data.put("line", node.location().line());
            data.put("column", node.location().column());
            return data;
        }
        if (value instanceof pl.genschu.bloomooemulator.interpreter.values.Value v) return TraceValues.value(v);
        if (value instanceof List<?> list) {
            List<Object> data = new ArrayList<>();
            for (Object item : list) data.add(astData(item));
            return data;
        }
        return value;
    }

    private void fields(Map<String, Object> data, Object... pairs) {
        for (int i = 0; i < pairs.length; i += 2) data.put((String)pairs[i], astData(pairs[i + 1]));
    }

    private void event(String kind, Context context, Map<String, Object> details) {
        if (!isActive()) return;
        try {
            Map<String, Object> current = new LinkedHashMap<>();
            Set<Context> visited = Collections.newSetFromMap(new IdentityHashMap<>());
            if (context != null && context.getGame() != null
                    && context.getGame().getCurrentSceneContext() instanceof Context scene)
                collect(scene, current, visited);
            collect(context, current, visited);
            List<Object> stack = new ArrayList<>();
            if (context != null) {
                for (ExecutionFrame f = context.exec().getCurrentFrame(); f != null; f = f.getParent()) {
                    String scope = "frame:" + id(f);
                    stack.add(Map.of("id", scope, "name", f.getName(), "instruction", f.getInstruction()));
                    for (var entry : f.getLocals().entrySet())
                        current.put(scope + ":" + entry.getKey(), TraceValues.snapshot(entry.getValue()));
                    if (f.getThis() != null) current.put(scope + ":@THIS", TraceValues.snapshot(f.getThis()));
                }
            }
            Map<String, Object> changes = new LinkedHashMap<>();
            Set<String> keys = new LinkedHashSet<>(previous.keySet());
            keys.addAll(current.keySet());
            for (String key : keys) {
                Object before = previous.get(key), after = current.get(key);
                if (!Objects.equals(before, after)) {
                    Map<String, Object> change = new LinkedHashMap<>();
                    change.put("before", before);
                    change.put("after", after);
                    changes.put(key, change);
                }
            }
            Map<String, Object> record = new LinkedHashMap<>(details);
            record.put("kind", kind);
            record.put("sequence", ++sequence);
            record.put("update", frame);
            if (context != null && context.getGame() != null) {
                record.put("scene", context.getGame().getCurrentScene());
                record.put("engine", context.getGame().getTraceState());
            }
            record.put("stack", stack);
            record.put("changes", changes);
            if (sequence == 1 || sequence % 256 == 0) record.put("checkpoint", current);
            write(record);
            if (isActive()) previous = current;
        } catch (RuntimeException error) { disable(error); }
    }

    private void collect(Context context, Map<String, Object> state, Set<Context> visited) {
        if (context == null || !visited.add(context)) return;
        String scope = "context:" + id(context);
        for (var entry : context.store().getAll().entrySet()) {
            state.put(scope + ":" + entry.getKey(), TraceValues.snapshot(entry.getValue()));
            if (entry.getValue() instanceof pl.genschu.bloomooemulator.interpreter.variable.capabilities.HasInstanceContext instance)
                collect(instance.getInstanceContext(), state, visited);
        }
        collect(context.getParent(), state, visited);
        for (Context additional : context.getAdditionalContexts()) collect(additional, state, visited);
    }

    private void write(Map<String, Object> record) {
        if (!isActive()) return;
        try {
            byte[] data = (TraceJson.encode(record) + "\n").getBytes(StandardCharsets.UTF_8);
            if (bytes + data.length + 128 > limit) { finish("limit"); return; }
            output.write(data);
            bytes += data.length;
            if (record.get("sequence") instanceof Number number) lastWrittenSequence = number.longValue();
        } catch (IOException | RuntimeException error) { disable(error); }
    }

    private void disable(Exception error) {
        failure = error.toString();
        System.err.println("Trace recording disabled: " + failure);
        if (output != null) {
            try { output.close(); } catch (IOException ignored) {}
            output = null;
        }
        identities.clear();
        previous.clear();
        scripts.clear();
        calls.clear();
        signals.clear();
    }

    private void finish(String reason) {
        if (output == null) return;
        try {
            output.write((TraceJson.encode(Map.of("kind", "end", "reason", reason,
                    "lastSequence", lastWrittenSequence)) + "\n").getBytes(StandardCharsets.UTF_8));
            output.close();
        } catch (IOException error) { disable(error); }
        finally { output = null; identities.clear(); previous.clear(); scripts.clear(); calls.clear(); signals.clear(); }
    }

    @Override public void close() { finish("closed"); }
}
