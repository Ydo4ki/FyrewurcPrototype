package org.fw.lib.elib;

import org.fw.core.util.FwUtils;
import org.fw.lib.elib.dvec.DVecFw;
import org.fw.lib.elib.expr.DoFw;
import org.fw.lib.elib.expr.ExprFw;
import org.fw.lib.elib.expr.ExprGetFw;
import org.fw.core.state.operation.OperationFw;

public final class EssentiaLibstd {
    public static final Lib lib = FwUtils.l(EssentiaLibstd.class, Lib.combine(
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

            OperationFw.lib
    ), "operationfns");
}
