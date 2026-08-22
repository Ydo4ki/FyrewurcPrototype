package org.fw.core.lib;

import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.CompEnv;

import static org.fw.core.FW.symbol;

public final class CompEnvLib {
    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("Constraint"), ConstraintFw.constraint.asVal())
    ));
}
