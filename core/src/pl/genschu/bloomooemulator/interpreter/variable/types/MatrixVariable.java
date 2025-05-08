package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatrixVariable extends Variable {
	private int width;
	private int height;
	private int cellWidth;
	private int cellHeight;
	private int basePosX;
	private int basePosY;
	private final ArrayVariable data;
	private int startGateColumn = -1;
	private int startGateRow = -1;
	private int endGateColumn = -1;
	private int endGateRow = -1;

	// field codes
	private final int FIELD_CODE_MOLE = 99;
	private final int FIELD_CODE_EMPTY = 0;
	private final int FIELD_CODE_GROUND = 1;
	private final int FIELD_CODE_STONE = 2;
	private final int FIELD_CODE_DYNAMITE = 3;
	private final int FIELD_CODE_WALL_WEAK = 4;
	private final int FIELD_CODE_ENEMY = 5;
	private final int FIELD_CODE_WALL_STRONG = 6;
	private final int FIELD_CODE_DYNAMITE_FIRED = 7;
	private final int FIELD_CODE_EXPLOSION = 8;
	private final int FIELD_CODE_EXIT = 9;

	// field movements
	private final int FIELD_MOVEMENT_DOWN = 1;
	private final int FIELD_MOVEMENT_DOWN_LEFT = 2;
	private final int FIELD_MOVEMENT_DOWN_RIGHT = 3;
	private final int FIELD_MOVEMENT_EXPLOSION = 4;

	private final List<int[]> pendingMoves = new ArrayList<>();
	private int currentMoveIndex = -1;

	public MatrixVariable(String name, Context context) {
		super(name, context);
		this.data = new ArrayVariable("_matrix_data_", context);
	}

	@Override
	public String getType() {
		return "MATRIX";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("CALCENEMYMOVEDEST", new Method(
				List.of(
						new Parameter("INTEGER", "oldCell", true),
						new Parameter("INTEGER", "directory", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int oldCell = ArgumentsHelper.getInteger(arguments.get(0));
				int direction = ArgumentsHelper.getInteger(arguments.get(1));
				int row = oldCell / width;
				int col = oldCell % width;

				int newRow = row;
				int newCol = col;

				switch (direction) {
					case 0: // LEFT
						newCol = Math.max(0, col - 1);
						break;
					case 1: // UP
						newRow = Math.max(0, row - 1);
						break;
					case 2: // RIGHT
						newCol = Math.min(width - 1, col + 1);
						break;
					case 3: // DOWN
						newRow = Math.min(height - 1, row + 1);
						break;
				}

				// Jeśli nowa pozycja jest taka sama jak stara (np. z powodu granicy mapy)
				if (newRow == row && newCol == col) {
					return new IntegerVariable("", oldCell, context); // Zwróć starą komórkę
				}

				int destCell = newRow * width + newCol;

				// Sprawdź, czy komórka docelowa jest pusta lub zawiera gracza
				if (destCell >= 0 && destCell < data.getElements().size()) {
					int cellCode = getCell(destCell);
					if (cellCode == 0 || cellCode == 99) { // 0-puste, 99-gracz
						return new IntegerVariable("", destCell, context);
					}
				}

				return new IntegerVariable("", oldCell, context);
			}
		});
		this.setMethod("CALCENEMYMOVEDIR", new Method(
				List.of(
						new Parameter("INTEGER", "oldCell", true),
						new Parameter("INTEGER", "oldDir", false)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int oldCell = ArgumentsHelper.getInteger(arguments.get(0));
				int oldDir = arguments.size() > 1 ? ArgumentsHelper.getInteger(arguments.get(1)) : 0;

				int row = oldCell / width;
				int col = oldCell % width;

				// Kolejność kierunków (0: lewo, 1: góra, 2: prawo, 3: dół)
				// Próbujemy z lewej względem aktualnego kierunku
				int[] directions = new int[4];
				directions[0] = (oldDir + 3) % 4; // lewo względem obecnego kierunku
				directions[1] = oldDir;           // prosto
				directions[2] = (oldDir + 1) % 4; // prawo względem obecnego kierunku
				directions[3] = (oldDir + 2) % 4; // zawróć

				for (int newDir : directions) {
					int newRow = row;
					int newCol = col;

					// Oblicz nową pozycję
					switch (newDir) {
						case 0: // lewo
							newCol = Math.max(0, col - 1);
							break;
						case 1: // góra
							newRow = Math.max(0, row - 1);
							break;
						case 2: // prawo
							newCol = Math.min(width - 1, col + 1);
							break;
						case 3: // dół
							newRow = Math.min(height - 1, row + 1);
							break;
					}

					// Jeśli pozycja nie zmieniła się (bo jesteśmy na granicy mapy), spróbuj następny kierunek
					if (newRow == row && newCol == col) {
						continue;
					}

					int destCell = newRow * width + newCol;

					// Sprawdź, czy komórka docelowa jest pusta lub zawiera gracza
					if (destCell >= 0 && destCell < data.getElements().size()) {
						int cellCode = getCell(destCell);
						if (cellCode == 0 || cellCode == 99) { // 0-puste, 99-gracz
							return new IntegerVariable("", newDir, context);
						}
					}
				}

				return new IntegerVariable("", directions[0], context);
			}
		});
		this.setMethod("CANHEROGOTO", new Method(
				List.of(
						new Parameter("INTEGER", "cellNo", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellNo = ArgumentsHelper.getInteger(arguments.get(0));

				if (cellNo >= 0 && cellNo < data.getElements().size()) {
					// check if there is no gate there
					if(isInGate(cellNo))
						return new BoolVariable("", false, context);

					int cellCode = getCell(cellNo);
					return new BoolVariable("",
							cellCode != FIELD_CODE_WALL_WEAK &&
							cellCode != FIELD_CODE_WALL_STRONG &&
							cellCode != FIELD_CODE_ENEMY &&
							cellCode != FIELD_CODE_STONE, context);
				}

				return new BoolVariable("", false, context);
			}
		});
		this.setMethod("GET", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				if (cellIndex >= 0 && cellIndex < width * height) {
					return data.getElements().get(cellIndex);
				}
				return new IntegerVariable("", 0, context);
			}
		});
		this.setMethod("GETCELLOFFSET", new Method(
				List.of(
						new Parameter("INTEGER", "x", true),
						new Parameter("INTEGER", "y", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int x = ArgumentsHelper.getInteger(arguments.get(0));
				int y = ArgumentsHelper.getInteger(arguments.get(1));

				if (x >= 0 && x < width && y >= 0 && y < height) {
					return new IntegerVariable("", y * width + x, context);
				}
				return new IntegerVariable("", -1, context);
			}
		});
		this.setMethod("GETCELLPOSX", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int col = cellIndex % width;
				return new IntegerVariable("", basePosX + col * cellWidth, context);
			}
		});
		this.setMethod("GETCELLPOSY", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int row = cellIndex / width;
				return new IntegerVariable("", basePosY + row * cellHeight, context);
			}
		});
		this.setMethod("GETCELLSNO", new Method(
				List.of(
						new Parameter("INTEGER", "cellCode", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellCode = ArgumentsHelper.getInteger(arguments.get(0));
				int count = 0;

				for (Variable element : data.getElements()) {
					if (element instanceof IntegerVariable && ((IntegerVariable) element).GET() == cellCode) {
						count++;
					}
				}

				return new IntegerVariable("", count, context);
			}
		});
		this.setMethod("GETFIELDPOSX", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int col = cellIndex % width;
				return new IntegerVariable("", basePosX + col * cellWidth, context);
			}
		});
		this.setMethod("GETFIELDPOSY", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int row = cellIndex / width;
				return new IntegerVariable("", basePosY + row * cellHeight, context);
			}
		});
		this.setMethod("GETOFFSET", new Method(
				List.of(
						new Parameter("INTEGER", "x", true),
						new Parameter("INTEGER", "y", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int x = ArgumentsHelper.getInteger(arguments.get(0));
				int y = ArgumentsHelper.getInteger(arguments.get(1));

				if (x >= 0 && x < width && y >= 0 && y < height) {
					return new IntegerVariable("", y * width + x, context);
				}
				return new IntegerVariable("", -1, context);
			}
		});
		this.setMethod("ISGATEEMPTY", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if (startGateColumn == -1 || startGateRow == -1 || endGateColumn == -1 || endGateRow == -1) {
					return new BoolVariable("", false, context);
				}

				for (int y = startGateRow; y <= endGateRow; y++) {
					for (int x = startGateColumn; x <= endGateColumn; x++) {
						int index = y * width + x;
						if (index < data.getElements().size()) {
							int cellCode = getCell(index);
							if (cellCode != FIELD_CODE_EMPTY) {
								return new BoolVariable("", false, context);
							}
						}
					}
				}

				return new BoolVariable("", true, context);
			}
		});
		this.setMethod("ISINGATE", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if (startGateColumn == -1 || startGateRow == -1 || endGateColumn == -1 || endGateRow == -1) {
					return new BoolVariable("", false, context);
				}

				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));

				return new BoolVariable("", isInGate(cellIndex), context);
			}
		});
		this.setMethod("MOVE", new Method(
				List.of(
						new Parameter("INTEGER", "srcCellIndex", true),
						new Parameter("INTEGER", "destCellIndex", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int srcCellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int destCellIndex = ArgumentsHelper.getInteger(arguments.get(1));

				if (srcCellIndex >= 0 && srcCellIndex < width * height &&
						destCellIndex >= 0 && destCellIndex < width * height) {

					Variable srcValue = data.getElements().get(srcCellIndex);
					data.getElements().set(destCellIndex, srcValue);
					data.getElements().set(srcCellIndex, new IntegerVariable("", 0, context)); // Puste pole
				}
				return null;
			}
		});
		this.setMethod("NEXT", new Method("INTEGER") {
			@Override
			public Variable execute(List<Object> arguments) {
				if (pendingMoves.isEmpty()) {
					return new IntegerVariable("", 0, context); // no action
				}

				// get next move
				if (currentMoveIndex < 0) {
					currentMoveIndex = 0;
				} else {
					currentMoveIndex++;
				}

				// all movements done, finish it all
				if (currentMoveIndex >= pendingMoves.size()) {
					pendingMoves.clear();
					currentMoveIndex = -1;
					return new IntegerVariable("", 0, context);  // no action
				}

				int[] move = pendingMoves.get(currentMoveIndex);
				int srcX = move[0];
				int srcY = move[1];
				int code = move[2];

				// set arguments for BEHONNEXT/BEHONLATEST
				context.setVariable("$1", new IntegerVariable("", srcX, context));
				context.setVariable("$2", new IntegerVariable("", srcY, context));
				context.setVariable("$3", new IntegerVariable("", code, context));

				int srcCellIdx = srcY * width + srcX;
				int destCellIdx = getCellIndex(srcCellIdx, code);

				// move stone
				setCell(srcCellIdx, FIELD_CODE_EMPTY);
				setCell(destCellIdx, FIELD_CODE_STONE);

				// update variables
				int sourceCellCode = getCell(srcCellIdx);
				int destCellCode = getCell(destCellIdx);

				// send signals
				if (currentMoveIndex == pendingMoves.size() - 1) {
					emitSignal("ONLATEST"); // when there are no more moves
				} else {
					emitSignal("ONNEXT"); // when there are more moves
				}

				// check if stone falls on mole
				if(code == FIELD_MOVEMENT_DOWN) // collision with mole is only checked when stone moves down
				{
					int potentialMoleCellCode = getCell(srcX, srcY + 2);

					// check if below cell is empty and below below cell is mole
					if (sourceCellCode == FIELD_CODE_EMPTY &&
						destCellCode == FIELD_CODE_STONE &&
						potentialMoleCellCode == FIELD_CODE_MOLE) {
						return new IntegerVariable("", 2, context); // collision with mole
					}
				}

				if(code == FIELD_MOVEMENT_DOWN || code == FIELD_MOVEMENT_DOWN_LEFT || code == FIELD_MOVEMENT_DOWN_RIGHT) {
					return new IntegerVariable("", 1, context); // 1 as movement action
				}

				return new IntegerVariable("", 0, context);
			}
		});
		this.setMethod("SET", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true),
						new Parameter("INTEGER", "cellCode", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int cellCode = ArgumentsHelper.getInteger(arguments.get(1));

				if (cellIndex >= 0 && cellIndex < width * height) {
					if (cellIndex >= data.getElements().size()) {
						while (data.getElements().size() <= cellIndex) {
							data.getElements().add(new IntegerVariable("", 0, context));
						}
					}
					data.getElements().set(cellIndex, new IntegerVariable("", cellCode, context));
				}
				return null;
			}
		});
		this.setMethod("SET", new Method(
				List.of(
						new Parameter("INTEGER", "cellX", true),
						new Parameter("INTEGER", "cellY", true),
						new Parameter("INTEGER", "cellCode", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int cellX = ArgumentsHelper.getInteger(arguments.get(0));
				int cellY = ArgumentsHelper.getInteger(arguments.get(1));
				int cellCode = ArgumentsHelper.getInteger(arguments.get(2));

				int cellIndex = cellY * width + cellX;

				if (cellIndex >= 0 && cellIndex < width * height) {
					if (cellIndex >= data.getElements().size()) {
						while (data.getElements().size() <= cellIndex) {
							data.getElements().add(new IntegerVariable("", 0, context));
						}
					}
					data.getElements().set(cellIndex, new IntegerVariable("", cellCode, context));
				}
				return null;
			}
		});
		this.setMethod("SETGATE", new Method(
				List.of(
						new Parameter("INTEGER", "startCol", true),
						new Parameter("INTEGER", "startRow", true),
						new Parameter("INTEGER", "endCol", true),
						new Parameter("INTEGER", "endRow", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				startGateColumn = ArgumentsHelper.getInteger(arguments.get(0));
				startGateRow = ArgumentsHelper.getInteger(arguments.get(1));
				endGateColumn = ArgumentsHelper.getInteger(arguments.get(2));
				endGateRow = ArgumentsHelper.getInteger(arguments.get(3));
				return null;
			}
		});
		this.setMethod("SETROW", new Method(
				List.of(
						new Parameter("INTEGER", "row", true),
						new Parameter("INTEGER", "cellCode1...cellCodeN", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int row = ArgumentsHelper.getInteger(arguments.get(0));

				if (row >= 0 && row < height) {
					int startIndex = row * width;

					for (int i = 1; i < arguments.size() && i - 1 < width; i++) {
						int cellCode = ArgumentsHelper.getInteger(arguments.get(i));
						int index = startIndex + (i - 1);

						if (index >= data.getElements().size()) {
							while (data.getElements().size() <= index) {
								data.getElements().add(new IntegerVariable("", 0, context));
							}
						}
						data.getElements().set(index, new IntegerVariable("", cellCode, context));
					}
				}
				return null;
			}
		});
		this.setMethod("TICK", new Method("void") {
			@Override
			public Variable execute(List<Object> arguments) {
				pendingMoves.clear();
				currentMoveIndex = -1;

				Set<Integer> reservedDest = new HashSet<>();

				// Check map from bottom to top, left to right
				for (int y = height - 2; y >= 0; y--) {
					for (int x = 0; x < width; x++) {
						int currentIndex = y * width + x;

						if (currentIndex >= data.getElements().size()) continue;

						int cellCode = getCell(currentIndex);

						if (cellCode == FIELD_CODE_STONE) {
							int aboveIndex = getCellIndex(currentIndex, 0, -1);
							int belowIndex = getCellIndex(currentIndex, FIELD_MOVEMENT_DOWN);

							int aboveCellCode = getCell(aboveIndex);
							int belowCellCode = getCell(belowIndex);

							// check if there is no stone above
							if (y > 0) {
								// check if above field is stone and below field is not empty
								if (aboveCellCode == FIELD_CODE_STONE && belowCellCode != FIELD_CODE_EMPTY) {
									continue;
								}
							}

							// check if below field is empty
							if (belowCellCode == FIELD_CODE_EMPTY) {
								if (reservedDest.add(belowIndex)) {
									pendingMoves.add(new int[]{x, y, FIELD_MOVEMENT_DOWN});  // x, y, down
								}
								continue;
							}

							// check if it can move askew left
							if (x > 0) {
								int leftBelowIndex = getCellIndex(currentIndex, FIELD_MOVEMENT_DOWN_LEFT);
								int leftIndex = getCellIndex(currentIndex, -1, 0);

								int leftBelowCellCode = getCell(leftBelowIndex);
								int leftCellCode = getCell(leftIndex);

								if (leftBelowCellCode == FIELD_CODE_EMPTY &&
									leftCellCode == FIELD_CODE_EMPTY &&
									belowCellCode == FIELD_CODE_STONE) {
									if (reservedDest.add(leftBelowCellCode)) {
										pendingMoves.add(new int[]{x, y, FIELD_MOVEMENT_DOWN_LEFT}); // x, y, down-left
									}
									continue;
								}
							}

							// check if it can move askew right
							if (x < width - 1) {
								int rightBelowIndex = getCellIndex(currentIndex, FIELD_MOVEMENT_DOWN_RIGHT);
								int rightIndex = getCellIndex(currentIndex, 1, 0);

								int rightBelowCellCode = getCell(rightBelowIndex);
								int rightCellCode = getCell(rightIndex);

								if (rightBelowCellCode == FIELD_CODE_EMPTY &&
									rightCellCode == FIELD_CODE_EMPTY &&
									belowCellCode == FIELD_CODE_STONE) {
									if (reservedDest.add(rightBelowCellCode)) {
										pendingMoves.add(new int[]{x, y, FIELD_MOVEMENT_DOWN_RIGHT}); // x, y, down-right
									}
								}
							}
						}
					}
				}

				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		switch (name) {
			case "SIZE":
				String[] dimensions = attribute.getValue().toString().split(",");
				if (dimensions.length == 2) {
					width = Integer.parseInt(dimensions[0].trim());
					height = Integer.parseInt(dimensions[1].trim());

					// Inicjalizujemy tablicę danych
					for (int i = 0; i < width * height; i++) {
						data.getElements().add(new IntegerVariable("", 0, context));
					}
				}
				break;
			case "BASEPOS":
				String[] basePos = attribute.getValue().toString().split(",");
				if (basePos.length == 2) {
					basePosX = Integer.parseInt(basePos[0].trim());
					basePosY = Integer.parseInt(basePos[1].trim());
				}
				break;
			case "CELLWIDTH":
				cellWidth = Integer.parseInt(attribute.getValue().toString());
				break;
			case "CELLHEIGHT":
				cellHeight = Integer.parseInt(attribute.getValue().toString());
				break;
			default:
				super.setAttribute(name, attribute);
				break;
		}
	}

	private void setCell(int idx, int value) {
		if (idx < data.getElements().size()) {
			data.getElements().set(idx, new IntegerVariable("", value, context));
		}
	}

	private int getCell(int x, int y) {
		int idx = y * width + x;
		return getCell(idx);
	}

	private int getCell(int idx) {
		if (idx < data.getElements().size()) {
			Variable cell = data.getElements().get(idx);
			if (cell instanceof IntegerVariable) {
				return ((IntegerVariable)cell).GET();
			}
		}
		return 0;
	}

	private int getCellIndex(int srcIdx, int direction) {
		switch (direction) {
			case FIELD_MOVEMENT_DOWN:
				return getCellIndex(srcIdx, 0, 1);
			case FIELD_MOVEMENT_DOWN_LEFT:
				return getCellIndex(srcIdx, -1, 1);
			case FIELD_MOVEMENT_DOWN_RIGHT:
				return getCellIndex(srcIdx, 1, 1);
			default:
				return srcIdx;
		}
	}

	private int getCellIndex(int srcIdx, int offsetX, int offsetY) {
		int x = srcIdx % width;
		int y = srcIdx / width;

		return (y + offsetY) * width + (x + offsetX);
	}

	private boolean isInGate(int cellIndex) {
		int row = cellIndex / width;
		int col = cellIndex % width;

        return (row >= startGateRow && row <= endGateRow &&
                col >= startGateColumn && col <= endGateColumn);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public int getBasePosY() {
		return basePosY;
	}

	public int getBasePosX() {
		return basePosX;
	}

	public ArrayVariable getData() {
		return data;
	}

	public List<int[]> getPendingMoves() {
		return pendingMoves;
	}
}
