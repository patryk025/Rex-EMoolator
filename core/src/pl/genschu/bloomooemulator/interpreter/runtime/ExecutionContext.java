package pl.genschu.bloomooemulator.interpreter.runtime;

import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

/**
 * Execution context that manages the call stack and provides stack trace functionality.
 */
public class ExecutionContext {
    private ExecutionFrame currentFrame;  // Current execution frame
    private int maxStackDepth = 1000;     // Maximum stack depth before overflow

    public ExecutionContext() {
        this.currentFrame = null;
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
    public void setThis(Variable value) {
        if (currentFrame == null) {
            throw new IllegalStateException("No active execution frame");
        }
        currentFrame.setThis(value);
    }

    /**
     * Gets the nearest "this" value from the current frame upwards.
     */
    public Variable getThis() {
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
    public void setLocal(String name, Variable variable) {
        if (currentFrame == null) {
            throw new IllegalStateException("No active execution frame");
        }
        currentFrame.setLocal(name, variable);
    }

    /**
     * Gets a local variable from the current frame or any parent frame.
     * Returns null if not found.
     */
    public Variable getLocal(String name) {
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
     * Updates a local variable in the frame where it was originally defined.
     * Searches from the current frame upwards.
     *
     * @return true if the variable was found and updated
     */
    public boolean updateLocal(String name, Variable variable) {
        ExecutionFrame frame = currentFrame;
        while (frame != null) {
            if (frame.hasLocal(name)) {
                frame.setLocal(name, variable);
                return true;
            }
            frame = frame.getParent();
        }
        return false;
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
