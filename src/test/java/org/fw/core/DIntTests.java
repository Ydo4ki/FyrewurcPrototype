package org.fw.core;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.StrFw;
import org.fw.core.state.obj.State;
import org.fw.core.base.context.RtEnv;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DIntTests {

    private static final Context context = new Context(RtEnv.unspecified, State.eternal());

    @Test
    void test() {
        Val n = DIntFw.dint(20);
        assertEquals(DIntFw.dint(70), n.call(symbol("+"), context).call(DIntFw.dint(50), context));
        assertEquals(DIntFw.dint(10), n.call(symbol("-"), context).call(DIntFw.dint(10), context));
        assertEquals(DIntFw.dint(1000), n.call(symbol("*"), context).call(DIntFw.dint(50), context));
        assertEquals(DIntFw.dint(6), n.call(symbol("/"), context).call(DIntFw.dint(3), context));
        assertEquals(DIntFw.dint(-20), n.call(symbol("neg"), context));
    }

    @Test
    void testParse() {
        Val str = StrFw.str("553");
        assertEquals(DIntFw.dint(553), DIntFw.dint.asVal().call(symbol("parse"), context).call(str, context));
    }
}
