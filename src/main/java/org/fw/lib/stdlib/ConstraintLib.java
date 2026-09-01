package org.fw.lib.stdlib;

import org.fw.lib.stdlib.expr.Lib;

import static org.fw.core.FW.symbol;

public class ConstraintLib {
    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("?"), ConstraintFw.isSpecified)
    ));
}
