package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.lib.*;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;

public class FunctionFw {
    public static final Type function_struct = StructFw.struct(
            DeclarationFw.declaration(symbol("arg-constraint"), ConstraintFw.toConstraint(ConstraintFw.constraint)),
            DeclarationFw.declaration(symbol("body"), VitFw.constraint),
            DeclarationFw.declaration(symbol("rt-env"), ConstraintFw.free)
    );

    public static final Type function = FW.telephonist((arg, context) -> {
        Val ret = function_struct.asVal().call(arg, context);
        if (arg.type().equals(ExprFw.symbol)) {
            String value = arg._unpack(Symbol.class).getValue();
            switch (value) {
                case "builder":
                    return builderWrapper(ret);
            }
        }
        if (FwUtils.isTypeApiCall(arg, FunctionFw.function, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            Val value = instance._unpack();
            if (cArg.type().equals(ExprFw.symbol)) {
                switch (cArg._unpack(Symbol.class).getValue()) {
                    case "fn-call":
                        Val constraint = value.call(symbol("arg-constraint"), context);
                        Vit body = value.call(symbol("body"), context)._unpack();
                        return FW.telephonist((arg1, context1) -> {
                            boolean qualifies = constraint.call(symbol("check"), context1).call(arg1, context) == BoolFw._true;
                            if (!qualifies) {
                                return null;
                            }

                            // this is questionable
                            Val oldRtEnv = value.call(symbol("rt-env"), context);
                            Val newRtEnv = FW.telephonist((arg2, context2) -> {
                                Val ret0 = arg1.call(arg2, context2);
                                if (Unspecified.isUnspecified(ret0)) return oldRtEnv.call(arg2, context2);
                                return ret0;
                            });
                            return OperationFw._VitOperation
                                    .call(VitFw.wrap(body), context)
//                                    .call(arg1, context);
                                    .call(newRtEnv, context);
                        });
                }
            }
        }
        return ret;
    }).asType();

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Function"), FunctionFw.function.asVal())
            ))
    ));

    private static Val builderWrapper(Val builder) {
        return FW.telephonist((arg, context) -> {
            Val ret = builder.call(arg, context);
            if (ret.type().equals(builder.type()))
                return builderWrapper(ret);
            if (ret.type() != function_struct)
                return null;
            return Val.of(function, ret); // wrap
        });
    }
}
