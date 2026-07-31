package org.fw.core;

import org.fw.core.base.Val;
import org.fw.core.cases.Main;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.DVecFw;
import org.fw.core.lib.StrFw;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DVecTests {

    @Test
    void apiDVecExpr() {
        Val b = DVecFw.emptyBuilder;
        assertEquals("(DVecBuilder)", b.toExpr(Main.rtEnv).toString());
        b = b.call(DIntFw.dint(50));
        assertEquals("(DVecBuilder 50)", b.toExpr(Main.rtEnv).toString());
        b = b.call(DIntFw.dint(60));
        b = b.call(StrFw.str("content"));
        b = DVecFw.dvecbf.call(b);
        assertEquals("[50 60 \"content\"]", b.toExpr(Main.rtEnv).toString());
    }

    @Test
    void apiDVec() {
        Val b = DVecFw.emptyBuilder
                .call(DIntFw.dint(50))
                .call(DIntFw.dint(60))
                .call(StrFw.str("content"));
        b = DVecFw.dvecbf.call(b);

        assertEquals(DIntFw.dint(50), b.call(DIntFw.dint(0)));
        assertEquals(DIntFw.dint(60), b.call(DIntFw.dint(1)));
        assertEquals(StrFw.str("content"), b.call(DIntFw.dint(2)));
//        assertEquals(Unspecified.unspecified, b.call(DIntFw.dint(3), context));
//        assertEquals(Unspecified.unspecified, b.call(DIntFw.dint(-1), context));

        assertEquals(DIntFw.dint(3), b.call(symbol("size"))); // lmao
    }
}
