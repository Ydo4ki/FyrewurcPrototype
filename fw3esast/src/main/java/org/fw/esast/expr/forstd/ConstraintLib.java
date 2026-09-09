package org.fw.esast.expr.forstd;

import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.esast.expr.Lib;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;

import static org.fw.core.FW.symbol;

public final class ConstraintLib {
    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("?"), ConstraintFw.isSpecified)
    ));
}
