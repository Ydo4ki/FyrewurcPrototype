package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Type;
import org.fw.core.base.Unspecified;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;
import org.fw.lib.stdlib.*;
import org.fw.lib.stdlib.comp.CurrentCompEnvCEnvFw;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.state.OperationFw;

public final class StdLib {
    private static final CompEnv somethingToExpr = CompEnv.of(FW.telephonist(arg -> {
        if (arg.type().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = arg.get("passing");
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            if (Unspecified.isUnspecified(val))
                return ExprFw.wrap(ExprList.of(BracketsTypes.braces,
                        Symbol.of("unspecified"),
                        Unspecified.getVal(val).toExpr(compEnv),
                        Unspecified.getArg(val).toExpr(compEnv))
                );
            if (val.asType() instanceof Type.TelephonistType) {
                return ExprFw.wrap(Symbol.of(val.asType().toString()));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.braces, val.type().asVal().toExpr(compEnv)));
        }
        return null;
    }));

    public static final Lib lib = FwUtils.l(StdLib.class, Lib.combine(
            Lib.ofCEnv(VitErrorFw.cantResolveAnythingCenv),
            Lib.ofCEnv(StdLib.somethingToExpr.asVal()),
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
    ), "operationfns");

}
