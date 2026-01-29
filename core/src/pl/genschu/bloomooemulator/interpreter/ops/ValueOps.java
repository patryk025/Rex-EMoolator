package pl.genschu.bloomooemulator.interpreter.ops;

import pl.genschu.bloomooemulator.interpreter.ast.ArithmeticNode;
import pl.genschu.bloomooemulator.interpreter.ast.ComparisonNode;
import pl.genschu.bloomooemulator.interpreter.ast.LogicalNode;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

/**
 * Operation dispatcher for Value arithmetic and logic.
 */
public final class ValueOps {
    private ValueOps() {}

    public static Value arithmetic(Value left, Value right, ArithmeticNode.ArithmeticOp op) {
        Value a = toPrimitive(left);
        Value b = toPrimitive(right);
        return switch (op) {
            case ADD -> add(a, b);
            case SUBTRACT -> subtract(a, b);
            case MULTIPLY -> multiply(a, b);
            case DIVIDE -> {
                try {
                    yield divide(a, b);
                } catch (ArithmeticException e) {
                    yield new StringValue("NULL");
                }
            }
            case MODULO -> {
                try {
                    yield modulo(a, b);
                } catch (ArithmeticException e) {
                    yield new StringValue("NULL");
                }
            }
        };
    }

    public static BoolValue compare(Value left, Value right, ComparisonNode.ComparisonOp op) {
        Value a = toPrimitive(left);
        Value b = toPrimitive(right);
        return switch (op) {
            case EQUAL -> equalsOp(a, b);
            case NOT_EQUAL -> BoolValue.of(!equalsOp(a, b).value());
            case GREATER -> greaterOp(a, b);
            case GREATER_EQUAL -> BoolValue.of(greaterOp(a, b).value() || equalsOp(a, b).value());
            case LESS -> lessOp(a, b);
            case LESS_EQUAL -> BoolValue.of(lessOp(a, b).value() || equalsOp(a, b).value());
        };
    }

    public static BoolValue logical(Value left, Value right, LogicalNode.LogicalOp op) {
        Value a = toPrimitive(left);
        Value b = toPrimitive(right);

        if (!(a instanceof BoolValue ba) || !(b instanceof BoolValue bb)) {
            throw new UnsupportedOperationException("Logical operation requires BOOL operands");
        }

        return switch (op) {
            case AND -> BoolValue.of(ba.value() && bb.value());
            case OR -> BoolValue.of(ba.value() || bb.value());
        };
    }

    private static Value add(Value a, Value b) {
        return switch (a) {
            case StringValue sa -> switch (b) {
                case StringValue sb -> new StringValue(sa.value() + sb.value());
                case IntValue ib -> new StringValue(sa.value() + ib.toStringValue().value());
                case DoubleValue db -> new StringValue(sa.value() + db.toStringValue().value());
                case BoolValue bb -> new StringValue(sa.value() + bb.toStringValue().value());
                default -> sa;
            };
            case IntValue ia -> switch (b) {
                case StringValue sb -> new IntValue(ia.value() + sb.tryParseInt().value());
                case IntValue ib -> new IntValue(ia.value() + ib.value());
                case DoubleValue db -> new IntValue(ia.value() + db.toInt().value());
                case BoolValue bb -> new IntValue(ia.value() + bb.toInt().value());
                default -> ia;
            };
            case DoubleValue da -> switch (b) {
                case StringValue sb -> new DoubleValue(da.value() + sb.tryParseDouble().value());
                case IntValue ib -> new DoubleValue(da.value() + ib.value());
                case DoubleValue db -> new DoubleValue(da.value() + db.value());
                case BoolValue bb -> new DoubleValue(da.value() + bb.toDouble().value());
                default -> da;
            };
            case BoolValue ba -> switch (b) {
                case StringValue sb -> BoolValue.FALSE;
                case BoolValue bb -> BoolValue.of(ba.value() && bb.value());
                case IntValue ib -> ba;
                case DoubleValue db -> ba;
                default -> ba;
            };
            default -> a;
        };
    }

