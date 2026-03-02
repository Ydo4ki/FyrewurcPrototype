package org.fw;

import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.BoolFw;
import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;
import org.junit.jupiter.api.Test;

import static org.fw.FW.symbol;
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
    void orBool() {        assertEquals(BoolFw._true, b.call(symbol("or"), context).call(BoolFw._false, context));

    }

    @Test
    void xorBool() {
        assertEquals(BoolFw._true, b.call(symbol("xor"), context).call(BoolFw._false, context));
    }
}
