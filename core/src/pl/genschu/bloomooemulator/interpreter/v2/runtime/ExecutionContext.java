package pl.genschu.bloomooemulator.interpreter.v2.runtime;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.v2.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.v2.values.Value;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * Execution context that manages the call stack and provides stack trace functionality.
 *
 * This wraps the old Context (for now) and adds v2 functionality on top.
 * Eventually, this will completely replace the old Context.
 */
public class ExecutionContext {
    private final Context legacyContext;  // Old context - temporary bridge
    private ExecutionFrame currentFrame;  // Current execution frame
    private int maxStackDepth = 1000;     // Maximum stack depth before overflow
    private final Map<String, Value> globalVariables = new HashMap<>();

    public ExecutionContext(Context legacyContext) {
        this.legacyContext = legacyContext;
        this.currentFrame = null;
    }

    /**
     * Returns the legacy context (temporary - for migration).
     */
    public Context getLegacyContext() {
        return legacyContext;
    }

    /**
     * Returns the current execution frame.
     */
    public ExecutionFrame getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Pushes a new execution frame onto the stack.
     *
     * @throws StackOverflowError if max stack depth is exceeded
     */
    public void pushFrame(String name, String instruction, SourceLocation location) {
        if (currentFrame != null && currentFrame.getDepth() >= maxStackDepth) {
            throw new StackOverflowError(
                "Maximum stack depth exceeded (" + maxStackDepth + ")\n" +
                getStackTrace()
            );
        }

        currentFrame = new ExecutionFrame(name, instruction, location, currentFrame);
    }

    /**
     * Pops the current execution frame from the stack.
     *
     * @throws IllegalStateException if stack is empty
     */
    public void popFrame() {
        if (currentFrame == null) {
            throw new IllegalStateException("Cannot pop from empty execution stack");
        }
        currentFrame = currentFrame.getParent();
    }

    /**
     * Returns the current stack depth (0 = no frames).
     */
    public int getStackDepth() {
        return currentFrame != null ? currentFrame.getDepth() + 1 : 0;
    }

    /**
     * Sets a "this" value on the current frame.
     */
    public void setThis(Value value) {
        if (currentFrame == null) {
            throw new IllegalStateException("No active execution frame");
        }
        currentFrame.setThis(value);
    }

    /**
     * Gets the nearest "this" value from the current frame upwards.
     */
    public Value getThis() {
        ExecutionFrame frame = currentFrame;
        while (frame != null) {
            if (frame.hasThis()) {
                return frame.getThis();
            }
            frame = frame.getParent();
        }
        return null;
    }

    /**
     * Sets a local variable in the current frame.
     *
     * @throws IllegalStateException if no frame is active
     */
    public void setLocal(String name, Value value) {
        if (currentFrame == null) {
            throw new IllegalStateException("No active execution frame");
        }
        currentFrame.setLocal(name, value);
    }

    /**
     * Gets a local variable from the current frame or any parent frame.
     * Returns null if not found.
     */
    public Value getLocal(String name) {
        ExecutionFrame frame = currentFrame;
        while (frame != null) {
            if (frame.hasLocal(name)) {
                return frame.getLocal(name);
            }
            frame = frame.getParent();
        }
        return null;
    }

    /**
     * Sets a variable in the active scope. If a local exists, it is updated; otherwise
     * the value is stored in the current frame (if present) or as a global.
     */
    public void setVariableValue(String name, Value value) {
        ExecutionFrame frameWithLocal = findFrameWithLocal(name);
        if (frameWithLocal != null) {
            frameWithLocal.setLocal(name, value);
        } else if (globalVariables.containsKey(name)) {
            setGlobal(name, value);
            return;
        } else if (currentFrame != null) {
            currentFrame.setLocal(name, value);
        } else {
            setGlobal(name, value);
            return;
        }

        mirrorLegacyVariable(name, value);
    }

    /**
     * Gets a variable value from locals or globals. If not found, falls back to legacy context
     * and converts it to a v2 Value (also caching it as a global for subsequent reads).
     */
    public Value getVariableValue(String name) {
        Value value = getLocalOrGlobal(name);
        if (value != null) {
            return value;
        }

        if (legacyContext != null) {
            Variable legacyVariable = legacyContext.getVariable(name);
            if (legacyVariable != null) {
                Value bridged = VariableBridge.toValue(legacyVariable);
                setGlobal(name, bridged);
                return bridged;
            }
        }

        return null;
    }

    /**
     * Exposes the global variable map for reads.
     */
    public Value getGlobal(String name) {
        return globalVariables.get(name);
    }

    /**
     * Stores a global variable value.
     */
    public void setGlobal(String name, Value value) {
        globalVariables.put(name, value);
        mirrorLegacyVariable(name, value);
    }

    /**
     * Returns a legacy Variable representation of a stored value if possible,
     * otherwise falls back to the wrapped legacy context.
     */
    public Variable getLegacyVariable(String name) {
        if (legacyContext == null) {
            return null;
        }

        Value stored = getLocalOrGlobal(name);
        if (stored != null) {
            return VariableBridge.toVariable(stored, name, legacyContext);
        }

        return legacyContext.getVariable(name);
    }

    private Value getLocalOrGlobal(String name) {
        Value local = getLocal(name);
        if (local != null) {
            return local;
        }
        return globalVariables.get(name);
    }

    private ExecutionFrame findFrameWithLocal(String name) {
        ExecutionFrame frame = currentFrame;
        while (frame != null) {
            if (frame.hasLocal(name)) {
                return frame;
            }
            frame = frame.getParent();
        }
        return null;
    }

    private void mirrorLegacyVariable(String name, Value value) {
        if (legacyContext == null) {
            return;
        }

        try {
            Variable variable = VariableBridge.toVariable(value, name, legacyContext);
            legacyContext.setVariable(name, variable);
        } catch (Exception ignored) {
            // Some values cannot be mirrored yet (e.g., behaviours without AST)
        }
    }

    /**
     * Generates a full stack trace as a string.
     * This is like Java stack traces but for AidemMedia code!
     */
    public String getStackTrace() {
        if (currentFrame == null) {
            return "<no stack trace available>";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Stack trace:\n");

        ExecutionFrame frame = currentFrame;
        while (frame != null) {
            sb.append(frame.toStackTraceString()).append("\n");
            frame = frame.getParent();
        }

        return sb.toString();
    }

    /**
     * Returns a compact stack trace (just the names and locations).
     */
    public String getCompactStackTrace() {
        if (currentFrame == null) {
            return "<empty>";
        }

        StringBuilder sb = new StringBuilder();
        ExecutionFrame frame = currentFrame;
        boolean first = true;

        while (frame != null) {
            if (!first) {
                sb.append(" > ");
            }
            sb.append(frame.getName())
              .append("@")
              .append(frame.getLocation().toCompact());
            first = false;
            frame = frame.getParent();
        }

        return sb.toString();
    }

    /**
     * Sets the maximum allowed stack depth.
     */
    public void setMaxStackDepth(int maxStackDepth) {
        if (maxStackDepth < 10) {
            throw new IllegalArgumentException("Max stack depth must be at least 10");
        }
        this.maxStackDepth = maxStackDepth;
    }

    /**
     * Returns the maximum allowed stack depth.
     */
    public int getMaxStackDepth() {
        return maxStackDepth;
    }

    @Override
    public String toString() {
        return "ExecutionContext[depth=" + getStackDepth() + ", trace=" + getCompactStackTrace() + "]";
    }
}
