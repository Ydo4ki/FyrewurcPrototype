package org.fw.base;

import org.fw.core.FW;

import org.fw.core.util.FwUtils;

final class InstancerFw {
    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    public static final Type instancer = FW.telephonist_native("Instancer", (d) -> {
        Val arg = d.arg();
        if (FwUtils.isTypeApiCall(arg, InstancerFw.instancer)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            Type targetType = d.unpack(instance);
            return magic.newInstance(targetType, cArg);
        }
        return null;
    }).asType();

    public static Val mkInstancer(Type type) {
        return magic.newInstance(instancer, type);
    }
}
