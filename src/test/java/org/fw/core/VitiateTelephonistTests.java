package org.fw.core;

import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.StrFw;
import org.fw.core.lib.ValsFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.telephonist.VitiateTelephonistFw;
import org.fw.core.state.obj.Scope;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.fw.core.lib.ValsFw.eq;
import static org.fw.core.vit.Vit.*;
import static org.fw.core.vit.Vit.val;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

// V18T lmao
public class VitiateTelephonistTests {

    private static final Context context = new Context(RtEnv.unspecified, Scope.eternal());

    @Test
    void oooooo() {
        Vit src = Vit.call(ValsFw.typeGet, Vit.var(symbol("arg")));
        Val vt = VitiateTelephonistFw.vitiateTelephonist.asVal().call(symbol("builder"), context).call(VitFw.wrap(src), context)
                .call(symbol("arg"), context).call(context.rtEnv().asVal(), context);
        assertEquals("(VitiateTelephonist (VitCall (VitVal type-get) (VitCall (VitVar) (VitVal (Symbol \"arg\")))))", vt.toExpr(context).toString());
        Val ret = vt.call(DIntFw.dint(14), context);
        // oh yeah Val is too long I should've called it Vl
        assertEquals(DIntFw.dint.asVal(), ret);
        // OHHHHHHHHHHHHHHH ITS WORKING
        // AFTER YEAR OF DEVVELOPMENT WE HAVE AN OBJECT FULLY DESCRIBED IN VIT INCREDIBLE
        // /j
    }

    @Test
    void apiTestArg() {
        Vit src = Vit.var(symbol("arg"));
        Val vt = VitiateTelephonistFw.vitiateTelephonist.asVal().call(symbol("builder"), context).call(VitFw.wrap(src), context)
                .call(symbol("arg"), context).call(context.rtEnv().asVal(), context);
        Val arg = DIntFw.dint(14);
        assertEquals(arg, vt.call(arg, context));
    }

    @Test
    void apiTestThis() {
        Vit src = Vit.var(symbol("this"));
        Val vt = VitiateTelephonistFw.vitiateTelephonist.asVal().call(symbol("builder"), context).call(VitFw.wrap(src), context)
                .call(symbol("arg"), context).call(context.rtEnv().asVal(), context);
        assertEquals(vt, vt.call(Val.unspecified, context));
    }

    @Test
    void apiTestPrivate() {
        Vit src = Vit.var(symbol("private"));
        Val vt = VitiateTelephonistFw.vitiateTelephonist.asVal().call(symbol("builder"), context).call(VitFw.wrap(src), context)
                .call(symbol("arg"), context).call(context.rtEnv().asVal(), context);
        assertNotEquals(Val.unspecified, vt.call(Val.unspecified, context));
    }

    @Test
    void apiTestPrivateInstancer() {
        Vit src = Vit.var(symbol("private"));
        Val vt = VitiateTelephonistFw.vitiateTelephonist.asVal().call(symbol("builder"), context).call(VitFw.wrap(src), context)
                .call(symbol("arg"), context).call(context.rtEnv().asVal(), context);
//        System.out.println(vt.call(Val.unspecified, context).call(symbol("instancer"), context).toExpr(context));
        Val instance = vt.call(Val.unspecified, context).call(symbol("instancer"), context).call(StrFw.str("Private Value"), context);
        assertEquals(vt, instance.type().asVal());
    }

    @Test
    void apiTestPrivateUnpacker() {
        Vit src = Vit.var(symbol("private"));
        Val vt = VitiateTelephonistFw.vitiateTelephonist.asVal().call(symbol("builder"), context).call(VitFw.wrap(src), context)
                .call(Val.unspecified, context).call(context.rtEnv().asVal(), context);

        Val valToBox = StrFw.str("Private Value");

        Val instance = vt.call(Val.unspecified, context).call(symbol("instancer"), context).call(valToBox, context);
        assertEquals(valToBox, vt.call(Val.unspecified, context).call(symbol("unpacker"), context).call(instance, context));
    }

    @Test
    void typeTest() {

        Val value = StrFw.str("Hello World!");

        Vit cInstance = var(symbol("arg")).call(symbol("val"));
        Vit cArg = var(symbol("arg")).call(symbol("arg"));
        Vit unpacker = var(symbol("private")).call(symbol("unpacker"));
        Vit instancer = var(symbol("private")).call(symbol("instancer"));

        Vit src = val(eq).call(call(ValsFw.typeGet, var(symbol("arg")))).call(Call.call_t.asVal())
                .call(symbol("if"))
                .call(val(eq).call(
                                val(ValsFw.typeGet).call(cInstance))
                        .call(var(symbol("this")))
                        .call(symbol("if"))
                        .call(val(eq).call(cArg).call(symbol("unbox"))
                                .call(symbol("if"))
                                .call(unpacker.call(cInstance))
                                .call(StrFw.str("I don't know what you want from me"))
                        )
                        .call(StrFw.str("Invalid instance call"))
                )
                .call(val(eq).call(var(symbol("arg"))).call(val(symbol("constructor")))
                        .call(symbol("if"))
                        .call(instancer)
                        .call(StrFw.str("Not understood"))
                );

        Val vt = VitiateTelephonistFw.vitiateTelephonist.asVal().call(symbol("builder"), context).call(VitFw.wrap(src), context)
                .call(symbol("arg"), context).call(context.rtEnv().asVal(), context);

        Val instance = Val.of(vt.asType(), value);
        Val ret = instance.call(symbol("unbox"), context);
        assertEquals(value, ret);
    }
}
