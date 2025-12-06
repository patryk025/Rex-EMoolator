package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.MultiArrayLoader;
import pl.genschu.bloomooemulator.saver.MultiArraySaver;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MultiArrayVariable extends Variable {
    // TODO: Technically, original engine can allocate n-dim array, but conversion from indices to flat index are 2D only
    // TODO: maybe I'll add custom setting for enable only 2D addressing.
    private Variable[] data;     // Flat array storage
    private int[] dimensions;    // Dimensions sizes
    private int totalElements;   // Total number of elements

    public MultiArrayVariable(String name, Context context) {
        super(name, context);
        this.dimensions = new int[]{1};  // Default 1D array
        this.totalElements = 1;
        this.data = new Variable[1];
    }

    @Override
    public String getType() {
        return "MULTIARRAY";
    }

    @Override
    protected void setMethods() {
        super.setMethods();

        this.setMethod("GET", new Method(
                List.of(
                        new Parameter("INTEGER", "x", true),
                        new Parameter("INTEGER", "y", false),
                        new Parameter("INTEGER", "z...", false)
                ),
                "mixed"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                int[] indices = parseIndices(arguments);

                if (!validateIndices(indices)) {
                    Gdx.app.error("MultiArrayVariable", "Invalid indices for GET: " + arrayToString(indices));
                    return new StringVariable("", "NULL", context);
                }

                int flatIndex = indicesToFlatIndex(indices);
                Variable value = data[flatIndex];

                return Objects.requireNonNullElseGet(value, () -> new StringVariable("", "NULL", context));
            }
        });

        this.setMethod("SET", new Method(
                List.of(
                        new Parameter("INTEGER", "x", true),
                        new Parameter("INTEGER", "y", false),
                        new Parameter("INTEGER", "z...", false),
                        new Parameter("mixed", "value", true)
                ),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                // Last argument is value
                int[] indices = parseIndices(arguments.subList(0, arguments.size() - 1));
                Variable value = (Variable) arguments.get(arguments.size() - 1);

                ensureCapacity(indices);

                if (!validateIndices(indices)) {
                    Gdx.app.error("MultiArrayVariable", "Invalid indices for SET: " + arrayToString(indices));
                    return null;
                }

                int flatIndex = indicesToFlatIndex(indices);
                data[flatIndex] = value.clone();

                return null;
            }
        });

        this.setMethod("SAVE", new Method(
                List.of(
                        new Parameter("STRING", "path", true)
                ),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                String path = ArgumentsHelper.getString(arguments.get(0));
                MultiArraySaver.saveMultiArray(MultiArrayVariable.this, path);
                return null;
            }
        });

        this.setMethod("LOAD", new Method(
                List.of(
                        new Parameter("STRING", "path", true)
                ),
                "void"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                String path = ArgumentsHelper.getString(arguments.get(0));
                MultiArrayLoader.loadMultiArray(MultiArrayVariable.this, path);
                return null;
            }
        });

        this.setMethod("GETSIZE", new Method(
                List.of(
                        new Parameter("INTEGER", "dimension", true)
                ),
                "INTEGER"
        ) {
            @Override
            public Variable execute(List<Object> arguments) {
                // Return size of one dimension
                int dim = ArgumentsHelper.getInteger(arguments.get(0));
                if (dim < 0 || dim >= dimensions.length) {
                    Gdx.app.error("MultiArrayVariable", "Invalid dimension index: " + dim);
                    return new IntegerVariable("", 0, context);
                }
                return new IntegerVariable("", dimensions[dim], context);
            }
        });
    }

    @Override
    public void setAttribute(String name, Attribute attribute) {
        if (name.equals("DIMENSIONS")) {
            int dimensionsCount = Integer.parseInt(attribute.getValue().toString());
            int[] dims = new int[dimensionsCount];

            // Fill every dimension with default 16 elements size
            Arrays.fill(dims, 16);

            setDimensions(dims);

            Gdx.app.log("MultiArrayVariable",
                    String.format(Locale.getDefault(), "Initialized %dD array with default dimensions [16]^%d",
                            dimensionsCount, dimensionsCount));
        } else {
            super.setAttribute(name, attribute);
        }
    }

    private int indicesToFlatIndex(int[] indices) {
        int flatIndex = 0;
        int multiplier = 1;

        // Iterate from right to left (rightmost dimension)
        for (int i = dimensions.length - 1; i >= 0; i--) {
            flatIndex += indices[i] * multiplier;
            multiplier *= dimensions[i];
        }

        return flatIndex;
    }

    private int[] flatIndexToIndices(int flatIndex) {
        int[] indices = new int[dimensions.length];
        int remaining = flatIndex;

        for (int i = dimensions.length - 1; i >= 0; i--) {
            indices[i] = remaining % dimensions[i];
            remaining /= dimensions[i];
        }

        return indices;
    }

    private boolean validateIndices(int[] indices) {
        if (indices.length != dimensions.length) {
            Gdx.app.error("MultiArrayVariable",
                    String.format(Locale.getDefault(), "Wrong number of indices: got %d, expected %d",
                            indices.length, dimensions.length));
            return false;
        }

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < 0 || indices[i] >= dimensions[i]) {
                Gdx.app.error("MultiArrayVariable",
                        String.format(Locale.getDefault(), "Index out of bounds at dimension %d: %d (max: %d)",
                                i, indices[i], dimensions[i] - 1));
                return false;
            }
        }

        return true;
    }

    private int[] parseIndices(List<Object> arguments) {
        int[] indices = new int[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            indices[arguments.size() - i - 1] = ArgumentsHelper.getInteger(arguments.get(i));
        }
        return indices;
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

    // === Public setters dla loadera ===
    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;

        // Oblicz całkowitą liczbę elementów
        this.totalElements = 1;
        for (int dim : dimensions) {
            this.totalElements *= dim;
        }

        // Zaalokuj flat array
        this.data = new Variable[totalElements];

        Gdx.app.log("MultiArrayVariable",
                String.format(Locale.getDefault(), "Initialized %dD array with dimensions %s (total: %d elements)",
                        dimensions.length, arrayToString(dimensions), totalElements));
    }

    public void setData(Variable[] data) {
        if (data.length != totalElements) {
            Gdx.app.error("MultiArrayVariable",
                    String.format(Locale.getDefault(), "Data array size mismatch: got %d, expected %d",
                            data.length, totalElements));
            return;
        }
        this.data = data;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public Variable[] getData() {
        return data;
    }

    public int getTotalElements() {
        return totalElements;
    }

    private void ensureCapacity(int[] indices) {
        if (dimensions == null || dimensions.length == 0) {
            return;
        }

        int[] newDims = Arrays.copyOf(dimensions, dimensions.length);
        boolean resized = false;

        for (int i = 0; i < dimensions.length && i < indices.length; i++) {
            int coord = indices[i];
            if (coord < 0) {
                continue;
            }

            if (dimensions[i] <= coord) {
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

        Variable[] newData = new Variable[newTotal];

        if (totalElements > 0 && data != null) {
            if (dimensions.length == 1) {
                for (int oldIndex = 0; oldIndex < totalElements && oldIndex < newTotal; oldIndex++) {
                    Variable v = data[oldIndex];
                    if (v != null) {
                        newData[oldIndex] = v;
                    }
                }
            } else {
                int dimCount = dimensions.length;

                for (int oldIndex = 0; oldIndex < totalElements; oldIndex++) {
                    Variable v = data[oldIndex];
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

        this.data = newData;
        this.dimensions = newDims;
        this.totalElements = newTotal;

        Gdx.app.log("MultiArrayVariable",
                String.format(Locale.getDefault(),
                        "Resized MultiArray to dimensions %s (total: %d elements)",
                        arrayToString(newDims), newTotal));
    }
}