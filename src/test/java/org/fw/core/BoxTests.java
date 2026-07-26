package org.fw.core;

import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.BoxFw;
import org.fw.core.state.obj.State;
import org.fw.core.vit.RtEnv;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoxTests {
    private static final Context context = new Context(RtEnv.unspecified, State.eternal());
    private static final Type opBox = BoxFw.newBoxType(symbol("op"), context);

    @Test
    void apiBox() {
        Val toBox = symbol("+");
        Val boxedOp = opBox.asVal().call(symbol("constructor"), context).call(toBox, context);

        // System.out.println(opBox.asVal().call(symbol("constructor"), context));
        // System.out.println(boxedOp.toExpr(context));

        assertEquals(toBox, boxedOp.call(symbol("unbox"), context));
    }
}
