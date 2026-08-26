package org.fw.lib.stdlib;

import org.fw.core.base.Type;

import static org.fw.core.FW.symbol;

public final class ExtendedFw {
    public static final Type extended = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("constructor"))
            .call(ConstraintFw.isSpecified)
            .asType();

}
