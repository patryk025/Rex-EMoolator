package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.runtime.ASTInterpreter;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionResult;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.engine.Game;
import pl.genschu.bloomooemulator.loader.CNVParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassVariable represents a CLASS definition.
 *
 * <p>{@code definingContext} captures the context the class was registered in
 * (set by {@link #init(Context)} during CNV parsing). NEW uses it as the
 * registration target for new instances and as the parent of their classContext,
 * so instances outlive the scene the NEW call originated from.
 */
public record ClassVariable(
    String name,
    String defPath,                      // Path to class definition file (DEF attribute)
    String basePath,                     // Base class (BASE attribute) - not yet used
    Map<String, SignalHandler> signals,
    Context definingContext              // Context where this class was declared; null until init()
) implements Variable, Initializable {

    public ClassVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // Convenience constructor
    public ClassVariable(String name, String defPath) {
        this(name, defPath, null, Map.of(), null);
    }

    public ClassVariable(String name, String defPath, String basePath, Map<String, SignalHandler> signals) {
        this(name, defPath, basePath, signals, null);
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public Value value() {
        // Class value is its definition path
        return new StringValue("CLASS[" + (defPath != null ? defPath : "undefined") + "]");
    }

    @Override
    public VariableType type() {
        return VariableType.CLASS;
    }

    @Override
    public Variable withValue(Value newValue) {
        // Class doesn't support direct value assignment
        // DEF and BASE are set via attributes during parsing
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new ClassVariable(name, defPath, basePath, newSignals, definingContext);
    }

    // ========================================
    // CLASS-SPECIFIC OPERATIONS
    // ========================================

    /**
     * Sets the DEF attribute (class definition file path).
     *
     * @param path Path to .cnv file
     * @return New ClassVariable with updated path
     */
    public ClassVariable withDefPath(String path) {
        return new ClassVariable(name, path, basePath, signals, definingContext);
    }

    /**
     * Sets the BASE attribute (base class for inheritance).
     *
     * @param base Base class name
     * @return New ClassVariable with updated base
     */
    public ClassVariable withBasePath(String base) {
        return new ClassVariable(name, defPath, base, signals, definingContext);
    }

    public ClassVariable withDefiningContext(Context context) {
        return new ClassVariable(name, defPath, basePath, signals, context);
    }

    @Override
    public void init(Context context) {
        // Capture the context that holds this declaration so instances created by
        // NEW from any nested scope (e.g. a scene-level __INIT__) still live with
        // the class, not with the caller.
        if (definingContext != context) {
            context.setVariable(name, withDefiningContext(context));
        }
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("NEW", MethodSpec.of((self, args, ctx) -> {
            ClassVariable thisVar = (ClassVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("NEW requires at least 1 argument (varName)");
            }
            if (ctx == null) {
                throw new IllegalArgumentException("CLASS.NEW requires MethodContext");
            }
            if (thisVar.defPath == null || thisVar.defPath.isBlank()) {
                throw new IllegalStateException("CLASS.NEW requires DEF path");
            }

            String varName = ArgumentHelper.getString(args.get(0));
            // Instance lives with the class declaration: if defined in app .cnv,
            // it survives scene changes; if defined in a scene .cnv, it dies with
            // the scene. Falls back to caller context for tests that build a
            // ClassVariable directly without going through CNV init.
            Context targetContext = thisVar.definingContext != null ? thisVar.definingContext : ctx.context();
            Context classContext = new Context(new ExecutionContext(), targetContext);
            classContext.setGame(ctx.getGame());

            Game game = ctx.getGame();
            String vfsPath = resolveDefinitionVfsPath(thisVar.defPath);
            CNVParser cnvParser = new CNVParser();

            if (game != null && game.getVfs().exists(vfsPath)) {
                try (InputStream is = game.getVfs().openRead(vfsPath)) {
                    cnvParser.parse(is, vfsPath, classContext);
                } catch (IOException e) {
                    Gdx.app.error("ClassVariable", "Error while loading class " + vfsPath, e);
                    throw new RuntimeException(e);
                }
            } else {
                Gdx.app.error("ClassVariable", "Class definition " + vfsPath + " doesn't exist. Instance will be empty.");
            }

            InstanceVariable instance = new InstanceVariable(varName, classContext);
            targetContext.setVariable(varName, instance);

            Variable constructorBehaviour = classContext.store().get("CONSTRUCTOR");
            if (constructorBehaviour instanceof BehaviourVariable behaviour) {
                ExecutionResult ctorResult = new ASTInterpreter(classContext)
                        .runBehaviour("CONSTRUCTOR:" + varName, instance, behaviour, args);
                return MethodResult.fromExecution(ctorResult);
            }

            return MethodResult.noReturn();
        })),

        Map.entry("DELETE", MethodSpec.of((self, args, ctx) -> {
            ClassVariable thisVar = (ClassVariable) self;
            if (args.isEmpty()) {
                throw new IllegalArgumentException("DELETE requires at least 1 argument (varName)");
            }
            if (ctx == null) {
                throw new IllegalArgumentException("CLASS.DELETE requires MethodContext");
            }

            String varName = ArgumentHelper.getString(args.get(0));
            Context targetContext = thisVar.definingContext != null ? thisVar.definingContext : ctx.context();
            Variable instanceVariable = targetContext.getVariable(varName);
            if (!(instanceVariable instanceof InstanceVariable instance)) {
                Gdx.app.error("ClassVariable", "Variable is not an INSTANCE: " + varName);
                return MethodResult.noReturn();
            }

            Variable destructorBehaviour = instance.instanceContext().store().get("DESTRUCTOR");
            ExecutionResult dtorResult = null;
            if (destructorBehaviour instanceof BehaviourVariable behaviour) {
                dtorResult = new ASTInterpreter(instance.instanceContext())
                        .runBehaviour("DESTRUCTOR:" + varName, instance, behaviour, args);
            }

            targetContext.removeVariable(varName);
            if (dtorResult != null) {
                return MethodResult.fromExecution(dtorResult);
            }
            return MethodResult.noReturn();
        }))
    );

    private static String resolveDefinitionVfsPath(String definitionPath) {
        String classPath = definitionPath.startsWith("$")
                ? definitionPath
                : "$COMMON/classes/" + definitionPath;
        return classPath.substring(1).replaceFirst("^[/\\\\]+", "").replace('\\', '/');
    }

    @Override
    public String toString() {
        return "Class[" + name + " def=" + (defPath != null ? defPath : "undefined") + "]";
    }
}
