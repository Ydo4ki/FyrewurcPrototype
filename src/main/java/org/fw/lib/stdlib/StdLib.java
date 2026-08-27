package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.*;
import org.fw.core.state.operation.OperationFw;

public final class StdLib {
    private static final CompEnv somethingToExpr = CompEnv.of(FW.telephonist(arg -> {
        if (arg.type().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");
            if (val instanceof Val.TelephonistVal) {
                return ExprFw.wrap(Symbol.of("Telephonist" + ((Val.TelephonistVal) val).getDepth()));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.braces, val.type().asVal().toExpr(CompEnv.of(compEnv))));
        }
        return null;
    }));

    public static final Lib lib = FwUtils.l(StdLib.class, Lib.combine(
            Lib.ofCEnv(VitErrorFw.cantResolveAnythingCenv),
            Lib.ofCEnv(StdLib.somethingToExpr.asVal()),
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
            CompEnvLib.lib,
            DoFw.lib,
            UseFw.lib,
            OperatorsFw.lib,
            BoxFw.lib,
            ToExprFw.lib,

            OperationFw.lib
    ), "operationfns");

}
