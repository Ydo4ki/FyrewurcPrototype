package org.fw.std.type;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;

public final class UnitTypeFw {
    public static final Type unitType = FW.telephonist("UnitType", d -> {
        Value arg = d.arg();
        if (arg.equalsSymbol("cons")) {
            if (UnitTypeFw.unit == null) // initialization of the unit field itself
                return d.instance(new U());
            return UnitTypeFw.unit;
        }
        return null;
    }).asType();

    public static final Val unit = unitType.get("cons").asVal();

    private static final class U {}
}