    private static Value subtract(Value a, Value b) {
        return switch (a) {
            case StringValue sa -> sa;
            case IntValue ia -> switch (b) {
                case StringValue sb -> new IntValue(ia.value() - sb.tryParseInt().value());
                case IntValue ib -> new IntValue(ia.value() - ib.value());
                case DoubleValue db -> new IntValue(ia.value() - db.toInt().value());
                case BoolValue bb -> new IntValue(ia.value() - bb.toInt().value());
                default -> ia;
            };
            case DoubleValue da -> switch (b) {
                case StringValue sb -> new DoubleValue(da.value() - sb.tryParseDouble().value());
                case IntValue ib -> new DoubleValue(da.value() - ib.value());
                case DoubleValue db -> new DoubleValue(da.value() - db.value());
                case BoolValue bb -> new DoubleValue(da.value() - bb.toDouble().value());
                default -> da;
            };
            case BoolValue ba -> ba;
            default -> a;
        };
    }

    private static Value multiply(Value a, Value b) {
        return switch (a) {
            case StringValue sa -> sa;
            case IntValue ia -> switch (b) {
                case StringValue sb -> new IntValue(ia.value() * sb.tryParseInt().value());
                case IntValue ib -> new IntValue(ia.value() * ib.value());
                case DoubleValue db -> new IntValue(ia.value() * db.toInt().value());
                case BoolValue bb -> new IntValue(ia.value() * bb.toInt().value());
                default -> ia;
            };
            case DoubleValue da -> switch (b) {
                case StringValue sb -> new DoubleValue(da.value() * sb.tryParseDouble().value());
                case IntValue ib -> new DoubleValue(da.value() * ib.value());
                case DoubleValue db -> new DoubleValue(da.value() * db.value());
                case BoolValue bb -> new DoubleValue(da.value() * bb.toDouble().value());
                default -> da;
            };
            case BoolValue ba -> switch (b) {
                case BoolValue bb -> BoolValue.of(ba.value() || bb.value());
                case IntValue ib -> BoolValue.of(ba.value() || ib.toBool().value());
                case DoubleValue db -> BoolValue.of(ba.value() || db.toBool().value());
                default -> ba;
            };
            default -> a;
        };
    }

    private static Value divide(Value a, Value b) {
        return switch (a) {
            case StringValue sa -> {
                double divisor = ArgumentHelper.getDouble(b);
                if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                yield sa;
            }
            case IntValue ia -> switch (b) {
                case StringValue sb -> {
                    int divisor = sb.tryParseInt().value();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    yield new IntValue(ia.value() / divisor);
                }
                case IntValue ib -> {
                    if (ib.value() == 0) throw new ArithmeticException("Division by zero");
                    yield new IntValue(ia.value() / ib.value());
                }
                case DoubleValue db -> {
                    int divisor = db.toInt().value();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    yield new IntValue(ia.value() / divisor);
                }
                case BoolValue bb -> {
                    int divisor = bb.toInt().value();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    yield new IntValue(ia.value() / divisor);
                }
                default -> ia;
            };
            case DoubleValue da -> switch (b) {
                case StringValue sb -> {
                    double divisor = sb.tryParseDouble().value();
                    if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                    yield new DoubleValue(da.value() / divisor);
                }
                case IntValue ib -> {
                    if (ib.value() == 0) throw new ArithmeticException("Division by zero");
                    yield new DoubleValue(da.value() / ib.value());
                }
                case DoubleValue db -> {
                    if (db.value() == 0.0) throw new ArithmeticException("Division by zero");
                    yield new DoubleValue(da.value() / db.value());
                }
                case BoolValue bb -> {
                    double divisor = bb.toDouble().value();
                    if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                    yield new DoubleValue(da.value() / divisor);
                }
                default -> da;
            };
            case BoolValue ba -> {
                double divisor = ArgumentHelper.getDouble(b);
                if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                yield ba;
            }
            default -> a;
        };
    }

