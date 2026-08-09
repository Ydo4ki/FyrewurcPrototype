package org.fw.core.util.bits;

public class QWordBits extends Bits {
    public final long value;
    private final int actualSize;

    public QWordBits(long value, int actualSize) {
        this.value = value;
        if (actualSize > 64)
            throw new IllegalArgumentException(actualSize + " must not be greater than 64");
        this.actualSize = actualSize;
    }

    public static QWordBits of(boolean[] bs) {
        long result = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i]) {
                result |= 1L << (bs.length - 1L - i);
            }
        }
        return new QWordBits(result, bs.length);
    }

    @Override
    public long size() {
        return actualSize;
    }

    @Override
    public boolean get(int index) {
        return ((value >> index) & 1) != 0;
    }

    @Override
    public long getLong(int index) {
        return value;
    }

    @Override
    public Bits or(Bits bits) {
        if (!(bits instanceof QWordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new QWordBits(value | ((QWordBits)bits).value, actualSize);
    }

    @Override
    public Bits and(Bits bits) {
        if (!(bits instanceof QWordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new QWordBits(value & ((QWordBits)bits).value, actualSize);
    }

    @Override
    public Bits xor(Bits bits) {
        if (!(bits instanceof QWordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new QWordBits(value ^ ((QWordBits)bits).value, actualSize);
    }
    @Override
    public Bits not() {
        return new QWordBits(~value, actualSize);
    }
}
