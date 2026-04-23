package org.fw.core.lib.telephonist;

import org.fw.core.FW;
import org.fw.core.FwUtils;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.state.obj.Scope;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;

// sorry this is the best name I could come up with
public final class VitiateTelephonistFw {

    // I think we're gonna replace this with regular telephonist
    // although
    // ok i think we need to fix the
    // yeah we need to fix Vit constructors
    // so vitiate telephonist won't be needed at all as we can just call these trees

    private static final Val builder = FW.telephonist("(get VitiateTelephonist builder)", (arg0, ctx) -> {
        if (!VitFw.isVit(arg0.type()))
            return Val.unspecified;

        Vit vit = arg0._unpack();

        return FW.telephonist("(call (get VitiateTelephonist builder) " + VitFw.wrap(vit).toExpr(ctx) + ")", (arg, context) -> {
            return FW.telephonist("(call (get VitiateTelephonist builder) " + VitFw.wrap(vit).toExpr(ctx) + " " + arg.toExpr(context) + ")", (parentRtEnv, context1) -> {
                return Val.of(VitiateTelephonistFw.vitiateTelephonist, new VitiateTelephonist(vit, arg, RtEnv.of(parentRtEnv), context1.scope()));
            });
        });
    });

    private record VitiateTelephonist(Vit src, Val argKey, RtEnv parentRtEnv, Scope parentScope) {}

    /*
     * VitiateTelephonist {
     *   Vit src;
     *   callHandle() -> src.evaluate(privateRtEnv)
     * }
     * RtEnv {
     *   arg -> reference to the given argument
     *   this -> reference to this Val
     *   private ->
     *       .instancer - creates an instance of this object as a type with the given value as a playload
     *       .unpack - gives a payload of an instance of this object
     *       * LEAVE THIS FOR TYPE META *
     *       ~~.src - gives a Vit source of itself~~
     * }
     *
     * */
    @Insightful
    public static final Type vitiateTelephonist = FW.telephonist("VitiateTelephonist", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, VitiateTelephonistFw.vitiateTelephonist, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            VitiateTelephonist vt = instance._unpack(VitiateTelephonist.class);
            Vit src = vt.src();
            Val varKey = vt.argKey();
            RtEnv parentRtEnv = vt.parentRtEnv();
            Scope parentScope = vt.parentScope();

            Val privateSpace = FW.telephonist(".private", (arg1, context1) -> {
                if (arg1.equals(symbol("instancer"))) {
                    return InstancerFw.mkInstancer(instance.asType(), instance, "instancer");
                }
                if (arg1.equals(symbol("unpacker"))) {
                    return UnpackerFw.mkUnpacker(instance.asType(), instance, "unpacker");
                }
                return Val.unspecified;
            });

            Val env = FW.telephonist("vitiate-telephonist-runtime-env", (arg1, context1) -> {
                if (arg1.equals(symbol("this"))) {
                    return instance;
                } else if (arg1.equals(varKey)) {
                    return cArg;
                } else if (arg1.equals(symbol("private"))) {
                    return privateSpace;
                }
                return parentRtEnv.get(arg1, context1);
            });
            // no something is wrong
            return Scope.performAndDie(parentScope, scope -> src.eval(new Context(RtEnv.of(env), scope)));
        } else if (arg.equals(symbol("builder"))) {
            return builder;
        } /*else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(VitiateTelephonistFw.vitiateTelephonist))
                return Val.unspecified;

            Val src = VitFw.wrap(instance._unpack(VitiateTelephonist.class).src());

            // ok this looks weird as hell
            // especially considering that the final way to create this will be just something like (VitiateTelephonist <src>)
//            return ExprFw.wrap(ExprList.of(BracketsTypes.round,
//                    Symbol.of("call"),
//                    ExprList.of(BracketsTypes.round,
//                            Symbol.of("get"),
//                            Symbol.of("VitiateTelephonist"),
//                            Symbol.of("constructor")
//                    ),
//                    src.toExpr(context)
//            ));
            return ExprFw.wrap(ExprList.of(BracketsTypes.round,
                    Symbol.of("VitiateTelephonist"),// ....ok
                    src.toExpr(context)
            ));

            // the day of weird music
        }*/

        // well actually we might expose source in toExpr
        // at least for now

        return Val.unspecified;
    }).asType();

    public static Val vitiate(Vit src, Val varKey, Context context) {
        return vitiateTelephonist.asVal().call(symbol("builder"), context).call(VitFw.wrap(src), context).call(varKey, context).call(context.rtEnv().asVal(), context);
    }


    // ok rule number 1
    // don't think about constraints till you actually add them

    // rule number 2
    // don't think about side effects until you start adding them

    // if we have some external function there's no way to check any constraints or side effect that it does
    // so there must be a possibility to just "trust" that function
    // but that means we'll have situations where the trusted functions turn out to be not worth trusting
    // so we mark them as trusted in some "external" space
    // so the internal space can blindly trust them and only the external space will see the actual side effects
    // so if some function behaves not the way we expected the internal space just brings us back to the external
    // where the handler is located

    // so the process is alive

    // and no global state is created

}
