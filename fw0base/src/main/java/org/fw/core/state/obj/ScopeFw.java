package org.fw.core.state.obj;

import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.state.operation.CreateObjectOperation;
import org.fw.core.util.FwUtils;

public final class ScopeFw {
    public static final Type scopePointer;

    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    static {
        //            if (arg.implies(_Constraint.type(SymbolFw.symbol))) {
        //                if (arg.implies(_Constraint.equals(symbol("owner")).or(_Constraint.equals(symbol("new"))))) {
        //                    return _Constraint.free; // (isSpecified) todo: unify those existing constraints
        //                }
        //            }
        scopePointer = FW.lambda_native((arg) -> {
            if (FwUtils.isTypeApiCall(arg, ScopeFw.scopePointer)) {
                Val instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);

                Scope obj = (Scope) magic.unpackVal(instance);

                if (arg.getType() == SymbolFw.symbol) {
                    String s = magic.unpackVal(arg).toString();
                    switch (s) {
                        case "owner":
                            return obj.parent().asValHandle();
                        case "new":
                            return FW.lambda_native(value -> new CreateObjectOperation(obj, value).asVal());
                    }
                }
                return null;
            }
            return null;
        }).asType();
    }
}
