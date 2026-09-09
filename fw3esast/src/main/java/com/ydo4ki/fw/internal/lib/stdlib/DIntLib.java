package com.ydo4ki.fw.internal.lib.stdlib;

import com.ydo4ki.esast.Symbol;
import org.fw.base.Unspecified;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.state.obj.State;
import org.fw.core.vit.Vit;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

public final class DIntLib {
    public static final CompEnv dint2exprCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = (Val) (Val) arg.get("passing");
            Value compEnv = (Val) arg.get("chain");
            if (val.getType() == DIntFw.dint) {
                return ExprFw.wrap(Symbol.of(val._UNPACK_().toString()));
            }
        }
        return null;
    }));
    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("DInt"), DIntFw.dint.asVal()),
                    DeclaredFw.declared(symbol("parseDIntCEnv"), DIntLib.ParseDIntCEnvFw.parseNumCenv)
            ),
            CompEnv.compEnv(
                    DIntLib.ParseDIntCEnvFw.parseNumCenv,
                    dint2exprCenv.asValue()
            )
    );

    public static final class ParseDIntCEnvFw {
        public static final Val parseNumCenv;

        static {
            Vit parseArg = val(FW.lambda_native("parseNum", (arg1) -> {
                return Vit.val(DIntFw.dint.asVal()).call(symbol("parse")).call((Val) ExprFw.symbolToString.call(arg1))
                        .eval();
            })).call(var.call(symbol("arg")).call(symbol("expr")));
            // what the heck is this
            // how's it suppose to work
            // WHY IT WORKS
            Vit body = FW.vIf(val(Unspecified.isUnspecified).call(parseArg).call(symbol("not")),
                    Vit.val(VitFw.vitVal.asVal()).call(symbol("construct"))
                            .call(parseArg),
                    parseArg
            );
            parseNumCenv = State.performAndDie(state -> FW.lambda((arg1) -> {
                Val rtEnv = FW.lambda((arg2) -> {
                    if (arg2.equalsSymbol("arg")) return arg1;
                    return null;
                });
                return (Val) body.eval(rtEnv, state);
            }));
        }
    }
}
