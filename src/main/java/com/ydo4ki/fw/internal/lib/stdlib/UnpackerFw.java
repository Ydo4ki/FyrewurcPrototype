package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;

final class UnpackerFw {
    public static final Type unpacker = FW.telephonist("Unpacker", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, UnpackerFw.unpacker)) {
            Val instance = CallFw.getVal(arg);
            Val cArg = CallFw.getArg(arg);

            Type targetType = instance._unpack();
            if (!cArg.getType().equals(targetType) || !(cArg._unpack() instanceof Val)) {
                return null; // wrong unpacker / unsupported value / consider using boxes
            }
            return cArg._unpack(Val.class);
        }
        return null;
    }).asType();

    public static Val mkUnpacker(Type type) {
        return Val.of(unpacker, type);
    }
}
