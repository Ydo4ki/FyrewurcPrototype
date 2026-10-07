package org.fw.base;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;

public final class TypePayloadInfo {
    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    public static final Type typePayloadInfo = FW.lambda_native(arg -> {
        if (FwUtils.isTypeApiCall(arg, TypePayloadInfo.typePayloadInfo)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            if (arg.getType() == SymbolFw.symbol) {
                String s = magic.unpackVal(arg).toString();
                switch (s) {
                    case "value":
                        return (Val)magic.unpackVal(instance);
                }
            }
        }
        return null;
    }).asType();

    public static Type value(Val payloadInfo) {
        if (payloadInfo.getType() == typePayloadInfo)
            return ((Val) magic.unpackVal(payloadInfo)).asType();
        return null;
    }

    public static Val wrap(Type type) {
        return magic.newInstance(typePayloadInfo, type.asVal());
    }
}
