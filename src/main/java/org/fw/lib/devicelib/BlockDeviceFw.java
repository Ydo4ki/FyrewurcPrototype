package org.fw.lib.devicelib;

import org.fw.core.base.Type;
import org.fw.lib.elib.DeclarationFw;
import org.fw.lib.elib.StructFw;
import org.fw.lib.elib.constraint.ConstraintFw;
import org.fw.lib.elib.expr.ToExprFn;

import static org.fw.core.FW.symbol;

public final class BlockDeviceFw {
    public static final Type blockDevice = StructFw.struct(
            DeclarationFw.declaration(symbol("size"), ConstraintFw.free),
            DeclarationFw.declaration(symbol("read"), ConstraintFw.free),
            DeclarationFw.declaration(symbol("write"), ConstraintFw.free),
            DeclarationFw.declaration(symbol("seek"), ConstraintFw.free),
            DeclarationFw.declaration(symbol("flush"), ConstraintFw.free)
    );
}