    private static Value modulo(Value a, Value b) {
        return switch (a) {
            case StringValue sa -> {
                double divisor = ArgumentHelper.getDouble(b);
                if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                yield sa;
            }
            case IntValue ia -> switch (b) {
                case StringValue sb -> {
                    int divisor = sb.tryParseInt().value();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    yield new IntValue(ia.value() % divisor);
                }
                case IntValue ib -> {
                    if (ib.value() == 0) throw new ArithmeticException("Division by zero");
                    yield new IntValue(ia.value() % ib.value());
                }
                case DoubleValue db -> {
                    int divisor = db.toInt().value();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    yield new IntValue(ia.value() % divisor);
                }
                case BoolValue bb -> {
                    int divisor = bb.toInt().value();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    yield new IntValue(ia.value() % divisor);
                }
                default -> ia;
            };
            case DoubleValue da -> switch (b) {
                case StringValue sb -> {
                    double divisor = sb.tryParseDouble().value();
                    if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                    yield new DoubleValue((int) (da.value() % divisor));
                }
                case IntValue ib -> {
                    if (ib.value() == 0) throw new ArithmeticException("Division by zero");
                    yield new DoubleValue((int) (da.value() % ib.value()));
                }
                case DoubleValue db -> {
                    if (db.value() == 0.0) throw new ArithmeticException("Division by zero");
                    yield new DoubleValue((int) (da.value() % db.value()));
                }
                case BoolValue bb -> {
                    double divisor = bb.toDouble().value();
                    if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                    yield new DoubleValue((int) (da.value() % divisor));
                }
                default -> da;
            };
            case BoolValue ba -> {
                double divisor = ArgumentHelper.getDouble(b);
                if (divisor == 0.0) throw new ArithmeticException("Division by zero");
                yield ba;
            }
            default -> a;
        };
    }

    private static BoolValue equalsOp(Value a, Value b) {
        return switch (a) {
            case StringValue sa -> switch (b) {
                case StringValue sb -> BoolValue.of(sa.value().equals(sb.value()));
                case IntValue ib -> BoolValue.of(sa.value().equals(ib.toStringValue().value()));
                case DoubleValue db -> BoolValue.of(sa.value().equals(db.toStringValue().value()));
                case BoolValue bb -> BoolValue.of(sa.value().equals(bb.toStringValue().value()));
                default -> BoolValue.FALSE;
            };
            case IntValue ia -> switch (b) {
                case StringValue sb -> BoolValue.of(ia.value() == sb.tryParseInt().value());
                case IntValue ib -> BoolValue.of(ia.value() == ib.value());
                case DoubleValue db -> BoolValue.of(ia.value() == db.toInt().value());
                case BoolValue bb -> BoolValue.of(ia.value() == bb.toInt().value());
                default -> BoolValue.FALSE;
            };
            case DoubleValue da -> switch (b) {
                case StringValue sb -> BoolValue.of(da.value() == sb.tryParseDouble().value());
                case IntValue ib -> BoolValue.of(da.value() == ib.toDouble().value());
                case DoubleValue db -> BoolValue.of(da.value() == db.value());
                case BoolValue bb -> BoolValue.of(da.value() == bb.toDouble().value());
                default -> BoolValue.FALSE;
            };
            case BoolValue ba -> switch (b) {
                case StringValue sb -> BoolValue.of(ba.value() == sb.toBool().value());
                case IntValue ib -> BoolValue.of(ba.value() == ib.toBool().value());
                case DoubleValue db -> BoolValue.of(ba.value() == db.toBool().value());
                case BoolValue bb -> BoolValue.of(ba.value() == bb.value());
                default -> BoolValue.FALSE;
            };
            default -> BoolValue.FALSE;
        };
    }

