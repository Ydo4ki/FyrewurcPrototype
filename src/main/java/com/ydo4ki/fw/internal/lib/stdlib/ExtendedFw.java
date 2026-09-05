package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.core.base.Type;
import org.fw.lib.stdlib.ChainLinkFw;
import org.fw.lib.stdlib.ConstraintFw;

import static org.fw.core.FW.symbol;

public final class ExtendedFw {
    public static final Type extended = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("construct"))
            .call(ConstraintFw.isSpecified)
            .asType();

}
