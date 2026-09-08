package org.fw.esast.expr;

import org.fw.std.ConstraintFw;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;

import static org.fw.core.FW.symbol;

// I still don't get it
// this class is just a joke at this point
public final class CompEnvLib {
    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("Constraint"), ConstraintFw.constraint.asVal())
    ));
}
