package org.fw.core.lib;

import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.CompEnv;

import static org.fw.core.FW.symbol;

public final class CompEnvLib {
    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Constraint"), ConstraintFw.constraint.asVal())
            ))
    ));
}
