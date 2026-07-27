package org.fw.core;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.state.obj.State;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.*;

public class VitTests {
    private static final Context context = new Context(RtEnv.unspecified, State.eternal());
    private static final Val v1 = FW.telephonist("v1", (arg, context1) -> null);
    private static final Val v2 = FW.telephonist("v2", (arg, context1) -> null);

    @Test
    void apiVal() {
        Val vit = VitFw.vitVal.asVal()
                .call(symbol("constructor"), context)
                .call(v1, context);

        assertEquals(v1, vit.call(symbol("val"), context));
    }

//    @Test
//    void apiVar() {
//        Val vit = VitFw.vitVar.asVal()
//                .call(symbol("constructor"), context)
//                .call(v1, context);
//
//        assertEquals(v1, vit.call(symbol("key"), context));
//    }

    @Test
    void apiCall() {
        Val vit = VitFw.vitCall.asVal()
                .call(symbol("builder"), context)
                .call(VitFw.vitVal.asVal()
                        .call(symbol("constructor"), context)
                        .call(v1, context), context)
                .call(VitFw.vitVal.asVal()
                        .call(symbol("constructor"), context)
                        .call(v2, context), context);

        assertEquals(v1, vit.call(symbol("func"), context).call(symbol("val"), context));
        assertEquals(v2, vit.call(symbol("arg"), context).call(symbol("val"), context));
    }

    @Test
    void apiCallAlt() {
        Vit codeThatCreatesVit = Vit.val(VitFw.vitCall.asVal())
                .call(symbol("builder"))
                .call(Vit.val(VitFw.vitVal.asVal())
                        .call(symbol("constructor"))
                        .call(v1))
                .call(Vit.val(VitFw.vitVal.asVal())
                        .call(symbol("constructor"))
                        .call(v2));

        Val vit = codeThatCreatesVit.eval(context);

        assertEquals(v1, vit.call(symbol("func"), context).call(symbol("val"), context));
        assertEquals(v2, vit.call(symbol("arg"), context).call(symbol("val"), context));
    }

    // apiInvoke?
}
