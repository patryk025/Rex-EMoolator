package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.render.RenderOrder;
import pl.genschu.bloomooemulator.geometry.coordinates.CanvasRect;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;

import java.util.*;

/**
 * PatternVariable is a layered grid of graphics.
 *
 * Tiles remember a cell, never a pixel position: a tile sits at
 * {@code board position + grid * cell}, so MOVE and SETPOSITION shift the whole
 * board at once. Each layer also keeps its tiles in insertion order, which is
 * the order they are searched in.
 *
 * The original does no bounds checking and crashes on a bad cell or layer. Here
 * such calls are logged and ignored.
 */
public record PatternVariable(
    String name,
    @InternalMutable PatternState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    private static final String UNKNOWN_GRAPHICS = "UNKNOWN";
    private static final String NO_ALIAS = "NULL";

    /** A cell's content. */
    public static final class Tile {
        public final String alias;
        public final int column;
        public final int row;
        public final int layer;
        public final Variable graphic;
        /** Stored by the original as well, and read by nothing there. */
        public final int extra;

        Tile(String alias, int column, int row, int layer, Variable graphic, int extra) {
            this.alias = alias;
            this.column = column;
            this.row = row;
            this.layer = layer;
            this.graphic = graphic;
            this.extra = extra;
        }
    }

    public static final class PatternState {
        public int width;
        public int height;
        public int gridX = 1;
        public int gridY = 1;
        public int posX;
        public int posY;
        public int priority;
        public boolean visible;
        public boolean toCanvas;
        public final long renderOrder = RenderOrder.next();

        /** Fixed at creation: SETGRID changes the spacing, not the table. */
        private Tile[][][] cells = new Tile[1][0][0];
        private List<List<Tile>> layers = List.of(new ArrayList<>());

        void allocate(int width, int height, int gridX, int gridY, int layerCount) {
            this.width = width;
            this.height = height;
            // CMC_Pattern turns a zero grid into 1. LAYERS is empty in most scripts,
            // and both empty and 0 behave as a single layer.
            this.gridX = gridX == 0 ? 1 : gridX;
            this.gridY = gridY == 0 ? 1 : gridY;
            int count = Math.max(layerCount, 1);
            int columns = Math.max(width / this.gridX, 0);
            int rows = Math.max(height / this.gridY, 0);
            cells = new Tile[count][columns][rows];
            List<List<Tile>> created = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                created.add(new ArrayList<>());
            }
            layers = created;
        }

        public int layerCount() {
            return layers.size();
        }

        /** Tiles of a layer in insertion order. */
        public List<Tile> tiles(int layer) {
            return Collections.unmodifiableList(layers.get(layer));
        }

        boolean isValidCell(int column, int row, int layer) {
            return layer >= 0 && layer < cells.length
                && column >= 0 && column < cells[layer].length
                && row >= 0 && row < cells[layer][column].length;
        }

        Tile tileAt(int column, int row, int layer) {
            return isValidCell(column, row, layer) ? cells[layer][column][row] : null;
        }

        void put(Tile tile) {
            Tile previous = cells[tile.layer][tile.column][tile.row];
            if (previous != null) {
                layers.get(tile.layer).remove(previous);
            }
            cells[tile.layer][tile.column][tile.row] = tile;
            layers.get(tile.layer).add(tile);
        }

        void remove(Tile tile) {
            cells[tile.layer][tile.column][tile.row] = null;
            layers.get(tile.layer).remove(tile);
        }

        /**
         * CPattern::remove(CXString) keeps the count it read before the loop and
         * removes from the vector while walking it, so the tile after each removed
         * one is skipped, and the slots past the new end still hold the last pointer.
         * With four tiles under one alias, the second survives. Measured on PIKLIB8.
         */
        void removeAlias(String alias) {
            for (List<Tile> layer : layers) {
                Tile[] slots = layer.toArray(new Tile[0]);
                int live = slots.length;
                for (Tile tile : slots) {
                    if (cells[tile.layer][tile.column][tile.row] != tile || !tile.alias.equals(alias)) {
                        continue;
                    }
                    remove(tile);
                    int index = Arrays.asList(slots).subList(0, live).indexOf(tile);
                    System.arraycopy(slots, index + 1, slots, index, live - index - 1);
                    live--;
                }
            }
        }

        Tile findAlias(String alias) {
            for (List<Tile> layer : layers) {
                for (Tile tile : layer) {
                    if (tile.alias.equals(alias)) {
                        return tile;
                    }
                }
            }
            return null;
        }
    }

    public PatternVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new PatternState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public PatternVariable(String name) {
        this(name, new PatternState(), Map.of());
    }

    @Override
    public void init(Context context) {
        state.allocate(
            intAttribute(context, "WIDTH"),
            intAttribute(context, "HEIGHT"),
            intAttribute(context, "GRIDX"),
            intAttribute(context, "GRIDY"),
            intAttribute(context, "LAYERS"));
        state.priority = intAttribute(context, "PRIORITY");
        state.visible = "TRUE".equalsIgnoreCase(context.attributes().get(name, "VISIBLE"));
        state.toCanvas = "TRUE".equalsIgnoreCase(context.attributes().get(name, "TOCANVAS"));
    }

    private int intAttribute(Context context, String attribute) {
        String value = context.attributes().get(name, attribute);
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            Gdx.app.error("PatternVariable", name + ": invalid " + attribute + ": " + value);
            return 0;
        }
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.PATTERN;
    }

    @Override
    public Variable withValue(Value newValue) {
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
        return new PatternVariable(name, state, newSignals);
    }

    // ========================================
    // RENDERING
    // ========================================

    public int getPriority() { return state.priority; }
    public long getRenderOrder() { return state.renderOrder; }

    /** Whether the screen list draws this board at all. */
    public boolean isDrawn() {
        return state.visible && state.toCanvas;
    }

    /**
     * Where a tile lands on the canvas, or null when its graphic has no bounds yet.
     */
    public CanvasRect tileBounds(Tile tile) {
        CanvasRect bounds = localBounds(tile);
        return bounds != null ? bounds.translated(state.posX, state.posY) : null;
    }

    public static boolean isTileVisible(Tile tile) {
        return isVisible(tile);
    }

    // ========================================
    // HIT TESTING
    // ========================================

    /** Bounds the graphic gets when placed in its cell, in board coordinates. */
    private CanvasRect localBounds(Tile tile) {
        int cellX = state.gridX * tile.column;
        int cellY = state.gridY * tile.row;
        if (tile.graphic instanceof ImageVariable image) {
            var graphic = image.state();
            return graphic.rect == null ? null : graphic.rect.translated(
                cellX - graphic.anchorX - graphic.posX,
                cellY - graphic.anchorY - graphic.posY);
        }
        if (tile.graphic instanceof AnimoVariable animo) {
            var graphic = animo.state();
            return graphic.rect == null ? null : graphic.rect.translated(
                cellX - graphic.anchorX - graphic.posX,
                cellY - graphic.anchorY - graphic.posY);
        }
        return null;
    }

    private static boolean isVisible(Tile tile) {
        if (tile.graphic instanceof ImageVariable image) {
            return image.isVisible();
        }
        return tile.graphic instanceof AnimoVariable animo && animo.isVisible();
    }

    private static int alphaAt(Tile tile, int x, int y) {
        if (tile.graphic instanceof ImageVariable image) {
            return image.getAlpha(x, y);
        }
        return tile.graphic instanceof AnimoVariable animo ? animo.getAlpha(x, y) : 0;
    }

    private Tile hitTest(Tile tile, int x, int y, boolean includeHidden, boolean ignoreAlpha,
                         int alphaThreshold, MethodContext ctx) {
        if (!includeHidden && !isVisible(tile)) {
            return null;
        }
        CanvasRect bounds = localBounds(tile);
        int localX = x - state.posX;
        int localY = y - state.posY;
        if (bounds == null || !bounds.contains(localX, localY)) {
            return null;
        }
        if (ignoreAlpha) {
            // The original moves the graphic into its cell for the test and returns
            // from here without moving it back, so it stays at the board-local cell
            // position. Observable through GETPOSITIONX and on-canvas tiles.
            tile.graphic.callMethod("SETPOSITION",
                List.of(new IntValue(state.gridX * tile.column), new IntValue(state.gridY * tile.row)), ctx);
            return tile;
        }
        return alphaAt(tile, localX - bounds.left(), localY - bounds.top()) > alphaThreshold ? tile : null;
    }

    /** From the top layer down. */
    private Tile findGraphics(int x, int y, boolean includeHidden, boolean ignoreAlpha,
                              int layer, int alphaThreshold, MethodContext ctx) {
        int top = state.layerCount() - 1;
        int first = layer < 0 ? top : layer;
        if (first > top) {
            Gdx.app.error("PatternVariable", name + ": layer out of range: " + first);
            return null;
        }
        // Only asking for the top layer searches them all; any other asks for that one.
        int last = first == top ? 0 : first;
        for (int current = first; current >= last; current--) {
            for (Tile tile : state.tiles(current)) {
                Tile hit = hitTest(tile, x, y, includeHidden, ignoreAlpha, alphaThreshold, ctx);
                if (hit != null) {
                    return hit;
                }
            }
        }
        return null;
    }

    /** From layer 0 up. */
    private Tile findAliasTile(int x, int y, boolean includeHidden, boolean ignoreAlpha, MethodContext ctx) {
        for (int layer = 0; layer < state.layerCount(); layer++) {
            for (Tile tile : state.tiles(layer)) {
                Tile hit = hitTest(tile, x, y, includeHidden, ignoreAlpha, 0, ctx);
                if (hit != null) {
                    return hit;
                }
            }
        }
        return null;
    }

    private ArrayVariable targetArray(Value arrayName, MethodContext ctx) {
        String target = ArgumentHelper.getString(arrayName);
        if (ctx.getVariable(target) instanceof ArrayVariable array) {
            return array;
        }
        Gdx.app.error("PatternVariable", name + ": not an ARRAY: " + target);
        return null;
    }

    private void appendInfo(Tile tile, ArrayVariable array) {
        CanvasRect bounds = localBounds(tile);
        int width = bounds != null ? bounds.width() : 0;
        int height = bounds != null ? bounds.height() : 0;
        Collections.addAll(array.elements(),
            new StringValue(tile.alias),
            new IntValue(tile.column),
            new IntValue(tile.row),
            new IntValue(width),
            new IntValue(height),
            new IntValue(state.posX + state.gridX * tile.column + width / 2),
            new IntValue(state.posY + state.gridY * tile.row + height / 2));
    }

    // ========================================
    // METHODS
    // ========================================

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("ADD", MethodSpec.of((self, args, ctx) -> {
            PatternVariable pattern = (PatternVariable) self;
            String alias = ArgumentHelper.getString(args.get(0));
            int column = ArgumentHelper.getInt(args.get(1));
            int row = ArgumentHelper.getInt(args.get(2));
            String graphicName = ArgumentHelper.getString(args.get(3));
            int extra = ArgumentHelper.getInt(args, 4, 0);
            int layer = ArgumentHelper.getInt(args, 5, 0);

            Variable graphic = ctx.getVariable(graphicName);
            if (!(graphic instanceof ImageVariable) && !(graphic instanceof AnimoVariable)) {
                Gdx.app.error("PatternVariable", pattern.name + ": not a graphics object: " + graphicName);
                return MethodResult.noReturn();
            }
            if (!pattern.state.isValidCell(column, row, layer)) {
                Gdx.app.error("PatternVariable", pattern.name + ": cell out of range: "
                    + column + "," + row + " layer " + layer);
                return MethodResult.noReturn();
            }
            pattern.state.put(new Tile(alias, column, row, layer, graphic, extra));
            return MethodResult.noReturn();
        })),

        Map.entry("GETALIASAT", MethodSpec.of((self, args, ctx) -> {
            PatternVariable pattern = (PatternVariable) self;
            Tile tile = pattern.findAliasTile(
                ArgumentHelper.getInt(args.get(0)),
                ArgumentHelper.getInt(args.get(1)),
                ArgumentHelper.getBoolean(args.get(2)),
                ArgumentHelper.getBoolean(args.get(3)),
                ctx);
            return MethodResult.returns(new StringValue(tile != null ? tile.alias : NO_ALIAS));
        })),

        Map.entry("GETALIASATCELL", MethodSpec.of((self, args, ctx) -> {
            PatternVariable pattern = (PatternVariable) self;
            Tile tile = pattern.state.tileAt(
                ArgumentHelper.getInt(args.get(0)),
                ArgumentHelper.getInt(args.get(1)),
                ArgumentHelper.getInt(args, 2, 0));
            return MethodResult.returns(new StringValue(tile != null ? tile.alias : NO_ALIAS));
        })),

        Map.entry("GETALIASINFO", MethodSpec.of((self, args, ctx) -> {
            PatternVariable pattern = (PatternVariable) self;
            Tile tile = pattern.state.findAlias(ArgumentHelper.getString(args.get(0)));
            ArrayVariable array = pattern.targetArray(args.get(1), ctx);
            if (tile != null && array != null) {
                pattern.appendInfo(tile, array);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("GETCELLCORD", MethodSpec.of((self, args, ctx) -> {
            PatternVariable pattern = (PatternVariable) self;
            PatternState state = pattern.state;
            int x = ArgumentHelper.getInt(args.get(0)) - state.posX;
            int y = ArgumentHelper.getInt(args.get(1)) - state.posY;
            ArrayVariable array = pattern.targetArray(args.get(2), ctx);
            if (array != null) {
                // Written even for a point outside the board.
                Collections.addAll(array.elements(),
                    new IntValue(x / state.gridX), new IntValue(y / state.gridY));
            }
            // Both far edges count as inside.
            return MethodResult.returns(BoolValue.of(
                x >= 0 && x <= state.width && y >= 0 && y <= state.height));
        })),

        Map.entry("GETGRAPHICSAT", MethodSpec.of((self, args, ctx) -> {
            PatternVariable pattern = (PatternVariable) self;
            Tile tile = pattern.findGraphics(
                ArgumentHelper.getInt(args.get(0)),
                ArgumentHelper.getInt(args.get(1)),
                ArgumentHelper.getBoolean(args.get(2)),
                ArgumentHelper.getBoolean(args.get(3)),
                ArgumentHelper.getInt(args, 4, -1),
                ArgumentHelper.getInt(args, 5, 0),
                ctx);
            return MethodResult.returns(new StringValue(tile != null ? tile.graphic.name() : UNKNOWN_GRAPHICS));
        })),

        Map.entry("GETINFOAT", MethodSpec.of((self, args, ctx) -> {
            PatternVariable pattern = (PatternVariable) self;
            ArrayVariable array = pattern.targetArray(args.get(4), ctx);
            Tile tile = pattern.findAliasTile(
                ArgumentHelper.getInt(args.get(0)),
                ArgumentHelper.getInt(args.get(1)),
                ArgumentHelper.getBoolean(args.get(2)),
                ArgumentHelper.getBoolean(args.get(3)),
                ctx);
            if (tile != null && array != null) {
                pattern.appendInfo(tile, array);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("MOVE", MethodSpec.of((self, args, ctx) -> {
            PatternState state = ((PatternVariable) self).state;
            state.posX += ArgumentHelper.getInt(args.get(0));
            state.posY += ArgumentHelper.getInt(args.get(1));
            return MethodResult.noReturn();
        })),

        Map.entry("REMOVE", MethodSpec.of((self, args, ctx) -> {
            PatternVariable pattern = (PatternVariable) self;
            if (args.get(0) instanceof IntValue column) {
                Tile tile = pattern.state.tileAt(
                    column.value(),
                    ArgumentHelper.getInt(args.get(1)),
                    ArgumentHelper.getInt(args, 2, 0));
                if (tile != null) {
                    pattern.state.remove(tile);
                }
            } else {
                pattern.state.removeAlias(ArgumentHelper.getString(args.get(0)));
            }
            return MethodResult.noReturn();
        })),

        // Scripts call this, but the original has no such PATTERN method and an unknown
        // method name does nothing. ADD overwriting the same cells hides that in game.
        Map.entry("REMOVEALL", MethodSpec.of((self, args, ctx) -> MethodResult.noReturn())),

        Map.entry("SETGRID", MethodSpec.of((self, args, ctx) -> {
            PatternState state = ((PatternVariable) self).state;
            state.gridX = ArgumentHelper.getInt(args.get(0));
            state.gridY = ArgumentHelper.getInt(args.get(1));
            return MethodResult.noReturn();
        })),

        Map.entry("SETPOSITION", MethodSpec.of((self, args, ctx) -> {
            PatternState state = ((PatternVariable) self).state;
            state.posX = ArgumentHelper.getInt(args.get(0));
            state.posY = ArgumentHelper.getInt(args.get(1));
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "PatternVariable[" + name + "]";
    }
}
