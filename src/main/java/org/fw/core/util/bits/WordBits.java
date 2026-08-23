package org.fw.core.util.bits;

public class WordBits extends Bits {
    public final short value;
    private final int actualSize;

    public WordBits(short value, int actualSize) {
        if (actualSize > 16)
            throw new IllegalArgumentException(actualSize + " must not be greater than 16");

        this.value = (short) (value & ((1 << actualSize) - 1));
        this.actualSize = actualSize;
    }

    public static WordBits of(boolean[] bs) {
        short result = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i]) {
                result |= (short) (1 << (bs.length - 1 - i));
            }
        }
        return new WordBits(result, bs.length);
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
        if (!(bits instanceof WordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new WordBits((short) (value | ((WordBits)bits).value), actualSize);
    }

    @Override
    public Bits and(Bits bits) {
        if (!(bits instanceof WordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new WordBits((short) (value & ((WordBits)bits).value), actualSize);
    }

    @Override
    public Bits xor(Bits bits) {
        if (!(bits instanceof WordBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new WordBits((short) (value ^ ((WordBits)bits).value), actualSize);
    }
    @Override
    public Bits not() {
        return new WordBits((short) ~value, actualSize);
    }
}
