package org.fw.core.lib.comp;

import org.fw.core.FW;
import org.fw.core.FwUtils;
import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.BoolFw;
import org.fw.core.lib.ModuleFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;

public final class ModuleCEnvFw {
    public static final Type moduleCEnvFn = FW.telephonist("ModuleCEnvFn", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, ModuleCEnvFw.moduleCEnvFn, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            Val payload = instance._unpack(Val.class);
            if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
                Val exprVal = arg.call(symbol("expr"), context);
                Val compEnv = arg.call(symbol("comp-env"), context);
                Expr expr = exprVal._unpack();
                if (expr instanceof Symbol) {
                    if (ModuleFw.module.asVal().call(symbol("contains-key"), context).call(payload, context).call(exprVal, context) == BoolFw._true) {
                        Val value = payload.call(exprVal, context);
                        return VitFw.wrap(Vit.val(value));
                    }
                }
                return Val.unspecified;
            }
        }
        return Val.unspecified;
    }).asType();

    public static Val compEnv(Val module) {
        return Val.of(moduleCEnvFn, module);
    }
}
