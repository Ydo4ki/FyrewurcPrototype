package org.fw.core.lib;

import org.fw.core.lib.dvec.DVecFw;
import org.fw.core.lib.expr.DoFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.ExprGetFw;

public final class EssentiaLibstd {
    public static final Lib lib = Lib.combine(
            BaseFw.lib,
            BoolLib.lib,
            VitFw.lib,
            ExprGetFw.lib,
            DIntFw.lib,
            ExprFw.lib,
            StrFw.lib,
            DVecFw.lib,
            FnCallFw.lib,
            ModuleFw.lib,
            FunctionFw.lib,
            DeclaredFw.lib,
            CompEnvLib.lib,
            DoFw.lib,
            UseFw.lib,
            OperatorsFw.lib
    );
}
