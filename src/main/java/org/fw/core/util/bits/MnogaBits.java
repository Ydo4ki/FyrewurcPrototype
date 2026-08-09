package org.fw.core.util.bits;

import java.util.BitSet;

public class MnogaBits extends Bits {
    private final BitSet value;
    private final long[] asLongArray;
    private final long size;

    public MnogaBits(BitSet value, long size) {
        this.value = value;
        this.size = size;
        this.asLongArray = value.toLongArray();
    }

    public static Bits of(boolean[] bs) {
        BitSet bitSet = new BitSet(bs.length);
        for (int i = 0; i < bs.length; i++) {
            bitSet.set(i, bs[i]);
        }
        return new MnogaBits(bitSet, bs.length);
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean get(int index) {
        return value.get(index);
    }

    @Override
    public long getLong(int index) {
        return asLongArray[index];
    }

    @Override
    public long[] toLongArray() {
        return asLongArray;
    }

    @Override
    public Bits or(Bits bits) {
        long size = bits.size();
        if (size != this.size())
            throw new IllegalArgumentException(size + " != " + this.size());

        long[] data = new long[(int) Math.ceil(size / 8D)];
        for (int i = 0; i < data.length; i++) {
            data[i] = this.getLong(i) | bits.getLong(i);
        }
        return Bits.of(BitSet.valueOf(data), size);
    }

    @Override
    public  Bits and(Bits bits) {
        long size = bits.size();
        if (size != this.size())
            throw new IllegalArgumentException(size + " != " + this.size());

        long[] data = new long[(int) Math.ceil(size / 8D)];
        for (int i = 0; i < data.length; i++) {
            data[i] = this.getLong(i) & bits.getLong(i);
        }
        return Bits.of(BitSet.valueOf(data), size);
    }

    @Override
    public Bits xor(Bits bits) {
        long size = bits.size();
        if (size != this.size())
            throw new IllegalArgumentException(size + " != " + this.size());

        long[] data = new long[(int) Math.ceil(size / 8D)];
        for (int i = 0; i < data.length; i++) {
            data[i] = this.getLong(i) ^ bits.getLong(i);
        }
        return Bits.of(BitSet.valueOf(data), size);
    }

    @Override
    public Bits not() {
        long size = size();

        long[] data = new long[(int) Math.ceil(size / 8D)];
        for (int i = 0; i < data.length; i++) {
            data[i] = ~this.getLong(i);
        }
        return Bits.of(BitSet.valueOf(data), size);
    }
}
