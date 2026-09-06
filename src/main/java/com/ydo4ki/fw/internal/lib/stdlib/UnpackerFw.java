package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;

final class UnpackerFw {
    public static final Type unpacker = FW.telephonist_native("Unpacker", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, UnpackerFw.unpacker)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            Type targetType = instance._UNPACK();
            if (!cArg.getType().equals(targetType) || !(cArg._UNPACK() instanceof Val)) {
                return null; // wrong unpacker / unsupported value / consider using boxes
            }
            return cArg._UNPACK(Val.class);
        }
        return null;
    }).asType();

    public static Val mkUnpacker(Type type) {
        return Val.of(unpacker, type);
    }
}
