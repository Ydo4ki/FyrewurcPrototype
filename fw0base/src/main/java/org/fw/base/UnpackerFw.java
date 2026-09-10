package org.fw.base;

import org.fw.core.FW;

import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;

final class UnpackerFw {
    public static final Type unpacker = FW.telephonist_native("Unpacker", (d) -> {
        Val arg = d.arg();
        if (FwUtils.isTypeApiCall(arg, UnpackerFw.unpacker)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Type targetType = d.unpack(instance);
            if (!arg.getType().equals(targetType) || !(arg.getValue() instanceof Value)) {
                return null; // wrong unpacker / unsupported value / consider using boxes
            }
            return (Val) arg.getValue();
        }
        return null;
    }).asType();

    public static Val mkUnpacker(Type type) {
        return Val.of(unpacker, type);
    }
}
