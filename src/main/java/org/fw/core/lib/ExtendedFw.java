package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Unspecified;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.symbol;

public final class ExtendedFw {
    public static final Type extended = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("constructor"))
            .call(Unspecified.isNot)
            .asType();

}
