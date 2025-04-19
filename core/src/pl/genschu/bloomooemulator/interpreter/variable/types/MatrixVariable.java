package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class MatrixVariable extends Variable {
	private int width;
	private int height;
	private int cellWidth;
	private int cellHeight;
	private int basePosX;
	private int basePosY;
	private ArrayVariable data;
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

				if (newRow == row && newCol == col) {
					return new IntegerVariable("", oldCell, context);
				}

				int destCell = newRow * width + newCol;

				if (destCell >= 0 && destCell < data.getElements().size()) {
					Variable cellContent = data.getElements().get(destCell);
					if (cellContent instanceof IntegerVariable) {
						int value = ((IntegerVariable)cellContent).GET();
						if (value == FIELD_CODE_EMPTY || value == FIELD_CODE_MOLE) {
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

				int[] directions = new int[4];
				directions[0] = (oldDir + 3) % 4; // turn left
				directions[1] = oldDir;           // go straight
				directions[2] = (oldDir + 1) % 4; // turn right
				directions[3] = (oldDir + 2) % 4; // turn around

				for (int newDir : directions) {
					int newRow = row;
					int newCol = col;

					switch (newDir) {
						case 0: // left
							newCol = Math.max(0, col - 1);
							break;
						case 1: // up
							newRow = Math.max(0, row - 1);
							break;
						case 2: // right
							newCol = Math.min(width - 1, col + 1);
							break;
						case 3: // down
							newRow = Math.min(height - 1, row + 1);
							break;
					}

					if (newRow == row && newCol == col) {
						continue;
					}

					int destCell = newRow * width + newCol;

					if (destCell >= 0 && destCell < data.getElements().size()) {
						Variable cellContent = data.getElements().get(destCell);
						if (cellContent instanceof IntegerVariable) {
							int value = ((IntegerVariable)cellContent).GET();
							if (value == FIELD_CODE_EMPTY || value == FIELD_CODE_MOLE) {
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
						new Parameter("INTEGER", "cellNo", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETFIELDPOSX is not implemented yet");
			}
		});
		this.setMethod("GETFIELDPOSY", new Method(
				List.of(
						new Parameter("INTEGER", "cellNo", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETFIELDPOSY is not implemented yet");
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
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETOFFSET is not implemented yet");
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
		this.setMethod("NEXT", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method NEXT is not implemented yet");
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
		this.setMethod("TICK", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method TICK is not implemented yet");
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

	public ArrayVariable getData() {
		return data;
	}
}
