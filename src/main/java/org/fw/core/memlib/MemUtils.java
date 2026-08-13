package org.fw.core.memlib;

import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.WrapperTypeFw;
import org.fw.core.util.bits.*;
import org.fw.core.memlib.words.BitFw;

import java.util.BitSet;

public final class MemUtils {
    public static boolean isBinary(Type type) {
        return binarySize(type) > 0;
    }

    public static long binarySize(Type type) {
        type = WrapperTypeFw.unwrapFully(type);

        if (type == BitFw.bit) return 1;
        if (type.asVal().type().equals(ReifiedTypeFw.reifiedType)) {
            ReifiedTypeFw.ReifiedType rt = type.asVal()._unpack();
            return binarySize(rt.atom_t) * rt.size;
        }
        return 0;
    }

    public static Bits toBits(Val val) {
        val = WrapperTypeFw.unwrapFully(val);

        if (val.type().equals(BitFw.bit))
            return Bits.bits(val._unpack());
        if (val.type().asVal().type().equals(ReifiedTypeFw.reifiedType)) {
            ReifiedTypeFw.ReifiedType rt = val.type().asVal()._unpack();
            long size = rt.size;
            Object obj = val._unpack();
            if (obj instanceof Byte) return new OctetBits((Byte) obj, (int) size);
            if (obj instanceof Short) return new WordBits((Short) obj, (int) size);
            if (obj instanceof Integer) return new DWordBits((Integer) obj, (int) size);
            if (obj instanceof Long) return new QWordBits((Long) obj, (int) size);
            if (obj instanceof long[]) return new MnogaBits(BitSet.valueOf(val._unpack(long[].class)), size);
            if (obj instanceof byte[]) return new MnogaBits(BitSet.valueOf(val._unpack(byte[].class)), size);
            throw new IllegalArgumentException(val._unpack().toString());
        }
        return null;
    }

    public static Val wrap(Type type, Bits bits) {
        if (MemUtils.binarySize(type) != bits.size())
            throw new IllegalArgumentException(MemUtils.binarySize(type) + " != " + bits.size());

        if (bits.size() == 1) return Val.of(type, bits.get(0));
        if (bits instanceof OctetBits) return Val.of(type, ((OctetBits)bits).value);
        if (bits instanceof WordBits) return Val.of(type, ((WordBits)bits).value);
        if (bits instanceof DWordBits) return Val.of(type, ((DWordBits)bits).value);
        if (bits instanceof QWordBits) return Val.of(type, ((QWordBits)bits).value);

        return Val.of(type, bits.toLongArray());
    }
}
