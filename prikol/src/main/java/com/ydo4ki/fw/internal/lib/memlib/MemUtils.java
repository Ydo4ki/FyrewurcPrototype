package com.ydo4ki.fw.internal.lib.memlib;

import com.ydo4ki.fw.internal.lib.memlib.utils.bits.*;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.std.WrapperTypeFw;
import com.ydo4ki.fw.internal.lib.memlib.ints.IntType;
import com.ydo4ki.fw.internal.lib.memlib.words.BitFw;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.BitSet;

public final class MemUtils {
    public static boolean isBinary(Type type) {
        return binarySize(type) > 0;
    }

    public static long binarySize(Type type) {
        type = WrapperTypeFw.unwrapFully(type);

        if (type == BitFw.bit) return 1;
        if (type.asVal().getType().equals(ReifiedTypeFw.reifiedType)) {
            ReifiedTypeFw.ReifiedType rt = type.asVal()._UNPACK_();
            return binarySize(rt.atom_t) * rt.size;
        }
        return 0;
    }

    public static Number toBitsAsNumber(Val val) {
        val = WrapperTypeFw.unwrapFully(val);
        if (val.getType().equals(BitFw.bit))
            return (Boolean) val._UNPACK_() ? (byte) 1 : (byte) 0;

        if (val.getType().asVal().getType().equals(ReifiedTypeFw.reifiedType)) {
            ReifiedTypeFw.ReifiedType rt = val.getType().asVal()._UNPACK_();
            long size = rt.size;
            Object obj = val._UNPACK_();
            if (obj instanceof Byte) return (Byte) obj;
            if (obj instanceof Short) return (Short) obj;
            if (obj instanceof Integer) return (Integer) obj;
            if (obj instanceof Long) return (Long) obj;
            if (obj instanceof long[]) return new BigInteger(MemUtils.toBytes((long[]) val._UNPACK_()));
            if (obj instanceof byte[]) return new BigInteger((byte[]) val._UNPACK_());
            throw new IllegalArgumentException(val._UNPACK_().toString());
        }
        return null;
    }

    private static byte[] toBytes(long[] longs) {
        ByteBuffer buffer = ByteBuffer.allocate(longs.length * Long.BYTES);

        for (long value : longs) {
            buffer.putLong(value);
        }

        return buffer.array();
    }

    public static Bits toBits(Val val) {
        val = WrapperTypeFw.unwrapFully(val);

        if (val.getType().equals(BitFw.bit))
            return Bits.bits((Boolean) val._UNPACK_());
        if (val.getType().asVal().getType().equals(ReifiedTypeFw.reifiedType)) {
            ReifiedTypeFw.ReifiedType rt = val.getType().asVal()._UNPACK_();
            long size = rt.size;
            Object obj = val._UNPACK_();
            if (obj instanceof Byte) return new OctetBits((Byte) obj, (int) size);
            if (obj instanceof Short) return new WordBits((Short) obj, (int) size);
            if (obj instanceof Integer) return new DWordBits((Integer) obj, (int) size);
            if (obj instanceof Long) return new QWordBits((Long) obj, (int) size);
            if (obj instanceof long[]) return new MnogaBits(BitSet.valueOf((long[]) val._UNPACK_()), size);
            if (obj instanceof byte[]) return new MnogaBits(BitSet.valueOf((byte[]) val._UNPACK_()), size);
            throw new IllegalArgumentException(val._UNPACK_().toString());
        }
        return null;
    }

    public static Val wrap(Type type, Bits bits) {
        if (MemUtils.binarySize(type) != bits.size())
            throw new IllegalArgumentException(MemUtils.binarySize(type) + " != " + bits.size());

        if (bits.size() == 1) return Val._NEW_INSTANCE_(type, bits.get(0));
        if (bits instanceof OctetBits) return Val._NEW_INSTANCE_(type, ((OctetBits) bits).value);
        if (bits instanceof WordBits) return Val._NEW_INSTANCE_(type, ((WordBits) bits).value);
        if (bits instanceof DWordBits) return Val._NEW_INSTANCE_(type, ((DWordBits) bits).value);
        if (bits instanceof QWordBits) return Val._NEW_INSTANCE_(type, ((QWordBits) bits).value);

        return Val._NEW_INSTANCE_(type, bits.toLongArray());
    }

    public static Val wrap(Type type, Number number) {
        if (number instanceof Byte) return Val._NEW_INSTANCE_(type, number);
        if (number instanceof Short) return Val._NEW_INSTANCE_(type, number);
        if (number instanceof Integer) return Val._NEW_INSTANCE_(type, number);
        if (number instanceof Long) return Val._NEW_INSTANCE_(type, number);

        return wrap(type, Bits.of(BitSet.valueOf(IntType.big(number).toByteArray()), MemUtils.binarySize(type)));
    }

    public static byte[] reverseBytes(byte[] array) {
        byte[] na = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            na[array.length - i - 1] = array[i];
        }
        return na;
    }
}
