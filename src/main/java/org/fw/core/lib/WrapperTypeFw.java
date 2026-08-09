package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;

public final class WrapperTypeFw {
    
    public static final Type wrapperType = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, WrapperTypeFw.wrapperType)) {
            Val wType = Call.getVal(arg);
            arg = Call.getArg(arg);

            WrapperType wt = wType._unpack();
            Type payloadType = wt.payloadType;
            Val callsHandler = wt.callsHandler;
            if (FwUtils.isTypeApiCall(arg, wType.asType())) {
                Val instanceOfWt = Call.getVal(arg);
                arg = Call.getArg(arg);

                // since we know the core type anyway there's no reason to create additional nesting levels
                // so the value of wrapper type instance is exactly the same as the one in the wrapped type
                Val rawPayload = Val.of(payloadType, instanceOfWt._unpack());
                return callsHandler.call(rawPayload).call(arg);
            } else {
                Val staticCallsHandler = wt.staticCallsHandler;
                Val ret = staticCallsHandler.call(arg);
                if (!Unspecified.isUnspecified(ret)) return ret;
                if (arg.type() == SymbolFw.symbol) {
                    String sym = arg._unpack().toString();
                    switch (sym) {
                        case "Payload":
                            return payloadType.asVal();
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
