package org.fw.std;

import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.fw.internal.lib.ConstraintFw;

import org.fw.std.state.OperationLibFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.*;

import static org.fw.core.FW.symbol;

public final class FunctionFw {
    public static final Type function_struct = StructFw.struct(
            DeclarationFw.declaration(symbol("arg-constraint"), ConstraintFw.toConstraint(ConstraintFw.constraint)),
            DeclarationFw.declaration(symbol("body"), VitFw.isVit),
            DeclarationFw.declaration(symbol("rt-env"), ConstraintFw.isSpecified)
    );

    public static final Type function = FW.lambda_native((arg) -> {
        Val val1 = function_struct.asVal();
        Val ret = (Val) val1.call(arg);
        if (arg.getType().equals(SymbolFw.symbol)) {
            String value = SymbolFw.unwrap(arg);
            switch (value) {
                case "builder":
                    return builderWrapper(ret);
            }
        }
        if (FwUtils.isTypeApiCall(arg, FunctionFw.function)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            Val value = instance._UNPACK_();
            if (cArg.getType().equals(SymbolFw.symbol)) {
                switch (SymbolFw.unwrap(cArg)) {
                    case "fn-call":
                        Value constraint = (Val) value.get("arg-constraint");
                        Vit body = ((Val) (Val) value.get("body"))._UNPACK_();
                        return FW.lambda_native((arg1) -> {
                            boolean qualifies = constraint.get("check").call(arg1).impliesEquality(BoolFw._true);
                            if (!qualifies) {
                                return null;
                            }

                            // this is questionable
                            Value oldRtEnv = (Val) value.get("rt-env");
//                            Val newRtEnv = FW.telephonist((arg2, context2) -> {
//                                Val ret0 = arg1.call(arg2, context2);
//                                if (Unspecified.isUnspecified(ret0)) return oldRtEnv.call(arg2, context2);
//                                return ret0;
//                            });
                            Val newRtEnv = FW.lambda_native((arg2) -> {
                                if (arg2.equalsSymbol("%")) return arg1;
                                if (arg2.equalsSymbol("%self%")) return instance;
                                else return oldRtEnv.call(arg2);
                            });
                            //                                    .call(arg1, context);
                            Val val = ((Val) OperationLibFw._VitOperation.call(VitFw.wrap(body)));
                            return (Val) val.call(newRtEnv);
                        });
                }
            }
        }
        return ret;
    }).asType();

    private static Val builderWrapper(Val builder) {
        return FW.lambda_native((arg) -> {
            Val ret = (Val) builder.call((Value) arg);
            if (ret.getType().equals(builder.getType()))
                return builderWrapper(ret);
            if (ret.getType() != function_struct)
                return null;
            return Val._NEW_INSTANCE_(function, ret); // wrap
        });
    }
}
