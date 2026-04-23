package org.fw.core.lib.comp;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;

public final class ParseNumCEnvFw {
    public static final Val parseNumCenv = ParseStrCEnvFw.symbolMapEnv(val(FW.telephonist("parseNum", (arg1, context1) -> {
        return Vit.val(DIntFw.dint.asVal()).call(symbol("parse")).call(arg1.call(symbol("value"), context1))
                .eval(context1);
    })));
}
