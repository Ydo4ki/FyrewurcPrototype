package org.fw.core.util.bits;

import java.util.BitSet;

public abstract class Bits {

    Bits() {}

    public static Bits bits(boolean b) {
        return b ? SingleBit.ONE : SingleBit.ZERO;
    }

    public static Bits bits(boolean... bs) {
        if (bs.length == 0) return empty;
        if (bs.length == 1) return bits(bs[0]);
        if (bs.length <= 8) return OctetBits.of(bs);
        if (bs.length <= 16) return WordBits.of(bs);
        if (bs.length <= 32) return DWordBits.of(bs);
        if (bs.length <= 64) return QWordBits.of(bs);
        return MnogaBits.of(bs);
    }

    public static Bits of(BitSet bitSet, long size) {
        if (size == 0) return empty;
        if (size == 1) return bits(bitSet.get(0));
        if (size <= 8) {
            byte[] bs = bitSet.toByteArray();
            if (bs.length == 0) return new OctetBits((byte) 0, (int) size);
            return new OctetBits(bs[0], (int) size);
        }
        long[] ls = bitSet.toLongArray();
        if (ls.length == 0) {
            if (size <= 16) return new WordBits((short) 0, (int) size);
            if (size <= 32) return new DWordBits(0, (int) size);
            if (size <= 64) return new QWordBits(0, (int) size);
        } else {
            if (size <= 16) return new WordBits((short) ls[0], (int) size);
            if (size <= 32) return new DWordBits((int) ls[0], (int) size);
            if (size <= 64) return new QWordBits(ls[0], (int) size);
        }
        return new MnogaBits(bitSet, size);
    }

    public static final Bits empty = new Bits() {
        @Override
        public long size() {
            return 0;
        }

        @Override
        public boolean get(int index) {
            return false;
        }

        @Override
        public long getLong(int index) {
            return 0;
        }

        @Override
        public Bits or(Bits bits) {
            return this;
        }

        @Override
        public Bits and(Bits bits) {
            return this;
        }

        @Override
        public Bits xor(Bits bits) {
            return this;
        }

        @Override
        public Bits not() {
            return this;
        }
    };

    public static Bits of(byte[] bytes) {
        BitSet bs = BitSet.valueOf(bytes);
        return of(bs, bytes.length * 8L);
    }

    public abstract long size();

    public abstract boolean get(int index); // if index is not in bounds it's UB, this is not meant for external use

    public abstract long getLong(int index);

    public long[] toLongArray() {
        return new long[]{getLong(0)};
    }

    public abstract Bits or(Bits bits);
    public abstract Bits and(Bits bits);
    public abstract Bits xor(Bits bits);

    @Override
    public String toString() {
        int size = Math.toIntExact(size());
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            chars[i] = get(i) ? '1' : '0';
        }
        return String.valueOf(chars);
    }

    public abstract Bits not();

    public Bits getSlice(long fromIndex, long toIndex) {
        BitSet bs = BitSet.valueOf(toLongArray());
        BitSet sub = bs.get(Math.toIntExact(fromIndex), Math.toIntExact(toIndex)); // placeholder
        return of(sub, toIndex - fromIndex);
    }
}
