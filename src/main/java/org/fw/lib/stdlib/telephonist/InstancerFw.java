package org.fw.lib.stdlib.telephonist;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;

final class InstancerFw {
    public static final Type instancer = FW.telephonist("Instancer", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, InstancerFw.instancer)) {
            Val instance = CallFw.getVal(arg);
            Val cArg = CallFw.getArg(arg);

            Type targetType = instance._unpack();
            return Val.of(targetType, cArg);
        }
        return null;
    }).asType();

    public static Val mkInstancer(Type type) {
        return Val.of(instancer, type);
    }
}
