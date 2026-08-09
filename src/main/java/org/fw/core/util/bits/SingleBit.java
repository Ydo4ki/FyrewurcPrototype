package org.fw.core.util.bits;

class SingleBit extends Bits {

    public static final SingleBit ONE = new SingleBit(true);
    public static final SingleBit ZERO = new SingleBit(false);

    private final boolean value;

    public static SingleBit of(boolean value) {
        return value ? ONE : ZERO;
    }

    private SingleBit(boolean value) {
        this.value = value;
    }

    @Override
    public long size() {
        return 1;
    }

    @Override
    public boolean get(int index) {
        return value;
    }

    @Override
    public long getLong(int index) {
        return value ? 1 : 0;
    }

    @Override
    public Bits or(Bits bits) {
        if (!(bits instanceof SingleBit))
            throw new IllegalArgumentException();
        return SingleBit.of(value || ((SingleBit)bits).value);
    }

    @Override
    public Bits and(Bits bits) {
        if (!(bits instanceof SingleBit))
            throw new IllegalArgumentException();
        return SingleBit.of(value && ((SingleBit)bits).value);
    }

    @Override
    public Bits xor(Bits bits) {
        if (!(bits instanceof SingleBit))
            throw new IllegalArgumentException();
        return SingleBit.of(value != ((SingleBit)bits).value);
    }
    @Override
    public Bits not() {
        return new SingleBit(!value);
    }
}
