package org.fw.core.memlib;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.DVecFw;
import org.fw.core.memlib.words.BitFw;
import org.fw.core.util.FwUtils;

import java.util.Objects;

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
        } else if (arg.type() == SymbolFw.symbol) {
            String v = arg._unpack().toString();
            if (v.equals("builder")) return FW.telephonist(atomType -> FW.telephonist(size0 -> {
                if (size0.type().equals(DIntFw.dint)) {
                    int size = DIntFw.unwrap0(size0).intValueExact();
                    return reifiedType(atomType.asType(), size).asVal();
                }
                return null;
            }));
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ReifiedType that = (ReifiedType) o;
            return size == that.size && Objects.equals(atom_t, that.atom_t);
        }

        @Override
        public int hashCode() {
            return Objects.hash(atom_t, size);
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
