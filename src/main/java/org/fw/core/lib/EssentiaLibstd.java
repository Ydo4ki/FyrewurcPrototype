package org.fw.core.lib;

import org.fw.core.lib.dvec.DVecFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.DoFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.ExprGetFw;

public final class EssentiaLibstd {
    public static final Lib lib = Lib.of(
            CompEnv.of(CompEnv.compEnv(BaseFw.exports.asVal(),
                    BoolLib.lib.exports(),
                    VitFw.exports.asVal(),
                    ExprGetFw.getterCEnv,
                    DIntFw.exports.asVal(),
                    ExprFw.exports.asVal(),
                    StrFw.exports.asVal(),
                    DVecFw.exports.asVal(),
                    FnCallFw.fnCallCenv.asVal(),
                    ModuleFw.exports.asVal(),
                    FunctionFw.exports.asVal(),
                    DeclaredFw.exports.asVal(),
                    CompEnvLib.exports.asVal(),
                    DoFw.exports.asVal(),
                    UseFw.useDirectivesCenv.asVal(),
                    OperatorsFw.exports.asVal()
            ))
    );
}
