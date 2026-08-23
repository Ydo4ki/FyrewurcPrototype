package org.fw.core;

import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.BoxFw;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoxTests {
    private static final Type opBox = BoxFw.newBoxType(symbol("op"));

    @Test
    void apiBox() {
        Val toBox = symbol("+");
        Val boxedOp = opBox.asVal().call(symbol("constructor")).call(toBox);

        // System.out.println(opBox.asVal().call(symbol("constructor"), context));
        // System.out.println(boxedOp.toExpr(context));

        assertEquals(toBox, boxedOp.call(symbol("unbox")));
    }
}
