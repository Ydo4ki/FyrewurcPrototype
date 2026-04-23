package org.fw.core;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.DVecFw;
import org.fw.core.lib.StrFw;
import org.fw.core.state.obj.Scope;
import org.fw.core.vit.RtEnv;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DVecTests {

    private static final Context context = new Context(RtEnv.unspecified, Scope.eternal());

    @Test
    void apiDVecExpr() {
        Val b = DVecFw.emptyBuilder;
        assertEquals("(DVecBuilder)", b.toExpr(context).toString());
        b = b.call(DIntFw.dint(50), context);
        assertEquals("(DVecBuilder 50)", b.toExpr(context).toString());
        b = b.call(DIntFw.dint(60), context);
        b = b.call(StrFw.str("content"), context);
        b = DVecFw.dvecbf.call(b, context);
        assertEquals("(DVec 50 60 \"content\")", b.toExpr(context).toString());
    }

    @Test
    void apiDVec() {
        Val b = DVecFw.emptyBuilder
                .call(DIntFw.dint(50), context)
                .call(DIntFw.dint(60), context)
                .call(StrFw.str("content"), context);
        b = DVecFw.dvecbf.call(b, context);

        assertEquals(DIntFw.dint(50), b.call(DIntFw.dint(0), context));
        assertEquals(DIntFw.dint(60), b.call(DIntFw.dint(1), context));
        assertEquals(StrFw.str("content"), b.call(DIntFw.dint(2), context));
        assertEquals(Val.unspecified, b.call(DIntFw.dint(3), context));
        assertEquals(Val.unspecified, b.call(DIntFw.dint(-1), context));

        assertEquals(DIntFw.dint(3), b.call(symbol("size"), context)); // lmao
    }
}
