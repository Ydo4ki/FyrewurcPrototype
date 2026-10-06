package org.fw.base;

import org.fw.core.FW;

import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;

final class UnpackerFw {
    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    public static final Type unpacker = FW.telephonist_native("Unpacker", (d) -> {
        Val arg = d.arg();
        if (FwUtils.isTypeApiCall(arg, UnpackerFw.unpacker)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Type targetType = d.unpack(instance);
            if (!arg.getType().equals(targetType) || !(magic.unpackVal(arg) instanceof Value)) {
                return null; // wrong unpacker / unsupported value / consider using boxes
            }
            return (Val) magic.unpackVal(arg);
        }
        return null;
    }).asType();

    public static Val mkUnpacker(Type type) {
        return magic.newInstance(unpacker, type);
    }
}
