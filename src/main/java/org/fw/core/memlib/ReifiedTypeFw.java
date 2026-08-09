package org.fw.core.memlib;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DVecFw;
import org.fw.core.memlib.words.BitFw;
import org.fw.core.util.FwUtils;

public final class ReifiedTypeFw {
    public static final Type reifiedType = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, ReifiedTypeFw.reifiedType)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            ReifiedType rt = instance._unpack();
            if (arg.type() == SymbolFw.symbol && arg._unpack().toString().equals("builder")) {
                return builder(instance.asType(), new Object[0]);
            }
            return null;
        }
        return null;
    }).asType();

    public static Type reifiedType(Type atom_t, int size) {
        return Val.of(reifiedType, new ReifiedType(atom_t, size)).asType();
    }

    static class ReifiedType {
        final Type atom_t;
        final int size;

        ReifiedType(Type atomT, int size) {
            atom_t = atomT;
            this.size = size;
        }
    }


    private static final Type rtBuilder = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, ReifiedTypeFw.rtBuilder)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            RtBuilder builder = instance._unpack();
            ReifiedType rt = builder.rt.asVal()._unpack(ReifiedType.class);
            if (arg.type().equals(rt.atom_t)) {
                Object[] na = DVecFw.arAppended(builder.data, arg._unpack());
                if (na.length == rt.size) {
                    Object data = na;
                    long bsize = MemUtils.binarySize(builder.rt);
                    if (bsize > 0) {
                        if (na.length == 8 && bsize == 8) {
                            byte result = 0;
                            for (int i = 0; i < na.length; i++) {
                                if (na[i].equals(BitFw.bit1._unpack())) {
                                    result |= (byte) (1 << (na.length - 1 - i));
                                }
                            }
                            data = result;
                        } else {
                            throw new UnsupportedOperationException("TODO");
                        }
                    }
                    return Val.of(builder.rt, data);
                }
                return builder(builder.rt, na);
            }
        }
        return null;
    }).asType();

    private static Val builder(Type rt, Object[] data) {
        return Val.of(rtBuilder, new RtBuilder(rt, data));
    }

    static class RtBuilder {
        final Type rt;
        final Object[] data;

        RtBuilder(Type rt, Object[] data) {
            this.rt = rt;
            this.data = data;
        }
    }
}
