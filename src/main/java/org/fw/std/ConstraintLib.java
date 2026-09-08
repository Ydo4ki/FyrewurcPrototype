package org.fw.std;

import org.fw.esast.expr.Lib;

import static org.fw.core.FW.symbol;

public class ConstraintLib {
    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("?"), ConstraintFw.isSpecified)
    ));
}
