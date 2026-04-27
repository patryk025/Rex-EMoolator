package pl.genschu.bloomooemulator.interpreter.values;

import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.utils.DoubleUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
     * Converts this double to a string value.
     */
    public StringValue toStringValue() {
        if(EngineConfig.getInstance().isUseOriginalDoubleRepresentation()) {
            // Reconstruction of algorithm from Piklib library
            double value = value();
            DoubleUtils.FcvtResult result = fcvt(value, 5);
            String digits = result.digits;
            int decimal = result.dec;
            int sign = result.sign;

            StringBuilder output = new StringBuilder();

            // Add sign if negative
            if (sign == 1) {
                output.append("-");
            }

            // Check if number is very small (0 < |x| < 1)
            boolean isVerySmall = (value != 0.0 && value * value < 1.0);

            // Handle leading zeros for very small numbers
            StringBuilder signStr = new StringBuilder();
            double tempValue = value;
            while (tempValue > 0.0) {
                if (tempValue * 10.0001 >= 1.0) {
                    break;
                }
                signStr.append("0");
                tempValue *= 10.0;
            }

            // Handle empty buffer case
            if (digits.isEmpty()) {
                digits = "0";
            }

            // Construct the final string
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

            // Add leading "0." for very small numbers without decimal point
            if (isVerySmall && output.indexOf(".") == -1) {
                output.insert(0, "0.");
            }

            return new StringValue(output.toString());
        }
        else {
            if (value() == 0) {
                return new StringValue("0");
            }
            NumberFormat formatter = new DecimalFormat("#0.00000");
            return new StringValue(formatter.format(value()).replace(",", "."));
        }
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
