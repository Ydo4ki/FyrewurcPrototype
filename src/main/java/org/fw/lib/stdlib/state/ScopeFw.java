package org.fw.lib.stdlib.state;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Scope;
import org.fw.core.state.operation.CreateObjectOperation;
import org.fw.core.util.FwUtils;

public final class ScopeFw {
    public static final Type scopePointer;

    static {
        //            if (arg.implies(_Constraint.type(SymbolFw.symbol))) {
        //                if (arg.implies(_Constraint.equals(symbol("owner")).or(_Constraint.equals(symbol("new"))))) {
        //                    return _Constraint.free; // (isSpecified) todo: unify those existing constraints
        //                }
        //            }
        scopePointer = FW.telephonist_native((arg) -> {
            if (FwUtils.isTypeApiCall(arg, ScopeFw.scopePointer)) {
                Val instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);

                Scope obj = instance._UNPACK();

                if (arg.getType() == SymbolFw.symbol) {
                    String s = arg._UNPACK().toString();
                    switch (s) {
                        case "owner":
                            return obj.partOf().asVal();
                        case "new":
                            return FW.telephonist_native(value -> new CreateObjectOperation(obj, value).asVal());
                    }
                }
                return null;
            }
            return null;
        }).asType();
    }
}
