package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;

public final class WrapperTypeFw {
    
    public static final Type wrapperType = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, WrapperTypeFw.wrapperType)) {
            Val wType = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            WrapperType wt = wType._unpack();
            Type payloadType = wt.payloadType;
            Val callsHandler = wt.callsHandler;
            if (FwUtils.isTypeApiCall(arg, wType.asType())) {
                Val instanceOfWt = CallFw.getVal(arg);
                arg = CallFw.getArg(arg);

                // since we know the core type anyway there's no reason to create additional nesting levels
                // so the value of wrapper type instance is exactly the same as the one in the wrapped type
                Val rawPayload = Val.of(payloadType, instanceOfWt._unpack());
                return callsHandler.call(instanceOfWt).call(rawPayload).call(arg);
            } else {
                Val staticCallsHandler = wt.staticCallsHandler;
                Val ret = staticCallsHandler.call(arg);
                if (!Unspecified.isUnspecified(ret)) return ret;
                if (arg.getType() == SymbolFw.symbol) {
                    String sym = arg._unpack().toString();
                    switch (sym) {
                        case "Payload":
                            return TypePayloadInfo.wrap(payloadType);
                    }
                }
            }
            
            return null;
        }
        return null;
    }).asType();

    public static Type wrapperType(Type payloadType, Val callsHandler, Val staticCallsHandler) {
        return Val.of(wrapperType, new WrapperType(payloadType, callsHandler, staticCallsHandler)).asType();
    }

    public static Type unwrapFully(Type type) {
        Type payload = FW.payloadType(type);
        while (payload != null) {
            type = payload;
            payload = FW.payloadType(type);
        }
//        while (type.asVal().type().equals(WrapperTypeFw.wrapperType))
//            type = type.asVal()._unpack(WrapperTypeFw.WrapperType.class).payloadType;
        return type;
    }

    public static Val unwrapFully(Val val) {
        Type type = unwrapFully(val.getType());
        if (type != val.getType())
            return Val.of(type, val._unpack());
        return val;
    }

    public final static class WrapperType {
        public final Type payloadType;
        public final Val callsHandler;
        public final Val staticCallsHandler;

        WrapperType(Type payloadType, Val callsHandler, Val staticCallsHandler) {
            this.payloadType = payloadType;
            this.callsHandler = callsHandler;
            this.staticCallsHandler = staticCallsHandler;
        }
    }
}
