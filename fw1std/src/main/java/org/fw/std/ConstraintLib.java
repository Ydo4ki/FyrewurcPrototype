package org.fw.std;

import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.esast.expr.Lib;

import static org.fw.core.FW.symbol;

public class ConstraintLib {
    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("?"), ConstraintFw.isSpecified)
    ));
}
