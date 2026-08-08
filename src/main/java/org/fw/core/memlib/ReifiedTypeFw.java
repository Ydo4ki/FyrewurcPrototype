package org.fw.core.memlib;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

public class ReifiedTypeFw {
    public static final Type reifiedType = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, ReifiedTypeFw.reifiedType)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            ReifiedType rt = instance._unpack();
            return null;
        }
        return null;
    }).asType();

    public static Type reifiedType(Type atom_t, int size) {
        return Val.of(reifiedType, new ReifiedType(atom_t, size)).asType();
    }

    static class ReifiedType {
        private final Type atom_t;
        private final int size;

        ReifiedType(Type atomT, int size) {
            atom_t = atomT;
            this.size = size;
        }

        public Type atom_t() {
            return atom_t;
        }

        public int size() {
            return size;
        }
    }
}
