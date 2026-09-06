package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;

final class InstancerFw {
    public static final Type instancer = FW.telephonist_native("Instancer", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, InstancerFw.instancer)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            Type targetType = instance._UNPACK();
            return Val.of(targetType, cArg);
        }
        return null;
    }).asType();

    public static Val mkInstancer(Type type) {
        return Val.of(instancer, type);
    }
}
