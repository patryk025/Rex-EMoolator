package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.capabilities.Initializable;

import java.util.*;

/**
 * MatrixVariable represents a tile-based game field (used in Reksio i Ufo).
 * Contains a grid of integer cell codes with physics-like stone falling and enemy pathfinding.
 *
 * Uses mutable MatrixState for the grid data and pending moves queue.
 */
public record MatrixVariable(
    String name,
    @InternalMutable MatrixState state,
    Map<String, SignalHandler> signals
) implements Variable, Initializable {

    // Field codes
    public static final int FIELD_CODE_MOLE = 99;
    public static final int FIELD_CODE_EMPTY = 0;
    public static final int FIELD_CODE_GROUND = 1;
    public static final int FIELD_CODE_STONE = 2;
    public static final int FIELD_CODE_DYNAMITE = 3;
    public static final int FIELD_CODE_WALL_WEAK = 4;
    public static final int FIELD_CODE_ENEMY = 5;
    public static final int FIELD_CODE_WALL_STRONG = 6;
    public static final int FIELD_CODE_DYNAMITE_FIRED = 7;
    public static final int FIELD_CODE_EXPLOSION = 8;
    public static final int FIELD_CODE_EXIT = 9;

    // Movement directions
    public static final int MOVEMENT_DOWN = 1;
    public static final int MOVEMENT_DOWN_LEFT = 2;
    public static final int MOVEMENT_DOWN_RIGHT = 3;
    public static final int MOVEMENT_EXPLOSION = 4;

    public static final class MatrixState {
        public int width;
        public int height;
        public int cellWidth;
        public int cellHeight;
        public int basePosX;
        public int basePosY;
        public int[] data;

        public int startGateColumn = -1;
        public int startGateRow = -1;
        public int endGateColumn = -1;
        public int endGateRow = -1;

        public final List<int[]> pendingMoves = new ArrayList<>();
        public int currentMoveIndex = -1;

        public MatrixState() {}

        public void initGrid(int width, int height) {
            this.width = width;
            this.height = height;
            this.data = new int[width * height];
        }

        public int getCell(int idx) {
            if (data != null && idx >= 0 && idx < data.length) return data[idx];
            return 0;
        }

        public int getCell(int x, int y) {
            return getCell(y * width + x);
        }

        public void setCell(int idx, int value) {
            if (data != null && idx >= 0 && idx < data.length) data[idx] = value;
        }

        public int getCellIndex(int srcIdx, int direction) {
            return switch (direction) {
                case MOVEMENT_DOWN -> getCellIndex(srcIdx, 0, 1);
                case MOVEMENT_DOWN_LEFT -> getCellIndex(srcIdx, -1, 1);
                case MOVEMENT_DOWN_RIGHT -> getCellIndex(srcIdx, 1, 1);
                default -> srcIdx;
            };
        }

        public int getCellIndex(int srcIdx, int offsetX, int offsetY) {
            int x = srcIdx % width;
            int y = srcIdx / width;
            return (y + offsetY) * width + (x + offsetX);
        }

        public boolean isInGate(int cellIndex) {
            if (startGateColumn == -1) return false;
            int row = cellIndex / width;
            int col = cellIndex % width;
            return row >= startGateRow && row <= endGateRow &&
                   col >= startGateColumn && col <= endGateColumn;
        }

        public MatrixState copy() {
            MatrixState copy = new MatrixState();
            copy.width = this.width;
            copy.height = this.height;
            copy.cellWidth = this.cellWidth;
            copy.cellHeight = this.cellHeight;
            copy.basePosX = this.basePosX;
            copy.basePosY = this.basePosY;
            copy.data = this.data != null ? this.data.clone() : null;
            copy.startGateColumn = this.startGateColumn;
            copy.startGateRow = this.startGateRow;
            copy.endGateColumn = this.endGateColumn;
            copy.endGateRow = this.endGateRow;
            return copy;
        }

        public void dispose() {
            pendingMoves.clear();
            data = null;
        }
    }

    public MatrixVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (state == null) {
            state = new MatrixState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public MatrixVariable(String name) {
        this(name, new MatrixState(), Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.MATRIX;
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
        return new MatrixVariable(name, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new MatrixVariable(newName, state.copy(), new HashMap<>(signals));
    }

    @Override
    public void init(Context context) {
        initAttributesFromContext(context);
    }

    private void initAttributesFromContext(Context context) {
        // SIZE: "W,H" → also allocates the data grid
        String sizeAttr = context.getAttribute(name, "SIZE");
        if (sizeAttr != null) {
            String[] dimensions = sizeAttr.split(",");
            if (dimensions.length == 2) {
                try {
                    int width = Integer.parseInt(dimensions[0].trim());
                    int height = Integer.parseInt(dimensions[1].trim());
                    state.initGrid(width, height);
                } catch (NumberFormatException e) {
                    Gdx.app.error("MatrixVariable", name + ": invalid SIZE attribute: " + sizeAttr);
                }
            }
        }

        // BASEPOS: "X,Y"
        String basePosAttr = context.getAttribute(name, "BASEPOS");
        if (basePosAttr != null) {
            String[] basePos = basePosAttr.split(",");
            if (basePos.length == 2) {
                try {
                    state.basePosX = Integer.parseInt(basePos[0].trim());
                    state.basePosY = Integer.parseInt(basePos[1].trim());
                } catch (NumberFormatException e) {
                    Gdx.app.error("MatrixVariable", name + ": invalid BASEPOS attribute: " + basePosAttr);
                }
            }
        }

        // CELLWIDTH
        String cellWidthAttr = context.getAttribute(name, "CELLWIDTH");
        if (cellWidthAttr != null) {
            try { state.cellWidth = Integer.parseInt(cellWidthAttr.trim()); }
            catch (NumberFormatException e) {
                Gdx.app.error("MatrixVariable", name + ": invalid CELLWIDTH attribute: " + cellWidthAttr);
            }
        }

        // CELLHEIGHT
        String cellHeightAttr = context.getAttribute(name, "CELLHEIGHT");
        if (cellHeightAttr != null) {
            try { state.cellHeight = Integer.parseInt(cellHeightAttr.trim()); }
            catch (NumberFormatException e) {
                Gdx.app.error("MatrixVariable", name + ": invalid CELLHEIGHT attribute: " + cellHeightAttr);
            }
        }
    }

    // ========================================
    // METHODS
    // ========================================

    @SuppressWarnings("DuplicatedCode")
    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("CALCENEMYMOVEDEST", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;
            int oldCell = ArgumentHelper.getInt(args.get(0));
            int direction = ArgumentHelper.getInt(args.get(1));
            int row = oldCell / s.width;
            int col = oldCell % s.width;
            int newRow = row, newCol = col;

            switch (direction) {
                case 0 -> newCol = Math.max(0, col - 1);
                case 1 -> newRow = Math.max(0, row - 1);
                case 2 -> newCol = Math.min(s.width - 1, col + 1);
                case 3 -> newRow = Math.min(s.height - 1, row + 1);
            }

            if (newRow == row && newCol == col) return MethodResult.returns(new IntValue(oldCell));

            int destCell = newRow * s.width + newCol;
            if (destCell >= 0 && destCell < s.data.length) {
                int cellCode = s.data[destCell];
                if (cellCode == FIELD_CODE_EMPTY || cellCode == FIELD_CODE_MOLE) {
                    return MethodResult.returns(new IntValue(destCell));
                }
            }
            return MethodResult.returns(new IntValue(oldCell));
        })),

        Map.entry("CALCENEMYMOVEDIR", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;
            int oldCell = ArgumentHelper.getInt(args.get(0));
            int oldDir = args.size() > 1 ? ArgumentHelper.getInt(args.get(1)) : 0;
            int row = oldCell / s.width;
            int col = oldCell % s.width;

            int[] directions = {(oldDir + 3) % 4, oldDir, (oldDir + 1) % 4, (oldDir + 2) % 4};

            for (int newDir : directions) {
                int newRow = row, newCol = col;
                switch (newDir) {
                    case 0 -> newCol = Math.max(0, col - 1);
                    case 1 -> newRow = Math.max(0, row - 1);
                    case 2 -> newCol = Math.min(s.width - 1, col + 1);
                    case 3 -> newRow = Math.min(s.height - 1, row + 1);
                }
                if (newRow == row && newCol == col) continue;

                int destCell = newRow * s.width + newCol;
                if (destCell >= 0 && destCell < s.data.length) {
                    int cellCode = s.data[destCell];
                    if (cellCode == FIELD_CODE_EMPTY || cellCode == FIELD_CODE_MOLE) {
                        return MethodResult.returns(new IntValue(newDir));
                    }
                }
            }
            return MethodResult.returns(new IntValue(directions[0]));
        })),

        Map.entry("CANHEROGOTO", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;
            int cellNo = ArgumentHelper.getInt(args.get(0));

            if (cellNo >= 0 && s.data != null && cellNo < s.data.length) {
                if (s.isInGate(cellNo)) return MethodResult.returns(new BoolValue(false));
                int cellCode = s.data[cellNo];
                boolean canGo = cellCode != FIELD_CODE_WALL_WEAK &&
                                cellCode != FIELD_CODE_WALL_STRONG &&
                                cellCode != FIELD_CODE_ENEMY &&
                                cellCode != FIELD_CODE_STONE;
                return MethodResult.returns(new BoolValue(canGo));
            }
            return MethodResult.returns(new BoolValue(false));
        })),

        Map.entry("GET", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int cellIndex = ArgumentHelper.getInt(args.get(0));
            if (cellIndex >= 0 && m.state.data != null && cellIndex < m.state.data.length) {
                return MethodResult.returns(new IntValue(m.state.data[cellIndex]));
            }
            return MethodResult.returns(new IntValue(0));
        })),

        Map.entry("GETCELLOFFSET", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int x = ArgumentHelper.getInt(args.get(0));
            int y = ArgumentHelper.getInt(args.get(1));
            if (x >= 0 && x < m.state.width && y >= 0 && y < m.state.height) {
                return MethodResult.returns(new IntValue(y * m.state.width + x));
            }
            return MethodResult.returns(new IntValue(-1));
        })),

        Map.entry("GETCELLPOSX", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int cellIndex = ArgumentHelper.getInt(args.get(0));
            int col = cellIndex % m.state.width;
            return MethodResult.returns(new IntValue(m.state.basePosX + col * m.state.cellWidth));
        })),

        Map.entry("GETCELLPOSY", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int cellIndex = ArgumentHelper.getInt(args.get(0));
            int row = cellIndex / m.state.width;
            return MethodResult.returns(new IntValue(m.state.basePosY + row * m.state.cellHeight));
        })),

        Map.entry("GETCELLSNO", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int cellCode = ArgumentHelper.getInt(args.get(0));
            int count = 0;
            if (m.state.data != null) {
                for (int val : m.state.data) {
                    if (val == cellCode) count++;
                }
            }
            return MethodResult.returns(new IntValue(count));
        })),

        Map.entry("GETFIELDPOSX", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int cellIndex = ArgumentHelper.getInt(args.get(0));
            int col = cellIndex % m.state.width;
            return MethodResult.returns(new IntValue(m.state.basePosX + col * m.state.cellWidth));
        })),

        Map.entry("GETFIELDPOSY", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int cellIndex = ArgumentHelper.getInt(args.get(0));
            int row = cellIndex / m.state.width;
            return MethodResult.returns(new IntValue(m.state.basePosY + row * m.state.cellHeight));
        })),

        Map.entry("GETOFFSET", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int x = ArgumentHelper.getInt(args.get(0));
            int y = ArgumentHelper.getInt(args.get(1));
            if (x >= 0 && x < m.state.width && y >= 0 && y < m.state.height) {
                return MethodResult.returns(new IntValue(y * m.state.width + x));
            }
            return MethodResult.returns(new IntValue(-1));
        })),

        Map.entry("ISGATEEMPTY", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;
            if (s.startGateColumn == -1) return MethodResult.returns(new BoolValue(false));

            for (int y = s.startGateRow; y <= s.endGateRow; y++) {
                for (int x = s.startGateColumn; x <= s.endGateColumn; x++) {
                    int index = y * s.width + x;
                    if (index < s.data.length && s.data[index] != FIELD_CODE_EMPTY) {
                        return MethodResult.returns(new BoolValue(false));
                    }
                }
            }
            return MethodResult.returns(new BoolValue(true));
        })),

        Map.entry("ISINGATE", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            int cellIndex = ArgumentHelper.getInt(args.get(0));
            return MethodResult.returns(new BoolValue(m.state.isInGate(cellIndex)));
        })),

        Map.entry("MOVE", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;
            int srcCellIndex = ArgumentHelper.getInt(args.get(0));
            int destCellIndex = ArgumentHelper.getInt(args.get(1));

            if (srcCellIndex >= 0 && srcCellIndex < s.data.length &&
                destCellIndex >= 0 && destCellIndex < s.data.length) {
                s.data[destCellIndex] = s.data[srcCellIndex];
                s.data[srcCellIndex] = FIELD_CODE_EMPTY;
            }
            return MethodResult.noReturn();
        })),

        Map.entry("NEXT", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;
            if (s.pendingMoves.isEmpty()) return MethodResult.returns(new IntValue(0));

            if (s.currentMoveIndex < 0) {
                s.currentMoveIndex = 0;
            } else {
                s.currentMoveIndex++;
            }

            if (s.currentMoveIndex >= s.pendingMoves.size()) {
                s.pendingMoves.clear();
                s.currentMoveIndex = -1;
                return MethodResult.returns(new IntValue(0));
            }

            int[] move = s.pendingMoves.get(s.currentMoveIndex);
            int srcX = move[0], srcY = move[1], code = move[2];

            int srcCellIdx = srcY * s.width + srcX;
            int destCellIdx = s.getCellIndex(srcCellIdx, code);

            s.setCell(srcCellIdx, FIELD_CODE_EMPTY);
            s.setCell(destCellIdx, FIELD_CODE_STONE);

            int sourceCellCode = s.getCell(srcCellIdx);
            int destCellCode = s.getCell(destCellIdx);

            if (s.currentMoveIndex == s.pendingMoves.size() - 1) {
                m.emitSignal("ONLATEST");
            } else {
                m.emitSignal("ONNEXT");
            }

            if (code == MOVEMENT_DOWN) {
                int potentialMoleCellCode = s.getCell(srcX, srcY + 2);
                if (sourceCellCode == FIELD_CODE_EMPTY &&
                    destCellCode == FIELD_CODE_STONE &&
                    potentialMoleCellCode == FIELD_CODE_MOLE) {
                    return MethodResult.returns(new IntValue(2));
                }
            }

            if (code == MOVEMENT_DOWN || code == MOVEMENT_DOWN_LEFT || code == MOVEMENT_DOWN_RIGHT) {
                return MethodResult.returns(new IntValue(1));
            }

            return MethodResult.returns(new IntValue(0));
        })),

        Map.entry("SET", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;

            if (args.size() == 2) {
                // SET(cellIndex, cellCode)
                int cellIndex = ArgumentHelper.getInt(args.get(0));
                int cellCode = ArgumentHelper.getInt(args.get(1));
                s.setCell(cellIndex, cellCode);
            } else if (args.size() >= 3) {
                // SET(cellX, cellY, cellCode)
                int cellX = ArgumentHelper.getInt(args.get(0));
                int cellY = ArgumentHelper.getInt(args.get(1));
                int cellCode = ArgumentHelper.getInt(args.get(2));
                int cellIndex = cellY * s.width + cellX;
                s.setCell(cellIndex, cellCode);
            }
            return MethodResult.noReturn();
        })),

        Map.entry("SETGATE", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            m.state.startGateColumn = ArgumentHelper.getInt(args.get(0));
            m.state.startGateRow = ArgumentHelper.getInt(args.get(1));
            m.state.endGateColumn = ArgumentHelper.getInt(args.get(2));
            m.state.endGateRow = ArgumentHelper.getInt(args.get(3));
            return MethodResult.noReturn();
        })),

        Map.entry("SETROW", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;
            int row = ArgumentHelper.getInt(args.get(0));

            if (row >= 0 && row < s.height) {
                int startIndex = row * s.width;
                for (int i = 1; i < args.size() && i - 1 < s.width; i++) {
                    int cellCode = ArgumentHelper.getInt(args.get(i));
                    int index = startIndex + (i - 1);
                    s.setCell(index, cellCode);
                }
            }
            return MethodResult.noReturn();
        })),

        Map.entry("TICK", MethodSpec.of((self, args, ctx) -> {
            MatrixVariable m = (MatrixVariable) self;
            MatrixState s = m.state;
            s.pendingMoves.clear();
            s.currentMoveIndex = -1;

            Set<Integer> reservedDest = new HashSet<>();

            for (int y = s.height - 2; y >= 0; y--) {
                for (int x = 0; x < s.width; x++) {
                    int currentIndex = y * s.width + x;
                    if (currentIndex >= s.data.length) continue;

                    int cellCode = s.data[currentIndex];
                    if (cellCode != FIELD_CODE_STONE) continue;

                    int aboveIndex = s.getCellIndex(currentIndex, 0, -1);
                    int belowIndex = s.getCellIndex(currentIndex, MOVEMENT_DOWN);
                    int aboveCellCode = s.getCell(aboveIndex);
                    int belowCellCode = s.getCell(belowIndex);

                    if (y > 0 && aboveCellCode == FIELD_CODE_STONE && belowCellCode != FIELD_CODE_EMPTY) {
                        continue;
                    }

                    if (belowCellCode == FIELD_CODE_EMPTY) {
                        if (reservedDest.add(belowIndex)) {
                            s.pendingMoves.add(new int[]{x, y, MOVEMENT_DOWN});
                        }
                        continue;
                    }

                    if (belowCellCode == FIELD_CODE_ENEMY) {
                        if (reservedDest.add(belowIndex)) {
                            s.pendingMoves.add(new int[]{x, y, MOVEMENT_EXPLOSION});
                        }
                        continue;
                    }

                    if (x > 0) {
                        int leftBelowIndex = s.getCellIndex(currentIndex, MOVEMENT_DOWN_LEFT);
                        int leftIndex = s.getCellIndex(currentIndex, -1, 0);
                        int leftBelowCellCode = s.getCell(leftBelowIndex);
                        int leftCellCode = s.getCell(leftIndex);

                        if (leftBelowCellCode == FIELD_CODE_EMPTY &&
                            leftCellCode == FIELD_CODE_EMPTY &&
                            belowCellCode == FIELD_CODE_STONE) {
                            if (reservedDest.add(leftBelowIndex)) {
                                s.pendingMoves.add(new int[]{x, y, MOVEMENT_DOWN_LEFT});
                            }
                            continue;
                        }
                    }

                    if (x < s.width - 1) {
                        int rightBelowIndex = s.getCellIndex(currentIndex, MOVEMENT_DOWN_RIGHT);
                        int rightIndex = s.getCellIndex(currentIndex, 1, 0);
                        int rightBelowCellCode = s.getCell(rightBelowIndex);
                        int rightCellCode = s.getCell(rightIndex);

                        if (rightBelowCellCode == FIELD_CODE_EMPTY &&
                            rightCellCode == FIELD_CODE_EMPTY &&
                            belowCellCode == FIELD_CODE_STONE) {
                            if (reservedDest.add(rightBelowIndex)) {
                                s.pendingMoves.add(new int[]{x, y, MOVEMENT_DOWN_RIGHT});
                            }
                        }
                    }
                }
            }
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "MatrixVariable[" + name + ", " + state.width + "x" + state.height + "]";
    }
}
