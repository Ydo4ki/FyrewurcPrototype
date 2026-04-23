package org.fw.core;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.BoolFw;
import org.fw.core.state.obj.Scope;
import org.fw.core.vit.RtEnv;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

// best tests ever
public class BoolTests {
    private static final Context context = new Context(RtEnv.unspecified, Scope.eternal());
    private static final Val b = BoolFw._true;

    @Test
    void exprBool() {
        assertEquals("true", b.toExpr(context).toString());
    }

    @Test
    void notBool() {
        assertEquals(BoolFw._false, b.call(symbol("not"), context));
    }

    @Test
    void andBool() {
        assertEquals(BoolFw._false, b.call(symbol("and"), context).call(BoolFw._false, context));
    }

    @Test
    void orBool() {
        assertEquals(BoolFw._true, b.call(symbol("or"), context).call(BoolFw._false, context));
    }

    @Test
    void xorBool() {
        assertEquals(BoolFw._true, b.call(symbol("xor"), context).call(BoolFw._false, context));
    }
}
