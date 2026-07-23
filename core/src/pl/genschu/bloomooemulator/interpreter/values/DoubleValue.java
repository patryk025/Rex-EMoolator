package pl.genschu.bloomooemulator.interpreter.values;

import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.engine.compatibility.Compatibility;
import pl.genschu.bloomooemulator.engine.compatibility.CompatibilityProfile;
import pl.genschu.bloomooemulator.utils.DoubleUtils;

import static pl.genschu.bloomooemulator.utils.DoubleUtils.fcvt;

/**
 * Immutable double value.
 */
public record DoubleValue(double value) implements Value {

    @Override
    public ValueType getType() {
        return ValueType.DOUBLE;
    }

    @Override
    public Object unwrap() {
        return value;
    }

    @Override
    public String toDisplayString() {
        return toStringValue().value();
    }

    /**
     * Converts this double to an int value (truncates).
     */
    public IntValue toInt() {
        if (value() > 0) {
            return new IntValue((int) Math.round(value()));
        } else {
            return new IntValue((int) Math.ceil(value() - 0.5)); // negative values are rounded down? Instead of -0.5 == 0 it becomes -1
        }
    }

    /**
     * Converts this double to a string, formatted the way the engine of the
     * currently running game does. The profile is ambient because the choice
     * belongs to the emulated binary, not to the call site.
     */
    public StringValue toStringValue() {
        return toStringValue(Compatibility.current());
    }

    public StringValue toStringValue(CompatibilityProfile profile) {
        // The debug switch only ever turns the original quirk off; it can never
        // enable it for an engine that formatted correctly.
        boolean piklibQuirk = profile.hasPiklibDoubleStringQuirk()
                && EngineConfig.getInstance().isUseOriginalDoubleRepresentation();
        return new StringValue(piklibQuirk ? formatPiklib(value) : formatBlooMoo(value));
    }

    /**
     * The fixed BlooMoo algorithm, from {@code CXString::toString(double)}
     *
     * <pre>
     * if (value == 0.0)  return "0";
     * digits = _fcvt(value, 5, &amp;dec, &amp;sign);
     * if (digits empty)  return "0";
     * out = dec &lt; 1 ? "0." + "0" * -dec + digits
     *                : digits.Left(dec) + "." + digits.Right(dec);
     * if (value &lt; 0.0)   out = "-" + out;
     * </pre>
     *
     * {@code CXString::Right(n)} (@0x10002009) is {@code buffer + n}, i.e. the
     * substring from {@code n} — not the last {@code n} characters. The sign is
     * prepended to the finished string, which is exactly the bug Piklib has.
     */
    private static String formatBlooMoo(double value) {
        if (value == 0.0) {
            return "0";
        }

        DoubleUtils.FcvtResult result = fcvt(value, 5);
        String digits = result.digits;
        if (digits.isEmpty()) {
            return "0";
        }

        StringBuilder output = new StringBuilder();
        if (result.sign == 1) {
            output.append('-');
        }

        if (result.dec < 1) {
            output.append("0.");
            output.append("0".repeat(-result.dec));
            output.append(digits);
        } else {
            // The original indexes past the terminator when dec exceeds the digit
            // count; fcvt(value, 5) always yields dec + 5 digits, so it cannot.
            int split = Math.min(result.dec, digits.length());
            output.append(digits, 0, split);
            output.append('.');
            output.append(digits.substring(split));
        }
        return output.toString();
    }

    /**
     * Reconstruction of the malformed Piklib CXString::toString(double)
     * algorithm. In particular, negative fractions put the sign after "0.".
     */
    private static String formatPiklib(double value) {
        DoubleUtils.FcvtResult result = fcvt(value, 5);
        String digits = result.digits;
        int decimal = result.dec;
        int sign = result.sign;

        StringBuilder output = new StringBuilder();

        if (sign == 1) {
            output.append("-");
        }

        boolean isVerySmall = (value != 0.0 && value * value < 1.0);

        StringBuilder signStr = new StringBuilder();
        double tempValue = value;
        while (tempValue > 0.0) {
            if (tempValue * 10.0001 >= 1.0) {
                break;
            }
            signStr.append("0");
            tempValue *= 10.0;
        }

        if (digits.isEmpty()) {
            digits = "0";
        }

        if (decimal < 1) {
            output.append(signStr).append(digits);
        } else {
            String left = digits.substring(0, Math.min(decimal, digits.length()));
            String right = decimal < digits.length() ? digits.substring(decimal) : "";
            output.append(signStr).append(left);
            if (!right.isEmpty()) {
                output.append(".").append(right);
            }
        }

        if (isVerySmall && output.indexOf(".") == -1) {
            output.insert(0, "0.");
        }

        return output.toString();
    }

    /**
     * Converts this double to a boolean value (0.0 = false, non-zero = true).
     */
    public BoolValue toBool() {
        return new BoolValue(value != 0.0);
    }

    /**
     * Converts this double to a double value (no-op).
     */
    public DoubleValue toDouble() {
        return this;
    }
}
