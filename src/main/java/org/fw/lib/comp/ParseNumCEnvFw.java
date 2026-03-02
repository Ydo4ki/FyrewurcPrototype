package org.fw.lib.comp;

import org.fw.FW;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.vit.Vit;

import static org.fw.FW.symbol;
import static org.fw.vit.Vit.val;

public final class ParseNumCEnvFw {
    public static final Val parseNumCenv = ParseStrCEnvFw.symbolMapEnv(val(FW.telephonist("parseNum", (arg1, context1) -> {
        return Vit.val(DIntFw.dint.asVal()).call(symbol("parse")).call(arg1.call(symbol("value"), context1))
                .eval(context1);
    })));
}
