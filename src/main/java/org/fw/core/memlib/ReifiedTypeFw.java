package org.fw.core.memlib;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.dvec.DVecFw;
import org.fw.core.memlib.words.BitFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;
import org.fw.core.util.bits.Bits;

import java.util.Objects;
import java.util.WeakHashMap;

public final class ReifiedTypeFw {
    public static final Type reifiedType = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, ReifiedTypeFw.reifiedType)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            Type type = instance.asType();
            ReifiedType rt = instance._unpack();
            if (FwUtils.isTypeApiCall(arg, type)) {
                // nah
//                instance = Call.getVal(arg);
//                arg = Call.getArg(arg);
//                int elements = Math.toIntExact(rt.size);
//                Val[] vals = new Val[elements];
//                for (int i = 0; i < elements; i++) {
//                    vals[i] = getAtom(instance, i).call(arg);
//                }
//                return reify(vals);
            } else if (arg.type() == SymbolFw.symbol && arg._unpack().toString().equals("builder")) {
                return builder(instance.asType(), new Object[0]);
            }
            return null;
        } else if (arg.type() == SymbolFw.symbol) {
            String v = arg._unpack().toString();
            if (v.equals("builder"))
                return FW.telephonist(atomType -> FW.telephonist(size0 -> {
                    if (size0.type().equals(DIntFw.dint)) {
                        int size = DIntFw.unwrap0(size0).intValueExact();
                        return reifiedType(atomType.asType(), size).asVal();
                    }
                    return null;
                }));
            else if (v.equals("fn-call")) return FW.telephonist(arg1 -> {
                if (arg1.type() != DVecFw.dVec)
                    return null;

                Val[] arr = arg1._unpack();
                int size = arr.length;
                if (size != 2)
                    return null;

                if (arr[1].type() != DIntFw.dint)
                    return null;

                return Operation.pure(reifiedType(arr[0].asType(), DIntFw.unwrap0(arr[1]).intValueExact()).asVal()).asVal();
            });

        }
        return null;
    }).asType();

    private static final WeakHashMap<Long, Type> reifiedBits = new WeakHashMap<>();

    public static Type reifiedType(Type elementType, long size) {
        if (size == 1)
            return elementType;
        while (elementType.asVal().type() == reifiedType) {
            ReifiedType rt = elementType.asVal()._unpack();
            elementType = rt.atom_t;
            size *= rt.size;
        }
        if (elementType == BitFw.bit) {
            return reifiedBits.computeIfAbsent(size, s -> Val.of(reifiedType, new ReifiedType(BitFw.bit, s)).asType());
        }
        return Val.of(reifiedType, new ReifiedType(elementType, size)).asType();
    }

    private static Val getAtom(Val reified, int index) {
        Type type = reified.type();
        if (type.asVal().type() != ReifiedTypeFw.reifiedType)
            throw new IllegalStateException();

        Type atomType = type.asVal()._unpack(ReifiedType.class).atom_t;

        long atomBSize = MemUtils.binarySize(atomType);
        if (atomBSize > 0) {
            Bits bits = MemUtils.toBits(reified);
            assert bits != null;
            return MemUtils.wrap(atomType, bits.getSlice(index * atomBSize, (index + 1) * atomBSize));
        } else {
            return Val.of(atomType, reified._unpack(Object[].class)[index]);
        }
    }

    public static Val reify(Val... vals) {
        Type atomType = null;
        Object[] payloads = new Object[vals.length];
        for (int i = 0; i < vals.length; i++) {
            if (atomType == null) {
                atomType = vals[i].type();
            } else if (!atomType.equals(vals[i].type())) {
                throw new IllegalStateException();
            }
            payloads[i] = vals[i]._unpack();
        }
        Type reifiedType = reifiedType(atomType, vals.length);
        return reify0(reifiedType, payloads);
    }

    private static Val reify0(Type reifiedType, Object[] payloads) {
//        System.out.println(Arrays.toString(payloads));
        Object data = payloads;
        long bsize = MemUtils.binarySize(reifiedType);
        if (bsize > 0) {
            if (payloads.length <= 8 && bsize == payloads.length) {
                byte result = 0;
                for (int i = 0; i < payloads.length; i++) {
                    if (payloads[i].equals(BitFw.bit1._unpack())) {
                        result |= (byte) (1 << (payloads.length - 1 - i));
                    }
                }
                data = result;
            } else {
                throw new UnsupportedOperationException("TODO");
            }
        }
        return Val.of(reifiedType, data);
    }

    static class ReifiedType {
        final Type atom_t;
        final long size;

        ReifiedType(Type atomT, long size) {
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
                    return reify0(builder.rt, na);
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
