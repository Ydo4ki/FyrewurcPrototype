package org.fw;

import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.StrFw;
import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;
import org.junit.jupiter.api.Test;

import static org.fw.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DIntTests {

    private static final Context context = new Context(RtEnv.unspecified, Scope.eternal());

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
