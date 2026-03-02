package org.fw;

import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.BoxFw;
import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;
import org.junit.jupiter.api.Test;

import static org.fw.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoxTests {
    private static final Context context = new Context(RtEnv.unspecified, Scope.eternal());
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
