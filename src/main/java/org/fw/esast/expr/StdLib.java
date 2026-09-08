package org.fw.esast.expr;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.core.FW;
import org.fw.esast.extern.BracketsTypes;
import org.fw.esast.extern.ExprList;
import org.fw.esast.extern.Symbol;
import org.fw.base.Type;
import org.fw.base.Unspecified;
import org.fw.base.Val;
import org.fw.core.util.FwUtils;
import org.fw.esast.expr.comp.CurrentCompEnvCEnvFw;
import org.fw.std.*;
import org.fw.std.dvec.DVecFw;
import org.fw.std.state.OperationFw;

public final class StdLib {
    private static final CompEnv somethingToExpr = CompEnv.of(FW.telephonist_native(arg -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = (Val) (Val) arg.get("passing");
            CompEnv compEnv = CompEnv.of((Val) arg.get("chain"));
            if (Unspecified.isUnspecified(val))
                return ExprFw.wrap(ExprList.of(BracketsTypes.braces,
                        Symbol.of("unspecified"),
                        Unspecified.getVal(val).toExpr(compEnv),
                        Unspecified.getArg(val).toExpr(compEnv))
                );
            if (val.asType() instanceof Type.TelephonistType) {
                return ExprFw.wrap(Symbol.of(val.asType().toString()));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.braces, val.getType().asVal().toExpr(compEnv)));
        }
        return null;
    }));

    public static final Lib lib = FwUtils.l(StdLib.class, Lib.combine(
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

            OperationFw.lib
    ), "operationfns.fw");

}
