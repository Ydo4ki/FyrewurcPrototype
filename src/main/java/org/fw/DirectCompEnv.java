package org.fw;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Unspecified;
import org.fw.core.base.Val;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;
import org.fw.lib.stdlib.DeclaredFw;
import java.util.function.Function;

import static org.fw.core.FW.symbol;

public final class DirectCompEnv {

    public static Vit compile(Expr expr, Function<String, Val> get) throws VitCompilationException {
        if (expr instanceof Symbol) {
            String v = expr.toString();
            if (v.startsWith("'") && v.endsWith("'")) {
                return Vit.val(symbol(v.substring(1, v.length() - 1)));
            }
            Val ret = get.apply(v);
            if (ret == null || Unspecified.isUnspecified(ret))
                throw new VitCompilationException(expr);

            return Vit.val(ret);
        } else if (expr instanceof ExprList) {
            ExprList list = ((ExprList) expr);
            if (list.getBracketsType().equals(BracketsTypes.braces) && list.size() == 2) {
                Expr name = list.get(0);
                if (!(name instanceof Symbol))
                    throw new VitCompilationException(name, "Symbol exprected");
                Vit value = compile(list.get(1), get);
                return Vit.val(DeclaredFw.declared.asVal()).call(symbol("builder")).call(symbol(name.toString())).call(value);
            } else if (list.getBracketsType().equals(BracketsTypes.round) && list.size() > 0) {
                // (value x)
                Expr f = list.get(0);
                int isize = list.size();

                Vit ret = compile(f, get);

                for (int i = 1; i < isize; i++) {
                    Vit v = compile(list.get(i), get);
                    ret = ret.call(v);
                }

                return ret;
            }
        }
        throw new VitCompilationException(expr);
    }
}
