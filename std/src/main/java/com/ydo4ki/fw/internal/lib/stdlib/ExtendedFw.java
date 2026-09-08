package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.std.ChainLinkFw;
import com.ydo4ki.fw.internal.lib.ConstraintFw;

import static org.fw.core.FW.symbol;

public final class ExtendedFw {
    public static final Type extended;

    static {
        extended = ((Val) ChainLinkFw.chainLinkType.asVal()
                .call(symbol("construct"))
                .call(ConstraintFw.isSpecified))
                .asType();
    }

}
