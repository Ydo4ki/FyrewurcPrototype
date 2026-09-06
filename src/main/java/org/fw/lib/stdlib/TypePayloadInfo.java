package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

public final class TypePayloadInfo {
    public static final Type typePayloadInfo = FW.telephonist_native(arg -> {
        if (FwUtils.isTypeApiCall(arg, TypePayloadInfo.typePayloadInfo)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            if (arg.getType() == SymbolFw.symbol) {
                String s = arg._UNPACK().toString();
                switch (s) {
                    case "value":
                        return instance._UNPACK(Val.class);
                }
            }
        }
        return null;
    }).asType();

    public static Type value(Val payloadInfo) {
        if (payloadInfo.getType() == typePayloadInfo)
            return payloadInfo._UNPACK(Val.class).asType();
        return null;
    }

    public static Val wrap(Type type) {
        return Val.of(typePayloadInfo, type.asVal());
    }
}
