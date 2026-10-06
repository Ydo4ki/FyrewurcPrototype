package org.fw.base;

import org.fw.core.FW;

public final class TelephonizeFw {
    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    // renamed to telephonize to avoid confusion with the type Telephonist
    public static final Val telephonize = FW.lambda("telephonize",
            cH -> FW.telephonist(
                    arg -> cH.call(magic.newInstance(CallFw.call_t, arg))));
}
