package org.fw.core.util.bits;

public class OctetBits extends Bits {
    public final byte value;
    private final int actualSize;

    public OctetBits(byte value, int actualSize) {
        if (actualSize > 8)
            throw new IllegalArgumentException(actualSize + " must not be greater than 8");

        this.value = (byte) (value & ((1 << actualSize) - 1));
        this.actualSize = actualSize;
    }

    public static OctetBits of(boolean[] bs) {
        byte result = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i]) {
                result |= (byte) (1 << (bs.length - 1 - i));
            }
        }
        return new OctetBits(result, bs.length);
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
        if (!(bits instanceof OctetBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new OctetBits((byte) (value | ((OctetBits)bits).value), actualSize);
    }

    @Override
    public Bits and(Bits bits) {
        if (!(bits instanceof OctetBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new OctetBits((byte) (value & ((OctetBits)bits).value), actualSize);
    }

    @Override
    public Bits xor(Bits bits) {
        if (!(bits instanceof OctetBits) || bits.size() != this.size())
            throw new IllegalArgumentException();
        return new OctetBits((byte) (value ^ ((OctetBits)bits).value), actualSize);
    }
    @Override
    public Bits not() {
        return new OctetBits((byte) ~value, actualSize);
    }
}

