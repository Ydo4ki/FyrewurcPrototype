package org.fw.core.util.bits;

public class DWordBits extends Bits {
    public final int value;
    private final int actualSize;

    public DWordBits(int value, int actualSize) {
        if (actualSize > 32)
            throw new IllegalArgumentException(actualSize + " must not be greater than 32");

        this.value = value & ((1 << actualSize) - 1);
        this.actualSize = actualSize;
    }

    public static DWordBits of(boolean[] bs) {
        int result = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i]) {
                result |= 1 << (bs.length - 1 - i);
            }
        }
        return new DWordBits(result, bs.length);
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
        if (!(bits instanceof DWordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new DWordBits(value | ((DWordBits)bits).value, actualSize);
    }

    @Override
    public Bits and(Bits bits) {
        if (!(bits instanceof DWordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new DWordBits(value & ((DWordBits)bits).value, actualSize);
    }

    @Override
    public Bits xor(Bits bits) {
        if (!(bits instanceof DWordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new DWordBits(value ^ ((DWordBits)bits).value, actualSize);
    }

    @Override
    public Bits not() {
        return new DWordBits(~value, actualSize);
    }
}

