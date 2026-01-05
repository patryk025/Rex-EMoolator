package pl.genschu.bloomooemulator.interpreter.v1.variable.types;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Method;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
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

	public int getFieldCodeMole() { return FIELD_CODE_MOLE; }
	public int getFieldCodeEmpty() { return FIELD_CODE_EMPTY; }
	public int getFieldCodeStone() { return FIELD_CODE_STONE; }
	public int getFieldCodeWallWeak() { return FIELD_CODE_WALL_WEAK; }
	public int getFieldCodeEnemy() { return FIELD_CODE_ENEMY; }
	public int getFieldCodeWallStrong() { return FIELD_CODE_WALL_STRONG; }
	public int getFieldCodeExplosion() { return FIELD_CODE_EXPLOSION; }

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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int oldCell = ArgumentsHelper.getInteger(arguments.get(0));
				int direction = ArgumentsHelper.getInteger(arguments.get(1));
				int row = oldCell / selfVar.getWidth();
				int col = oldCell % selfVar.getWidth();

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
						newCol = Math.min(selfVar.getWidth() - 1, col + 1);
						break;
					case 3: // DOWN
						newRow = Math.min(selfVar.getHeight() - 1, row + 1);
						break;
				}

				// Jeśli nowa pozycja jest taka sama jak stara (np. z powodu granicy mapy)
				if (newRow == row && newCol == col) {
					return new IntegerVariable("", oldCell, selfVar.getContext()); // Zwróć starą komórkę
				}

				int destCell = newRow * selfVar.getWidth() + newCol;

				// Sprawdź, czy komórka docelowa jest pusta lub zawiera gracza
				if (destCell >= 0 && destCell < selfVar.getData().getElements().size()) {
					int cellCode = selfVar.getCell(destCell);
					if (cellCode == 0 || cellCode == 99) { // 0-puste, 99-gracz
						return new IntegerVariable("", destCell, selfVar.getContext());
					}
				}

				return new IntegerVariable("", oldCell, selfVar.getContext());
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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int oldCell = ArgumentsHelper.getInteger(arguments.get(0));
				int oldDir = arguments.size() > 1 ? ArgumentsHelper.getInteger(arguments.get(1)) : 0;

				int row = oldCell / selfVar.getWidth();
				int col = oldCell % selfVar.getWidth();

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
							newCol = Math.min(selfVar.getWidth() - 1, col + 1);
							break;
						case 3: // dół
							newRow = Math.min(selfVar.getHeight() - 1, row + 1);
							break;
					}

					// Jeśli pozycja nie zmieniła się (bo jesteśmy na granicy mapy), spróbuj następny kierunek
					if (newRow == row && newCol == col) {
						continue;
					}

					int destCell = newRow * selfVar.getWidth() + newCol;

					// Sprawdź, czy komórka docelowa jest pusta lub zawiera gracza
					if (destCell >= 0 && destCell < selfVar.getData().getElements().size()) {
						int cellCode = selfVar.getCell(destCell);
						if (cellCode == 0 || cellCode == 99) { // 0-puste, 99-gracz
							return new IntegerVariable("", newDir, selfVar.getContext());
						}
					}
				}

				return new IntegerVariable("", directions[0], selfVar.getContext());
			}
		});
		this.setMethod("CANHEROGOTO", new Method(
				List.of(
						new Parameter("INTEGER", "cellNo", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellNo = ArgumentsHelper.getInteger(arguments.get(0));

				if (cellNo >= 0 && cellNo < selfVar.getData().getElements().size()) {
					// check if there is no gate there
					if(selfVar.isInGate(cellNo))
						return new BoolVariable("", false, selfVar.getContext());

					int cellCode = selfVar.getCell(cellNo);
					return new BoolVariable("",
							cellCode != selfVar.getFieldCodeWallWeak() &&
							cellCode != selfVar.getFieldCodeWallStrong() &&
							cellCode != selfVar.getFieldCodeEnemy() &&
							cellCode != selfVar.getFieldCodeStone(), selfVar.getContext());
				}

				return new BoolVariable("", false, selfVar.getContext());
			}
		});
		this.setMethod("GET", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				if (cellIndex >= 0 && cellIndex < selfVar.getWidth() * selfVar.getHeight()) {
					return selfVar.getData().getElements().get(cellIndex);
				}
				return new IntegerVariable("", 0, selfVar.getContext());
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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int x = ArgumentsHelper.getInteger(arguments.get(0));
				int y = ArgumentsHelper.getInteger(arguments.get(1));

				if (x >= 0 && x < selfVar.getWidth() && y >= 0 && y < selfVar.getHeight()) {
					return new IntegerVariable("", y * selfVar.getWidth() + x, selfVar.getContext());
				}
				return new IntegerVariable("", -1, selfVar.getContext());
			}
		});
		this.setMethod("GETCELLPOSX", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int col = cellIndex % selfVar.getWidth();
				return new IntegerVariable("", selfVar.getBasePosX() + col * selfVar.getCellWidth(), selfVar.getContext());
			}
		});
		this.setMethod("GETCELLPOSY", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int row = cellIndex / selfVar.getWidth();
				return new IntegerVariable("", selfVar.getBasePosY() + row * selfVar.getCellHeight(), selfVar.getContext());
			}
		});
		this.setMethod("GETCELLSNO", new Method(
				List.of(
						new Parameter("INTEGER", "cellCode", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellCode = ArgumentsHelper.getInteger(arguments.get(0));
				int count = 0;

				for (Variable element : selfVar.getData().getElements()) {
					if (element instanceof IntegerVariable && ((IntegerVariable) element).GET() == cellCode) {
						count++;
					}
				}

				return new IntegerVariable("", count, selfVar.getContext());
			}
		});
		this.setMethod("GETFIELDPOSX", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int col = cellIndex % selfVar.getWidth();
				return new IntegerVariable("", selfVar.getBasePosX() + col * selfVar.getCellWidth(), selfVar.getContext());
			}
		});
		this.setMethod("GETFIELDPOSY", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int row = cellIndex / selfVar.getWidth();
				return new IntegerVariable("", selfVar.getBasePosY() + row * selfVar.getCellHeight(), selfVar.getContext());
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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int x = ArgumentsHelper.getInteger(arguments.get(0));
				int y = ArgumentsHelper.getInteger(arguments.get(1));

				if (x >= 0 && x < selfVar.getWidth() && y >= 0 && y < selfVar.getHeight()) {
					return new IntegerVariable("", y * selfVar.getWidth() + x, selfVar.getContext());
				}
				return new IntegerVariable("", -1, selfVar.getContext());
			}
		});
		this.setMethod("ISGATEEMPTY", new Method(
				"BOOL"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				if (selfVar.startGateColumn == -1 || selfVar.startGateRow == -1 || selfVar.endGateColumn == -1 || selfVar.endGateRow == -1) {
					return new BoolVariable("", false, selfVar.getContext());
				}

				for (int y = selfVar.startGateRow; y <= selfVar.endGateRow; y++) {
					for (int x = selfVar.startGateColumn; x <= selfVar.endGateColumn; x++) {
						int index = y * selfVar.getWidth() + x;
						if (index < selfVar.getData().getElements().size()) {
							int cellCode = selfVar.getCell(index);
							if (cellCode != selfVar.FIELD_CODE_EMPTY) {
								return new BoolVariable("", false, selfVar.getContext());
							}
						}
					}
				}

				return new BoolVariable("", true, selfVar.getContext());
			}
		});
		this.setMethod("ISINGATE", new Method(
				List.of(
						new Parameter("INTEGER", "cellIndex", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				if (selfVar.startGateColumn == -1 || selfVar.startGateRow == -1 || selfVar.endGateColumn == -1 || selfVar.endGateRow == -1) {
					return new BoolVariable("", false, selfVar.getContext());
				}

				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));

				return new BoolVariable("", selfVar.isInGate(cellIndex), selfVar.getContext());
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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int srcCellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int destCellIndex = ArgumentsHelper.getInteger(arguments.get(1));

				if (srcCellIndex >= 0 && srcCellIndex < selfVar.getWidth() * selfVar.getHeight() &&
						destCellIndex >= 0 && destCellIndex < selfVar.getWidth() * selfVar.getHeight()) {

					Variable srcValue = selfVar.getData().getElements().get(srcCellIndex);
					selfVar.getData().getElements().set(destCellIndex, srcValue);
					selfVar.getData().getElements().set(srcCellIndex, new IntegerVariable("", 0, selfVar.getContext())); // Puste pole
				}
				return null;
			}
		});
		this.setMethod("NEXT", new Method("INTEGER") {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				if (selfVar.getPendingMoves().isEmpty()) {
					return new IntegerVariable("", 0, selfVar.getContext()); // no action
				}

				// get next move
				if (selfVar.currentMoveIndex < 0) {
					selfVar.currentMoveIndex = 0;
				} else {
					selfVar.currentMoveIndex++;
				}

				// all movements done, finish it all
				if (selfVar.currentMoveIndex >= selfVar.getPendingMoves().size()) {
					selfVar.getPendingMoves().clear();
					selfVar.currentMoveIndex = -1;
					return new IntegerVariable("", 0, selfVar.getContext());  // no action
				}

				int[] move = selfVar.getPendingMoves().get(selfVar.currentMoveIndex);
				int srcX = move[0];
				int srcY = move[1];
				int code = move[2];

				// set arguments for BEHONNEXT/BEHONLATEST
				selfVar.getContext().setVariable("$1", new IntegerVariable("", srcX, selfVar.getContext()));
				selfVar.getContext().setVariable("$2", new IntegerVariable("", srcY, selfVar.getContext()));
				selfVar.getContext().setVariable("$3", new IntegerVariable("", code, selfVar.getContext()));

				int srcCellIdx = srcY * selfVar.getWidth() + srcX;
				int destCellIdx = selfVar.getCellIndex(srcCellIdx, code);

				// move stone
				selfVar.setCell(srcCellIdx, selfVar.FIELD_CODE_EMPTY);
				selfVar.setCell(destCellIdx, selfVar.FIELD_CODE_STONE);

				// update variables
				int sourceCellCode = selfVar.getCell(srcCellIdx);
				int destCellCode = selfVar.getCell(destCellIdx);

				// send signals
				if (selfVar.currentMoveIndex == selfVar.getPendingMoves().size() - 1) {
					selfVar.emitSignal("ONLATEST"); // when there are no more moves
				} else {
					selfVar.emitSignal("ONNEXT"); // when there are more moves
				}

				// check if stone falls on mole
				if(code == selfVar.FIELD_MOVEMENT_DOWN) // collision with mole is only checked when stone moves down
				{
					int potentialMoleCellCode = selfVar.getCell(srcX, srcY + 2);

					// check if below cell is empty and below below cell is mole
					if (sourceCellCode == selfVar.FIELD_CODE_EMPTY &&
						destCellCode == selfVar.FIELD_CODE_STONE &&
						potentialMoleCellCode == selfVar.getFieldCodeMole()) {
						return new IntegerVariable("", 2, selfVar.getContext()); // collision with mole
					}
				}

				if(code == selfVar.FIELD_MOVEMENT_DOWN || code == selfVar.FIELD_MOVEMENT_DOWN_LEFT || code == selfVar.FIELD_MOVEMENT_DOWN_RIGHT) {
					return new IntegerVariable("", 1, selfVar.getContext()); // 1 as movement action
				}

				return new IntegerVariable("", 0, selfVar.getContext());
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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellIndex = ArgumentsHelper.getInteger(arguments.get(0));
				int cellCode = ArgumentsHelper.getInteger(arguments.get(1));

				if (cellIndex >= 0 && cellIndex < selfVar.getWidth() * selfVar.getHeight()) {
					if (cellIndex >= selfVar.getData().getElements().size()) {
						while (selfVar.getData().getElements().size() <= cellIndex) {
							selfVar.getData().getElements().add(new IntegerVariable("", 0, selfVar.getContext()));
						}
					}
					selfVar.getData().getElements().set(cellIndex, new IntegerVariable("", cellCode, selfVar.getContext()));
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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int cellX = ArgumentsHelper.getInteger(arguments.get(0));
				int cellY = ArgumentsHelper.getInteger(arguments.get(1));
				int cellCode = ArgumentsHelper.getInteger(arguments.get(2));

				int cellIndex = cellY * selfVar.getWidth() + cellX;

				if (cellIndex >= 0 && cellIndex < selfVar.getWidth() * selfVar.getHeight()) {
					if (cellIndex >= selfVar.getData().getElements().size()) {
						while (selfVar.getData().getElements().size() <= cellIndex) {
							selfVar.getData().getElements().add(new IntegerVariable("", 0, selfVar.getContext()));
						}
					}
					selfVar.getData().getElements().set(cellIndex, new IntegerVariable("", cellCode, selfVar.getContext()));
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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				selfVar.startGateColumn = ArgumentsHelper.getInteger(arguments.get(0));
				selfVar.startGateRow = ArgumentsHelper.getInteger(arguments.get(1));
				selfVar.endGateColumn = ArgumentsHelper.getInteger(arguments.get(2));
				selfVar.endGateRow = ArgumentsHelper.getInteger(arguments.get(3));
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
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				int row = ArgumentsHelper.getInteger(arguments.get(0));

				if (row >= 0 && row < selfVar.getHeight()) {
					int startIndex = row * selfVar.getWidth();

					for (int i = 1; i < arguments.size() && i - 1 < selfVar.getWidth(); i++) {
						int cellCode = ArgumentsHelper.getInteger(arguments.get(i));
						int index = startIndex + (i - 1);

						if (index >= selfVar.getData().getElements().size()) {
							while (selfVar.getData().getElements().size() <= index) {
								selfVar.getData().getElements().add(new IntegerVariable("", 0, selfVar.getContext()));
							}
						}
						selfVar.getData().getElements().set(index, new IntegerVariable("", cellCode, selfVar.getContext()));
					}
				}
				return null;
			}
		});
		this.setMethod("TICK", new Method("void") {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				MatrixVariable selfVar = (MatrixVariable) self;
				selfVar.getPendingMoves().clear();
				selfVar.currentMoveIndex = -1;

				Set<Integer> reservedDest = new HashSet<>();

				// Check map from bottom to top, left to right
				for (int y = selfVar.getHeight() - 2; y >= 0; y--) {
					for (int x = 0; x < selfVar.getWidth(); x++) {
						int currentIndex = y * selfVar.getWidth() + x;

						if (currentIndex >= selfVar.getData().getElements().size()) continue;

						int cellCode = selfVar.getCell(currentIndex);

						if (cellCode == selfVar.getFieldCodeStone()) {
							int aboveIndex = selfVar.getCellIndex(currentIndex, 0, -1);
							int belowIndex = selfVar.getCellIndex(currentIndex, selfVar.FIELD_MOVEMENT_DOWN);

							int aboveCellCode = selfVar.getCell(aboveIndex);
							int belowCellCode = selfVar.getCell(belowIndex);

							// check if there is no stone above
							if (y > 0) {
								// check if above field is stone and below field is not empty
								if (aboveCellCode == selfVar.getFieldCodeStone() && belowCellCode != selfVar.FIELD_CODE_EMPTY) {
									continue;
								}
							}

							// check if below field is empty
							if (belowCellCode == selfVar.FIELD_CODE_EMPTY) {
								if (reservedDest.add(belowIndex)) {
									selfVar.getPendingMoves().add(new int[]{x, y, selfVar.FIELD_MOVEMENT_DOWN});  // x, y, down
								}
								continue;
							}

							if (belowCellCode == selfVar.getFieldCodeEnemy()) {
								if (reservedDest.add(belowIndex)) {
									selfVar.getPendingMoves().add(new int[]{x, y, selfVar.FIELD_MOVEMENT_EXPLOSION});  // x, y, explosion
								}
								continue;
							}

							// check if it can move askew left
							if (x > 0) {
								int leftBelowIndex = selfVar.getCellIndex(currentIndex, selfVar.FIELD_MOVEMENT_DOWN_LEFT);
								int leftIndex = selfVar.getCellIndex(currentIndex, -1, 0);

								int leftBelowCellCode = selfVar.getCell(leftBelowIndex);
								int leftCellCode = selfVar.getCell(leftIndex);

								if (leftBelowCellCode == selfVar.FIELD_CODE_EMPTY &&
									leftCellCode == selfVar.FIELD_CODE_EMPTY &&
									belowCellCode == selfVar.getFieldCodeStone()) {
									if (reservedDest.add(leftBelowCellCode)) {
										selfVar.getPendingMoves().add(new int[]{x, y, selfVar.FIELD_MOVEMENT_DOWN_LEFT}); // x, y, down-left
									}
									continue;
								}
							}

							// check if it can move askew right
							if (x < selfVar.getWidth() - 1) {
								int rightBelowIndex = selfVar.getCellIndex(currentIndex, selfVar.FIELD_MOVEMENT_DOWN_RIGHT);
								int rightIndex = selfVar.getCellIndex(currentIndex, 1, 0);

								int rightBelowCellCode = selfVar.getCell(rightBelowIndex);
								int rightCellCode = selfVar.getCell(rightIndex);

								if (rightBelowCellCode == selfVar.FIELD_CODE_EMPTY &&
									rightCellCode == selfVar.FIELD_CODE_EMPTY &&
									belowCellCode == selfVar.getFieldCodeStone()) {
									if (reservedDest.add(rightBelowCellCode)) {
										selfVar.getPendingMoves().add(new int[]{x, y, selfVar.FIELD_MOVEMENT_DOWN_RIGHT}); // x, y, down-right
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
