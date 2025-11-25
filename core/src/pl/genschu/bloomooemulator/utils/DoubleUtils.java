package pl.genschu.bloomooemulator.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtils {
    public static class FcvtResult {
        public final String digits;
        public final int dec;
        public final int sign;
        public FcvtResult(String digits, int dec, int sign) {
            this.digits = digits;
            this.dec = dec;
            this.sign = sign;
        }
    }

    // Replicates the behavior of the original engine's fcvt function
    public static FcvtResult fcvt(double value, int count) {
        // handle NaN / Infinity
        if (Double.isNaN(value)) {
            int n = Math.max(0, count - 5);
            return new FcvtResult("1#QNAN" + "0".repeat(n), 1, 0);
        }
        if (Double.isInfinite(value)) {
            int n = Math.max(0, count - 4);
            int signInf = value < 0.0 ? 1 : 0;
            return new FcvtResult("1#INF" + "0".repeat(n), 1, signInf);
        }

        // detect sign including -0.0
        int sign = Math.copySign(1.0, value) < 0.0 ? 1 : 0;
        BigDecimal x = BigDecimal.valueOf(Math.abs(value));

        // zero case
        if (x.compareTo(BigDecimal.ZERO) == 0) {
            int len = count > 0 ? count : 1;
            return new FcvtResult("0".repeat(len), 0, sign);
        }

        // round to 'count' fractional digits
        BigDecimal rounded = x.setScale(Math.max(0, count), RoundingMode.HALF_UP);

        // determine dec (position of first digit)
        int dec;
        if (rounded.compareTo(BigDecimal.ZERO) == 0) { // if rounded to zero
            dec = x.precision() - x.scale(); // use original x
        } else {
            dec = rounded.precision() - rounded.scale();
        }

        // get digits as string without decimal point or sign, and without scientific notation
        String digits = getDigitsBuffer(count, rounded, dec);

        return new FcvtResult(digits, dec, sign);
    }

    private static String getDigitsBuffer(int count, BigDecimal rounded, int dec) {
        String plain = rounded.stripTrailingZeros().toPlainString();
        // remove decimal point if present
        plain = plain.replace(".", "");
        // remove any leading '+' or '-' (shouldn't be present because we used abs)
        if (plain.startsWith("+") || plain.startsWith("-")) plain = plain.substring(1);

        // remove leading zeros
        int idx = 0;
        while (idx < plain.length() && plain.charAt(idx) == '0') idx++;
        StringBuilder digits = new StringBuilder((idx >= plain.length()) ? "" : plain.substring(idx));

        // refill zeros to needed size
        int target = (Math.max(count, 0)) + dec;
        while (digits.length() < target) {
            digits.append("0");
        }
        return digits.toString();
    }
}
