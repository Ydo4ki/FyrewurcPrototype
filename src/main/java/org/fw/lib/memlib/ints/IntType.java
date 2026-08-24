package org.fw.lib.memlib.ints;

import org.fw.core.base.Val;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.BinaryOperator;

import static org.fw.lib.memlib.ints.Overflow.*;

/**
 * @author Sulphuris
 * @since 7/29/2025 10:55 PM
 */
public final class IntType {

    private final int bitWidth;
    private final Val sign;
    private final Val overflow;
    public final BinaryOperator<Number> add;
    public final BinaryOperator<Number> sub;
    public final BinaryOperator<Number> mul;
    public final BinaryOperator<Number> div;

    public IntType(int bitWidth, Val sign, Val overflow) {
        if (bitWidth <= 0 || bitWidth > 1024) {
            throw new IllegalArgumentException(System.getProperty("user.name") + " what the hell (" + bitWidth + ")");
        }
        this.bitWidth = bitWidth;
        this.sign = sign;
        this.overflow = overflow;
        this.add = addOperator();
        this.sub = subtractOperator();
        this.mul = multiplyOperator();
        this.div = divideOperator();
    }

    public BinaryOperator<Number> add() {
        return add;
    }

    private BinaryOperator<Number> addOperator() {
        if (bitWidth <= 64) return (a, b) -> applyOverflow(a.longValue() + b.longValue());
        else return (a, b) -> applyOverflow(big(a).add(big(b)));
    }

    private BinaryOperator<Number> subtractOperator() {
        if (bitWidth <= 64)
            return (a, b) -> applyOverflow(a.longValue() - b.longValue());
        else
            return (a, b) -> applyOverflow(big(a).subtract(big(b)));
    }

    private BinaryOperator<Number> multiplyOperator() {
        if (bitWidth <= 64)
            return (a, b) -> applyOverflow(a.longValue() * b.longValue());
        else
            return (a, b) -> applyOverflow(big(a).multiply(big(b)));
    }

    private BinaryOperator<Number> divideOperator() {
        if (bitWidth <= 64)
            return (a, b) -> {
                long divisor = b.longValue();
                if (divisor == 0) throw new ArithmeticException("Division by zero");
                return applyOverflow(a.longValue() / divisor);
            };
        else
            return (a, b) -> {
                BigInteger divisor = big(b);
                if (divisor.equals(BigInteger.ZERO)) throw new ArithmeticException("Division by zero");
                return applyOverflow(big(a).divide(divisor));
            };
    }

    public static BigInteger big(Number n) {
        return (n instanceof BigInteger) ? (BigInteger) n : BigInteger.valueOf(n.longValue());
    }


    private Number applyOverflow(long value) {
        long min = minValue().longValue();
        long max = maxValue().longValue();

        if (overflow.equals(wrap)) {
            long mask = (1L << bitWidth) - 1;
            long raw = value & mask;
            if (sign == Signedness.unsigned) {
                return raw;
            } else {
                long signBit = 1L << (bitWidth - 1);
                return (raw ^ signBit) - signBit;
            }
        } else if (overflow.equals(saturate)) {
            return Math.max(min, Math.min(max, value));
        } else if (overflow.equals(trap)) {
            if (value < min || value > max)
                throw new ArithmeticException("Overflow: " + value);
            return value;
        }
        return value;
    }

    private Number applyOverflow(BigInteger value) {
        BigInteger min = big(minValue());
        BigInteger max = big(maxValue());

        if (overflow.equals(wrap)) {
            BigInteger modulo = BigInteger.ONE.shiftLeft(bitWidth);
            value = value.mod(modulo);
            if (sign == Signedness.signed && value.compareTo(max) > 0)
                value = value.subtract(modulo);
            return value.bitLength() <= 63 ? value.longValue() : value;
        } else if (overflow.equals(saturate)) {
            return value.min(max).max(min).bitLength() <= 63
                    ? value.longValue() : value;
        } else if (overflow.equals(trap)) {
            if (value.compareTo(min) < 0 || value.compareTo(max) > 0)
                throw new ArithmeticException("Overflow: " + value);
            return value.bitLength() <= 63 ? value.longValue() : value;
        }
        return value;
    }


    public int getBitWidth() {
        return bitWidth;
    }

    public Val getSign() {
        return sign;
    }

    public Val getOverflow() {
        return overflow;
    }

    public Number minValue() {
        if (sign == Signedness.unsigned) return 0L;
        return -1L << (bitWidth - 1);
    }

    public Number maxValue() {
        if (sign == Signedness.unsigned) return (1L << bitWidth) - 1;
        return (1L << (bitWidth - 1)) - 1;
    }

//    @Override
//    public String toString() {
//        return "[int " + bitWidth + " " + sign.name().toLowerCase() + " overflow:" + overflow.name().toLowerCase() +
//                " endian:" + endianness.name().toLowerCase() + "]";
//    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IntType intType = (IntType) o;
        return bitWidth == intType.bitWidth
                && sign == intType.sign
                && overflow == intType.overflow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitWidth, sign, overflow, add);
    }

    public Number parse(String input) {
        BigInteger value;

        if (input.startsWith("0x") || input.startsWith("0X")) {
            value = new BigInteger(input.substring(2), 16);
        } else if (input.startsWith("0b") || input.startsWith("0B")) {
            value = new BigInteger(input.substring(2), 2);
        } else if (input.startsWith("0o") || input.startsWith("0O")) {
            value = new BigInteger(input.substring(2), 8);
        } else {
            value = new BigInteger(input);
        }

        BigInteger min = minValue() instanceof BigInteger ? (BigInteger) minValue() : BigInteger.valueOf(minValue().longValue());
        BigInteger max = minValue() instanceof BigInteger ? (BigInteger) maxValue() : BigInteger.valueOf(maxValue().longValue());

        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new ArithmeticException("Value out of range for " + this + ": " + value);
        }

        if (this.bitWidth <= 8) {
            return value.byteValue();
        } else if (this.bitWidth <= 16) {
            return value.shortValue();
        } else if (this.bitWidth <= 32) {
            return value.intValue();
        } else if (this.bitWidth <= 64) {
            return value.longValue();
        } else {
            return value;
        }
    }

//    public Val parseVal(String input) {
//        return Val.checked(this, parse(input));
//    }

//    @Override
//    public Val implicitCast(Env callerEnv, Expr expr) throws OffieRuntimeException {
//        if (!(expr instanceof Symbol)) throw new OffieRuntimeException("symbol expected");
//        String symbol = ((Symbol) expr).getValue();
//        try {
//            return parseVal(symbol);
//        } catch (Exception e) {
//            throw new OffieRuntimeException(e);
//        }
//    }
//
//    @Override
//    public Val staticCall(Env callerEnv, ExprList f) throws OffieRuntimeException {
//        return implicitCast(callerEnv, f.get(1));
//    }
}
