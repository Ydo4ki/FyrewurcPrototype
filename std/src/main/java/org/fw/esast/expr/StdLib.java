package org.fw.esast.expr;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;
import org.fw.base.Type;
import org.fw.base.Unspecified;
import org.fw.base.Val;
import org.fw.std.VitFw;
import org.fw.esast.expr.comp.CurrentCompEnvCEnvFw;
import org.fw.esast.util.FwUtils3;
import org.fw.std.*;
import org.fw.std.dvec.DVecFw;
import org.fw.std.state.OperationLibFw;

public final class StdLib {
    private static final CompEnv somethingToExpr = CompEnv.of(FW.telephonist_native(arg -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = (Val) (Val) arg.get("passing");
            CompEnv compEnv = CompEnv.of((Val) arg.get("chain"));
            if (Unspecified.isUnspecified(val)) {
                Value value = Unspecified.getArg(val);
                Value value1 = Unspecified.getVal(val);
                return ExprFw.wrap(ExprList.of(BracketsTypes.braces,
                        Symbol.of("unspecified"),
                        (Expr) compEnv.toExpr(value1),
                        (Expr) compEnv.toExpr(value))
                );
            }
            if (val.asType() instanceof Type.TelephonistType) {
                return ExprFw.wrap(Symbol.of(val.asType().toString()));
            }
            Value value = val.getType().asVal();
            return ExprFw.wrap(ExprList.of(BracketsTypes.braces, (Expr) compEnv.toExpr(value)));
        }
        return null;
    }));

    public static final Lib lib = FwUtils3.l(StdLib.class, Lib.combine(
            Lib.ofCEnv(VitErrorFw.cantResolveAnythingCenv),
            Lib.ofCEnv(StdLib.somethingToExpr.asValue()),
            Lib.ofCEnv(CurrentCompEnvCEnvFw.currentCompEnvCenv),
            BaseFw.lib,
            VitFw.lib,
            ExprGetFw.lib,
            DIntFw.lib,
            ExprFw.lib,
            StrFw.lib,
            DVecFw.lib,
            ModuleFw.lib,
            FnCallFw.lib,
            FunctionFw.lib,
            DeclaredFw.lib,
            DeclarationFw.lib,
            CompEnvLib.lib,
            DoFw.lib,
            UseFw.lib,
            OperatorsFw.lib,
            BoxFw.lib,
            ToExprFw.lib,
            SyntaxResolveFw.lib,
            VitErrorFw.lib,
            ConstraintLib.lib,
            StructFw.lib,

            OperationLibFw.lib
    ), "operationfns.fw");

}
