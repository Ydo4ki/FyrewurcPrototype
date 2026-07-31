package org.fw.core;

import org.fw.core.base.Val;
import org.fw.core.cases.Main;
import org.fw.core.lib.BoolFw;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

// best tests ever
public class BoolTests {
    private static final Val b = BoolFw._true;

    @Test
    void exprBool() {
        assertEquals("true", b.toExpr(Main.rtEnv).toString());
    }

    @Test
    void notBool() {
        assertEquals(BoolFw._false, b.call(symbol("not")));
    }

    @Test
    void andBool() {
        assertEquals(BoolFw._false, b.call(symbol("and")).call(BoolFw._false));
    }

    @Test
    void orBool() {
        assertEquals(BoolFw._true, b.call(symbol("or")).call(BoolFw._false));
    }

    @Test
    void xorBool() {
        assertEquals(BoolFw._true, b.call(symbol("xor")).call(BoolFw._false));
    }
}
