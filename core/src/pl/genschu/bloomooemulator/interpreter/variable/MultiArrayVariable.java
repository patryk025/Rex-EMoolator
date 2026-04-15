package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;
import pl.genschu.bloomooemulator.loader.MultiArrayLoader;
import pl.genschu.bloomooemulator.saver.MultiArraySaver;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * MultiArrayVariable stores a multi-dimensional array as a flat Value[] array.
 *
 * TODO: Technically, original engine can allocate n-dim array, but conversion from indices to flat index is 2D only
 * TODO: maybe I'll add custom setting to enable only 2D addressing.
 */
public record MultiArrayVariable(
    String name,
    MutableState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    static final class MutableState {
        int[] dimensions;
        Value[] data;
        int totalElements;

        MutableState() {
            this.dimensions = new int[]{16, 16}; // 2D array, 16x16
            this.totalElements = 16 * 16;
            this.data = new Value[this.totalElements];
        }
    }

    public MultiArrayVariable(String name) {
        this(name, new MutableState(), Map.of());
    }

    public MultiArrayVariable {
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    // ========================================
    // INTERFACE IMPLEMENTATION
    // ========================================

    @Override
    public void init(Context context) {
        // DIMENSIONS (default: 2)
        int dimCount = 2;
        String dimensionsAttr = context.getAttribute(name, "DIMENSIONS");
        if (dimensionsAttr != null) {
            dimCount = Integer.parseInt(dimensionsAttr);
        }
        int[] dimensions = new int[dimCount];
        Arrays.fill(dimensions, 16);
        setDimensions(dimensions);
    }
    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.MULTIARRAY;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS_SPECS;
    }

    @Override
    public Map<String, SignalHandler> signals() {
        return signals;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler == null) {
            newSignals.remove(signalName);
        } else {
            newSignals.put(signalName, handler);
        }
        return new MultiArrayVariable(name, state, newSignals);
    }

    // ========================================
    // PUBLIC ACCESSORS FOR LOADER/SAVER
    // ========================================

    public int[] getDimensions() {
        return state.dimensions;
    }

    public Value[] getData() {
        return state.data;
    }

    public int getTotalElements() {
        return state.totalElements;
    }

    public void setDimensions(int[] dimensions) {
        state.dimensions = dimensions;
        state.totalElements = 1;
        for (int dim : dimensions) {
            state.totalElements *= dim;
        }
        state.data = new Value[state.totalElements];

        Gdx.app.log("MultiArrayVariable",
                String.format(Locale.getDefault(), "Initialized %dD array with dimensions %s (total: %d elements)",
                        dimensions.length, arrayToString(dimensions), state.totalElements));
    }

    public void setData(Value[] data) {
        if (data.length != state.totalElements) {
            Gdx.app.error("MultiArrayVariable",
                    String.format(Locale.getDefault(), "Data array size mismatch: got %d, expected %d",
                            data.length, state.totalElements));
            return;
        }
        state.data = data;
    }

    // ========================================
    // METHODS DEFINITION
    // ========================================

    private static final Map<String, MethodSpec> METHODS_SPECS = Map.ofEntries(
        Map.entry("GET", MethodSpec.of((self, args, ctx) -> {
            MultiArrayVariable thisVar = (MultiArrayVariable) self;
            int[] indices = thisVar.parseIndices(args);

            if (!thisVar.validateIndices(indices)) {
                Gdx.app.error("MultiArrayVariable", "Invalid indices for GET: " + thisVar.arrayToString(indices));
                return MethodResult.returns(NullValue.INSTANCE);
            }

            int flatIndex = thisVar.indicesToFlatIndex(indices);
            Value value = thisVar.state.data[flatIndex];
            return MethodResult.returns(value != null ? value : NullValue.INSTANCE);
        })),

        Map.entry("SET", MethodSpec.of((self, args, ctx) -> {
            MultiArrayVariable thisVar = (MultiArrayVariable) self;
            // Last argument is value
            List<Value> indexArgs = args.subList(0, args.size() - 1);
            int[] indices = thisVar.parseIndices(indexArgs);
            Value value = args.get(args.size() - 1);

            thisVar.ensureCapacity(indices);

            if (!thisVar.validateIndices(indices)) {
                Gdx.app.error("MultiArrayVariable", "Invalid indices for SET: " + thisVar.arrayToString(indices));
                return MethodResult.noReturn();
            }

            int flatIndex = thisVar.indicesToFlatIndex(indices);
            thisVar.state.data[flatIndex] = value;

            return MethodResult.noReturn();
        })),

        Map.entry("SAVE", MethodSpec.of((self, args, ctx) -> {
            MultiArrayVariable thisVar = (MultiArrayVariable) self;
            String path = ArgumentHelper.getString(args.get(0));
            String resolvedPath = FileUtils.resolveRelativePath(ctx.getGame(), path);
            MultiArraySaver.saveMultiArray(thisVar, resolvedPath);
            return MethodResult.noReturn();
        })),

        Map.entry("LOAD", MethodSpec.of((self, args, ctx) -> {
            MultiArrayVariable thisVar = (MultiArrayVariable) self;
            String path = ArgumentHelper.getString(args.get(0));
            String resolvedPath = FileUtils.resolveRelativePath(ctx.getGame(), path);
            MultiArrayLoader.loadMultiArray(thisVar, resolvedPath);
            return MethodResult.noReturn();
        })),

        Map.entry("GETSIZE", MethodSpec.of((self, args, ctx) -> {
            MultiArrayVariable thisVar = (MultiArrayVariable) self;
            int dim = ArgumentHelper.getInt(args.get(0));
            if (dim < 0 || dim >= thisVar.state.dimensions.length) {
                Gdx.app.error("MultiArrayVariable", "Invalid dimension index: " + dim);
                return MethodResult.returns(new IntValue(0));
            }
            return MethodResult.returns(new IntValue(thisVar.state.dimensions[dim]));
        }))
    );

    // ========================================
    // PRIVATE HELPERS
    // ========================================

    private int indicesToFlatIndex(int[] indices) {
        int flatIndex = 0;
        int multiplier = 1;

        // Iterate from right to left (rightmost dimension)
        for (int i = state.dimensions.length - 1; i >= 0; i--) {
            flatIndex += indices[i] * multiplier;
            multiplier *= state.dimensions[i];
        }

        return flatIndex;
    }

    private int[] flatIndexToIndices(int flatIndex) {
        int[] indices = new int[state.dimensions.length];
        int remaining = flatIndex;

        for (int i = state.dimensions.length - 1; i >= 0; i--) {
            indices[i] = remaining % state.dimensions[i];
            remaining /= state.dimensions[i];
        }

        return indices;
    }

    private boolean validateIndices(int[] indices) {
        if (indices.length != state.dimensions.length) {
            Gdx.app.error("MultiArrayVariable",
                    String.format(Locale.getDefault(), "Wrong number of indices: got %d, expected %d",
                            indices.length, state.dimensions.length));
            return false;
        }

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < 0 || indices[i] >= state.dimensions[i]) {
                Gdx.app.error("MultiArrayVariable",
                        String.format(Locale.getDefault(), "Index out of bounds at dimension %d: %d (max: %d)",
                                i, indices[i], state.dimensions[i] - 1));
                return false;
            }
        }

        return true;
    }

    private int[] parseIndices(List<Value> arguments) {
        int[] indices = new int[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            indices[arguments.size() - i - 1] = ArgumentHelper.getInt(arguments.get(i));
        }
        return indices;
    }

    private void ensureCapacity(int[] indices) {
        if (state.dimensions == null || state.dimensions.length == 0) {
            return;
        }

        int[] newDims = Arrays.copyOf(state.dimensions, state.dimensions.length);
        boolean resized = false;

        for (int i = 0; i < state.dimensions.length && i < indices.length; i++) {
            int coord = indices[i];
            if (coord < 0) {
                continue;
            }

            if (state.dimensions[i] <= coord) {
                while (newDims[i] <= coord) {
                    newDims[i] *= 2;
                }
                resized = true;
            }
        }

        if (!resized) {
            return;
        }

        int newTotal = 1;
        for (int d : newDims) {
            newTotal *= d;
        }

        Value[] newData = new Value[newTotal];

        if (state.totalElements > 0 && state.data != null) {
            if (state.dimensions.length == 1) {
                for (int oldIndex = 0; oldIndex < state.totalElements && oldIndex < newTotal; oldIndex++) {
                    Value v = state.data[oldIndex];
                    if (v != null) {
                        newData[oldIndex] = v;
                    }
                }
            } else {
                int dimCount = state.dimensions.length;

                for (int oldIndex = 0; oldIndex < state.totalElements; oldIndex++) {
                    Value v = state.data[oldIndex];
                    if (v == null) continue;

                    int[] oldIndices = flatIndexToIndices(oldIndex);

                    int newIndex = 0;
                    int multiplier = 1;
                    for (int i = dimCount - 1; i >= 0; i--) {
                        newIndex += oldIndices[i] * multiplier;
                        multiplier *= newDims[i];
                    }

                    if (newIndex >= 0 && newIndex < newTotal) {
                        newData[newIndex] = v;
                    } else {
                        Gdx.app.error("MultiArrayVariable",
                                String.format(Locale.getDefault(),
                                        "Reindex out of bounds during resize: oldIndex=%d -> newIndex=%d (newTotal=%d)",
                                        oldIndex, newIndex, newTotal));
                    }
                }
            }
        }

        state.data = newData;
        state.dimensions = newDims;
        state.totalElements = newTotal;

        Gdx.app.log("MultiArrayVariable",
                String.format(Locale.getDefault(),
                        "Resized MultiArray to dimensions %s (total: %d elements)",
                        arrayToString(newDims), newTotal));
    }

    private String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
