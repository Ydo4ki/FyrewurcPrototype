package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class ExprGetFw {
    public static final Val get = FW.telephonist("get", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();

            if (isize == 0)
                return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

//            System.out.println("0: " + ret);
            for (int i = 1; i < isize; i++) {
                Val property = arg.call(DIntFw.dint(i), context);
                if (!property.type().equals(ExprFw.symbol))
                    return Val.unspecified; // not a compile error idk (actually it still is)

                retVit = VitFw.wrap(VitFw.unwrap(retVit).call(Vit.val(property)));
                // ok I''m actually not sure if that's gonna work
            }
            // nice
            return retVit;
        }
        return Val.unspecified;
    });

    public static final class DotGettersCEnvFw {
        public static final Val cenv = telephonist("dot-getters-cenv-fw", (arg, context) -> {
            if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
                Val exprVal = arg.call(symbol("expr"), context);
                Val compEnv = arg.call(symbol("comp-env"), context);
                Expr expr = exprVal._unpack();
                if (expr instanceof Symbol) {
                    Symbol sym = (Symbol) expr;
                    String fullQualifier = sym.getValue();
                    int dotIndex = fullQualifier.lastIndexOf('.');
                    if (dotIndex == -1)
                        return Val.unspecified;
                    String origin = fullQualifier.substring(0, dotIndex);
                    String property = fullQualifier.substring(dotIndex + 1);

                    Vit first = CompEnv.of(compEnv).compile(symbol(origin), context);
                    if (first == null)
                        return Val.unspecified;

                    return VitFw.wrap(first.call(symbol(property)));
                }
            }
            return Val.unspecified;
        });
    }
}
