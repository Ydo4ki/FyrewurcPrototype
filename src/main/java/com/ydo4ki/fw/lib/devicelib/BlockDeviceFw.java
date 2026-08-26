package com.ydo4ki.fw.lib.devicelib;

import org.fw.core.base.Type;
import org.fw.lib.stdlib.DeclarationFw;
import org.fw.lib.stdlib.StructFw;
import org.fw.lib.stdlib.constraint.ConstraintFw;

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
