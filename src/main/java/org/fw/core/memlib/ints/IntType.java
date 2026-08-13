package org.fw.core.memlib.ints;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.lib.*;
import org.fw.core.lib.constraint.ConstraintFw;

import static org.fw.core.FW.symbol;

public final class IntType {

    public static final Type signedness = EnumFw.enumeration("signed", "unsigned");
    public static final Type endian = EnumFw.enumeration("big", "little");
    public static final Type overflow = EnumFw.enumeration("wrap", "saturate", "trap");

    public static final Type int_t_payload = StructFw.struct(
            DeclarationFw.declaration(symbol("bitsize"), ConstraintFw.toConstraint(DIntFw.dint)),
            DeclarationFw.declaration(symbol("signedness"), ConstraintFw.toConstraint(signedness)),
            DeclarationFw.declaration(symbol("endian"), ConstraintFw.toConstraint(endian)),
            DeclarationFw.declaration(symbol("overflow"), ConstraintFw.toConstraint(overflow))
    );

    public static final Type int_t = WrapperTypeFw.wrapperType(int_t_payload, FW.telephonist(raw_payload -> FW.telephonist(arg -> {
        return null;
    })), FW.telephonist(arg -> {
        return null;
    }));
}
