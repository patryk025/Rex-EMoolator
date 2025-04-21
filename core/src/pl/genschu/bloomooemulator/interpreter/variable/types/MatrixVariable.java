package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.List;

public class MatrixVariable extends Variable {
	private int width;
	private int height;
	private int cellWidth;
	private int cellHeight;
	private int basePosX;
	private int basePosY;
	private final ArrayVariable data;
	private int gateRow = -1;
	private int gateCol = -1;
	private int gateWidth = 0;
	private int gateHeight = 0;

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

	private List<int[]> pendingMoves = new ArrayList<>();
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
					Variable cellContent = data.getElements().get(destCell);
					if (cellContent instanceof IntegerVariable) {
						int value = ((IntegerVariable)cellContent).GET();
						if (value == 0 || value == 99) { // 0-puste, 99-gracz
							return new IntegerVariable("", destCell, context);
						}
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
						Variable cellContent = data.getElements().get(destCell);
						if (cellContent instanceof IntegerVariable) {
							int value = ((IntegerVariable)cellContent).GET();
							if (value == 0 || value == 99) { // 0-puste, 99-gracz
								return new IntegerVariable("", newDir, context);
							}
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
					Variable cell = data.getElements().get(cellNo);
					if (cell instanceof IntegerVariable) {
						int value = ((IntegerVariable) cell).GET();
						// Check if cell is not a wall, enemy or stone
						return new BoolVariable("", value != FIELD_CODE_WALL_WEAK && value != FIELD_CODE_WALL_STRONG && value != FIELD_CODE_ENEMY && value != FIELD_CODE_STONE, context);
					}
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
				if (gateRow == -1 || gateCol == -1 || gateWidth == 0 || gateHeight == 0) {
					return new BoolVariable("", false, context);
				}

				for (int y = gateRow; y < gateRow + gateHeight; y++) {
					for (int x = gateCol; x < gateCol + gateWidth; x++) {
						int index = y * width + x;
						if (index < data.getElements().size()) {
							Variable cell = data.getElements().get(index);
							if (cell instanceof IntegerVariable && ((IntegerVariable) cell).GET() != 0) {
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
				if (gateRow == -1 || gateCol == -1 || gateWidth == 0 || gateHeight == 0) {
					return new BoolVariable("", false, context);
				}

				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int row = cellIndex / width;
				int col = cellIndex % width;

				boolean inGate = (row >= gateRow && row < gateRow + gateHeight &&
						col >= gateCol && col < gateCol + gateWidth);

				return new BoolVariable("", inGate, context);
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

				Variable srcCell = data.getElements().get(srcY * width + srcX);

				// check if stone falls on mole
				if(code == FIELD_MOVEMENT_DOWN) // collision with mole is only checked when stone moves down
				{
					Variable destCell = data.getElements().get((srcY + 1) * width + srcX);
					if (destCell instanceof IntegerVariable && ((IntegerVariable) destCell).GET() == FIELD_CODE_MOLE) {
						return new IntegerVariable("", 2, context); // collision with mole
					}
				}

				// move stone
				if (srcCell instanceof IntegerVariable) {
					switch (code) {
						case 1:
							data.getElements().set(srcY * width + srcX, data.getElements().get((srcY + 1) * width + srcX));
							data.getElements().set((srcY + 1) * width + srcX, new IntegerVariable("", FIELD_CODE_STONE, context));
							break;
						case 2:
							data.getElements().set(srcY * width + srcX, data.getElements().get((srcY + 1) * width + (srcX - 1)));
							data.getElements().set((srcY + 1) * width + (srcX - 1), new IntegerVariable("", FIELD_CODE_STONE, context));
							break;
						case 3:
							data.getElements().set(srcY * width + srcX, data.getElements().get((srcY + 1) * width + (srcX + 1)));
							data.getElements().set((srcY + 1) * width + (srcX + 1), new IntegerVariable("", FIELD_CODE_STONE, context));
							break;
					}
				}

				// send signals
				if (currentMoveIndex == pendingMoves.size() - 1) {
					emitSignal("ONLATEST"); // when there are no more moves
				} else {
					emitSignal("ONNEXT"); // when there are more moves
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
		this.setMethod("SETGATE", new Method(
				List.of(
						new Parameter("INTEGER", "row", true),
						new Parameter("INTEGER", "col", true),
						new Parameter("INTEGER", "width", true),
						new Parameter("INTEGER", "height", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				gateRow = ArgumentsHelper.getInteger(arguments.get(0));
				gateCol = ArgumentsHelper.getInteger(arguments.get(1));
				gateWidth = ArgumentsHelper.getInteger(arguments.get(2));
				gateHeight = ArgumentsHelper.getInteger(arguments.get(3));
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

				// Check map from bottom to top, left to right
				for (int y = height - 2; y >= 0; y--) {
					for (int x = 0; x < width; x++) {
						int currentIndex = y * width + x;

						if (currentIndex >= data.getElements().size()) continue;

						Variable currentCell = data.getElements().get(currentIndex);
						if (!(currentCell instanceof IntegerVariable)) continue;
						int currentValue = ((IntegerVariable)currentCell).GET();

						if (currentValue == FIELD_CODE_STONE) {
							// check if there is no stone above
							if (y > 0) {
								int aboveIndex = (y - 1) * width + x;
								int belowIndex = (y + 1) * width + x;
								if (aboveIndex >= 0 && aboveIndex < data.getElements().size() && belowIndex >= 0 && belowIndex < data.getElements().size()) {
									Variable aboveCell = data.getElements().get(aboveIndex);
									Variable belowCell = data.getElements().get(belowIndex);
									if (aboveCell instanceof IntegerVariable && belowCell instanceof IntegerVariable) {
										int aboveValue = ((IntegerVariable)aboveCell).GET();
										int belowValue = ((IntegerVariable)belowCell).GET();

										// check if above field is stone and below field is not empty
										if (aboveValue == FIELD_CODE_STONE && belowValue != FIELD_CODE_EMPTY) {
											continue;
										}
									}
								}
							}

							// check below
							int belowIndex = (y + 1) * width + x;
							if (belowIndex < data.getElements().size()) {
								Variable belowCell = data.getElements().get(belowIndex);
								if (belowCell instanceof IntegerVariable) {
									int belowValue = ((IntegerVariable)belowCell).GET();

									// check if below field is empty
									if (belowValue == FIELD_CODE_EMPTY || belowValue == FIELD_CODE_MOLE) {
										pendingMoves.add(new int[]{x, y, FIELD_MOVEMENT_DOWN}); // x, y, down
										continue;
									}
								}
							}

							// check if it can move askew left
							if (x > 0) {
								int leftBelowIndex = (y + 1) * width + (x - 1);
								int leftIndex = y * width + (x - 1);
								if (leftBelowIndex < data.getElements().size() && leftIndex < data.getElements().size() && belowIndex < data.getElements().size()) {
									Variable leftBelowCell = data.getElements().get(leftBelowIndex);
									Variable leftCell = data.getElements().get(leftIndex);
									Variable belowCell = data.getElements().get(belowIndex);

									if (leftBelowCell instanceof IntegerVariable &&
											leftCell instanceof IntegerVariable &&
											belowCell instanceof IntegerVariable &&
											((IntegerVariable)leftBelowCell).GET() == FIELD_CODE_EMPTY &&
											((IntegerVariable)leftCell).GET() == FIELD_CODE_EMPTY &&
											((IntegerVariable)belowCell).GET() == FIELD_CODE_STONE) {

										pendingMoves.add(new int[]{x, y, FIELD_MOVEMENT_DOWN_LEFT}); // x, y, down-left
										continue;
									}
								}
							}

							// check if it can move askew right
							if (x < width - 1) {
								int rightBelowIndex = (y + 1) * width + (x + 1);
								int rightIndex = y * width + (x + 1);
								if (rightBelowIndex < data.getElements().size() && rightIndex < data.getElements().size() && belowIndex < data.getElements().size()) {
									Variable rightBelowCell = data.getElements().get(rightBelowIndex);
									Variable rightCell = data.getElements().get(rightIndex);
									Variable belowCell = data.getElements().get(belowIndex);

									if (rightBelowCell instanceof IntegerVariable &&
											rightCell instanceof IntegerVariable &&
											belowCell instanceof IntegerVariable &&
											((IntegerVariable)rightBelowCell).GET() == FIELD_CODE_EMPTY &&
											((IntegerVariable)rightCell).GET() == FIELD_CODE_EMPTY &&
											((IntegerVariable)belowCell).GET() == FIELD_CODE_STONE) {

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
}
