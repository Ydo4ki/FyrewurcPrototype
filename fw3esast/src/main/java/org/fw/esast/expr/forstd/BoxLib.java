package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.ExprList;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.std.BoxFw;
import org.fw.std.DeclaredFw;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.std.ModuleFw;

import static org.fw.core.FW.symbol;

public final class BoxLib {
    public static final CompEnv box2exprCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));

            arg = (Val) arg.call(FW.symbol("passing"));

            Type type = arg.getType();
            if (type.equals(BoxFw.boxType)) {
                Value value = BoxFw.boxType.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value), compEnv.toExpr(BoxFw.unbox(arg))));
            } else if (type.asVal().getType().equals(BoxFw.boxType)) {
                Value value = type.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value), compEnv.toExpr(BoxFw.unbox(arg))));
            }
            return null;
        }
        return null;
    }));
    public static final Lib lib = Lib.of(ModuleFw.module(
                    DeclaredFw.declared(symbol("BoxType"), BoxFw.boxType)
            ),
            box2exprCenv.asValue()
    );
}
