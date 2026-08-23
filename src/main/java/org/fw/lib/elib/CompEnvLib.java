package org.fw.lib.elib;

import org.fw.lib.elib.constraint.ConstraintFw;

import static org.fw.core.FW.symbol;

public final class CompEnvLib {
    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("Constraint"), ConstraintFw.constraint.asVal())
    ));
}
