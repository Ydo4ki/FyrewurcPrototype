package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.base.CallFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;

import org.fw.core.util.FwUtils;

final class InstancerFw {
    public static final Type instancer = FW.lambda_native("Instancer", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, InstancerFw.instancer)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            Type targetType = instance._UNPACK_();
            return Val._NEW_INSTANCE_(targetType, cArg);
        }
        return null;
    }).asType();

    public static Val mkInstancer(Type type) {
        return Val._NEW_INSTANCE_(instancer, type);
    }
}
