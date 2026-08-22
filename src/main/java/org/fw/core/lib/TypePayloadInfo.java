package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

public final class TypePayloadInfo {
    public static final Type typePayloadInfo = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, TypePayloadInfo.typePayloadInfo)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            if (arg.type() == SymbolFw.symbol) {
                String s = arg._unpack().toString();
                switch (s) {
                    case "value":
                        return instance._unpack(Val.class);
                }
            }
        }
        return null;
    }).asType();

    public static Type value(Val payloadInfo) {
        if (payloadInfo.type() == typePayloadInfo)
            return payloadInfo._unpack(Val.class).asType();
        return null;
    }

    public static Val wrap(Type type) {
        return Val.of(typePayloadInfo, type.asVal());
    }
}