    private static BoolValue greaterOp(Value a, Value b) {
        return switch (a) {
            case StringValue sa -> switch (b) {
                case StringValue sb -> BoolValue.of(sa.value().compareTo(sb.value()) > 0);
                case IntValue ib -> BoolValue.of(sa.value().compareTo(ib.toStringValue().value()) > 0);
                case DoubleValue db -> BoolValue.of(sa.value().compareTo(db.toStringValue().value()) > 0);
                case BoolValue bb -> BoolValue.of(sa.value().compareTo(bb.toStringValue().value()) > 0);
                default -> BoolValue.FALSE;
            };
            case IntValue ia -> switch (b) {
                case StringValue sb -> BoolValue.of(ia.value() > sb.tryParseInt().value());
                case IntValue ib -> BoolValue.of(ia.value() > ib.value());
                case DoubleValue db -> BoolValue.of(ia.value() > db.value());
                case BoolValue bb -> BoolValue.of(ia.value() > bb.toInt().value());
                default -> BoolValue.FALSE;
            };
            case DoubleValue da -> switch (b) {
                case StringValue sb -> BoolValue.of(da.value() > sb.tryParseDouble().value());
                case IntValue ib -> BoolValue.of(da.value() > ib.toDouble().value());
                case DoubleValue db -> BoolValue.of(da.value() > db.value());
                case BoolValue bb -> BoolValue.of(da.value() > bb.toDouble().value());
                default -> BoolValue.FALSE;
            };
            case BoolValue ba -> switch (b) {
                case StringValue sb -> BoolValue.of(ba.toInt().value() > (sb.toBool().value() ? 1 : 0));
                case IntValue ib -> BoolValue.of(ba.toInt().value() > (ib.toBool().value() ? 1 : 0));
                case DoubleValue db -> BoolValue.of(ba.toDouble().value() > (db.toBool().value() ? 1 : 0));
                case BoolValue bb -> BoolValue.of(ba.toInt().value() > bb.toInt().value());
                default -> BoolValue.FALSE;
            };
            default -> BoolValue.FALSE;
        };
    }

    private static BoolValue lessOp(Value a, Value b) {
        return switch (a) {
            case StringValue sa -> switch (b) {
                case StringValue sb -> BoolValue.of(sa.value().compareTo(sb.value()) < 0);
                case IntValue ib -> BoolValue.of(sa.value().compareTo(ib.toStringValue().value()) < 0);
                case DoubleValue db -> BoolValue.of(sa.value().compareTo(db.toStringValue().value()) < 0);
                case BoolValue bb -> BoolValue.of(sa.value().compareTo(bb.toStringValue().value()) < 0);
                default -> BoolValue.FALSE;
            };
            case IntValue ia -> switch (b) {
                case StringValue sb -> BoolValue.of(ia.value() < sb.tryParseInt().value());
                case IntValue ib -> BoolValue.of(ia.value() < ib.value());
                case DoubleValue db -> BoolValue.of(ia.value() < db.value());
                case BoolValue bb -> BoolValue.of(ia.value() < bb.toInt().value());
                default -> BoolValue.FALSE;
            };
            case DoubleValue da -> switch (b) {
                case StringValue sb -> BoolValue.of(da.value() < sb.tryParseDouble().value());
                case IntValue ib -> BoolValue.of(da.value() < ib.toDouble().value());
                case DoubleValue db -> BoolValue.of(da.value() < db.value());
                case BoolValue bb -> BoolValue.of(da.value() < bb.toDouble().value());
                default -> BoolValue.FALSE;
            };
            case BoolValue ba -> switch (b) {
                case StringValue sb -> BoolValue.of(ba.toInt().value() < (sb.toBool().value() ? 1 : 0));
                case IntValue ib -> BoolValue.of(ba.toInt().value() < (ib.toBool().value() ? 1 : 0));
                case DoubleValue db -> BoolValue.of(ba.toDouble().value() < (db.toBool().value() ? 1 : 0));
                case BoolValue bb -> BoolValue.of(ba.toInt().value() < bb.toInt().value());
                default -> BoolValue.FALSE;
            };
            default -> BoolValue.FALSE;
        };
    }

    private static Value toPrimitive(Value value) {
        if (value instanceof VariableValue(Variable variable)) {
            return toPrimitive(variable.value());
        }
        if (value instanceof IntValue || value instanceof DoubleValue || value instanceof StringValue || value instanceof BoolValue) {
            return value;
        }
        if (value instanceof NullValue) {
            return new StringValue("NULL");
        }
        return new StringValue(value.toDisplayString());
    }
}
