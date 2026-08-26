package org.fw.lib.stdlib;

import org.fw.lib.stdlib.constraint.ConstraintFw;

import static org.fw.core.FW.symbol;

// I still don't get it
// this class is just a joke at this point
public final class CompEnvLib {
    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("Constraint"), ConstraintFw.constraint.asVal())
    ));
}
