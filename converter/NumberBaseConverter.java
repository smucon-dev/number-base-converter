package converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberBaseConverter {

    public static int precision = 5;

    /**
     *
     * @param sourceNumber The number to be converted.
     * @param sourceBase The base of the source number.
     * @param targetBase The base to which the source number will be converted.
     * @throws IllegalArgumentException If base lower than 2 or greater than 36.
     * @return The converted number.
     */
    public static String convert(String sourceNumber, int sourceBase, int targetBase) {

        if (sourceBase < 2 || sourceBase > 36 || targetBase < 2 || targetBase > 36) {
            throw new IllegalArgumentException("Bases must be between 2 and 36 (inclusive).");
        }

        // this case distinction is for performance reasons only. it could all be handled by the else case.
        if (sourceBase == targetBase) {
            return sourceNumber;
        } else if (sourceBase == 10) {
            return fromDecimal(sourceNumber, targetBase);
        } else if (targetBase == 10) {
            return toDecimal(sourceNumber, sourceBase);
        } else {
            return fromDecimal(toDecimal(sourceNumber, sourceBase, 100), targetBase);
        }
    }

    // convert from base 10 to another base
    private static String fromDecimal(String sourceNumber, int targetBase) {
        BigDecimal[] wholeAndPart = new BigDecimal(sourceNumber).divideAndRemainder(BigDecimal.ONE);
        BigInteger decimal = wholeAndPart[0].toBigInteger();
        BigInteger base = BigInteger.valueOf(targetBase);
        StringBuilder sb = new StringBuilder();
        while (base.compareTo(decimal) <= 0) {
            BigInteger[] decimalAndRemainder = decimal.divideAndRemainder(base);
            decimal = decimalAndRemainder[0];
            sb.append(toLetter(decimalAndRemainder[1].intValue(), base.intValue()));
        }
        sb.append(toLetter(decimal.intValue(), base.intValue()));
        return sb.reverse() + (sourceNumber.indexOf(".") > -1 ? fromDecimalFraction(wholeAndPart[1], targetBase, precision) : "");
    }

    // convert from another base to base 10
    private static String toDecimal(String sourceNumber, int sourceBase) {
        return toDecimal(sourceNumber, sourceBase, precision);
    }

    // convert from another base to base 10
    private static String toDecimal(String sourceNumber, int sourceBase, int digits) {
        String[] wholeAndPart = sourceNumber.split("\\.");
        BigInteger decimal = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(sourceBase);
        for (int i = wholeAndPart[0].length(); i > 0; i--) {
            var factor = BigInteger.valueOf(Integer.valueOf(String.valueOf(wholeAndPart[0].charAt(i - 1)), sourceBase));
            var exponent = wholeAndPart[0].length() - i;
            decimal = decimal.add(factor.multiply(base.pow(exponent)));
        }
        return decimal + (wholeAndPart.length == 2 ? toDecimalFraction(wholeAndPart[1], sourceBase, digits) : "");
    }

    // convert fractional part from base 10 to another base
    private static String fromDecimalFraction(BigDecimal fraction, int targetBase, int digits) {
        var base = new BigDecimal(targetBase);
        var tempFrac = fraction;
        var targetFrac = new StringBuilder(".");
        for (int i = 0; i < digits; i++) {
            var res = tempFrac.multiply(base).divideAndRemainder(BigDecimal.ONE);
            targetFrac.append(toLetter(res[0].intValue(), targetBase));
            tempFrac = res[1];
        }
        return targetFrac.toString();
    }

    // convert fractional part from another base to base 10
    private static String toDecimalFraction(String fraction, int sourceBase, int digits) {
        if (new BigInteger(fraction, sourceBase).equals(BigInteger.ZERO)) {
            return BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP).toString().substring(1);
        }
        var base = new BigDecimal(sourceBase);
        var decimalFrac = BigDecimal.ZERO;
        for (int i = 0; i < fraction.length(); i++) {
            var factor = new BigDecimal(Integer.valueOf(String.valueOf(fraction.charAt(i)), sourceBase));
            decimalFrac = decimalFrac.add(BigDecimal.ONE.divide(base.pow(i + 1), 100, RoundingMode.HALF_UP).multiply(factor));
        }
        return decimalFrac.setScale(digits, RoundingMode.HALF_UP).toString().substring(1);
    }

    // convert digit into a letter  if necessary
    private static String toLetter(int digit, int base) {
        return String.valueOf(Character.forDigit(digit, base)).toUpperCase();
    }

}
