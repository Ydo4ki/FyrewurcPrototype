package org.fw.lib.comp;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.BoolFw;
import org.fw.lib.DVecFw;
import org.fw.lib.ModuleFw;
import org.fw.lib.VitFw;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprFw;
import org.fw.lib.expr.SyntaxResolveFw;
import org.fw.vit.Vit;

import static org.fw.FW.symbol;
import static org.fw.vit.Vit.val;

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
