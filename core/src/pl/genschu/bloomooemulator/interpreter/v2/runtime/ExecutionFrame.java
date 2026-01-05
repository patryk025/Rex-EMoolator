package pl.genschu.bloomooemulator.interpreter.v2.runtime;

import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.v2.values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single frame in the execution stack.
 * Each frame corresponds to a behaviour call, loop iteration, or similar construct.
 *
 * Frames form a linked list (via parent pointer) representing the call stack.
 */
public class ExecutionFrame {
    private final String name;              // Name of this frame (e.g., "LOOP", "Behaviour:INIT")
    private final String instruction;       // Instruction being executed (e.g., "@LOOP", "@IF")
    private final SourceLocation location;  // Where in source code
    private final ExecutionFrame parent;    // Parent frame (null for root)

    // Local variables in this frame (e.g., $1, $2 for behaviour arguments)
    private final Map<String, Value> locals;
    private Value thisValue;

    public ExecutionFrame(
        String name,
        String instruction,
        SourceLocation location,
        ExecutionFrame parent
    ) {
        this.name = name != null ? name : "<unknown>";
        this.instruction = instruction != null ? instruction : "";
        this.location = location != null ? location : SourceLocation.UNKNOWN;
        this.parent = parent;
        this.locals = new HashMap<>();
        this.thisValue = null;
    }

    public String getName() {
        return name;
    }

    public String getInstruction() {
        return instruction;
    }

    public SourceLocation getLocation() {
        return location;
    }

    public ExecutionFrame getParent() {
        return parent;
    }

    /**
     * Sets a local variable in this frame.
     */
    public void setLocal(String name, Value value) {
        locals.put(name, value);
    }

    /**
     * Gets a local variable from this frame.
     * Returns null if not found.
     */
    public Value getLocal(String name) {
        return locals.get(name);
    }

    /**
     * Checks if this frame has a local variable with the given name.
     */
    public boolean hasLocal(String name) {
        return locals.containsKey(name);
    }

    public void setThis(Value value) {
        this.thisValue = value;
    }

    public Value getThis() {
        return thisValue;
    }

    public boolean hasThis() {
        return thisValue != null;
    }

    /**
     * Returns a string representation of this frame for stack traces.
     */
    public String toStackTraceString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  at ").append(name);

        if (!instruction.isEmpty()) {
            sb.append(" (").append(instruction).append(")");
        }

        sb.append(" @ ").append(location.toCompact());

        if (location != SourceLocation.UNKNOWN) {
            sb.append(": ").append(location.snippet());
        }

        return sb.toString();
    }

    /**
     * Returns the depth of this frame in the stack (0 = root).
     */
    public int getDepth() {
        int depth = 0;
        ExecutionFrame current = parent;
        while (current != null) {
            depth++;
            current = current.parent;
        }
        return depth;
    }

    @Override
    public String toString() {
        return "ExecutionFrame[" + name + " @ " + location.toCompact() + "]";
    }
}
