package org.fw.core.lib.comp;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.j.JValConstGetter;
import org.fw.core.lib.telephonist.VitiateTelephonistFw;
import org.fw.core.vit.Vit;

import java.util.HashMap;
import java.util.Map;

import static org.fw.core.FW.symbol;
import static org.fw.core.lib.ValsFw.eq;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

public final class InternalSymbolMapCEnvFw {

    private static final Map<String, Val> valsMap = new HashMap<>();
    static {
        valsMap.put("jval", JValConstGetter.jval);

//        valsMap.put("unspecified_call", UnspecifiedCallFw.unspecified_call.asVal());

//        valsMap.put(":", DeclaredFw.colon);

//        valsMap.put("systime", Val.of(StateHoleFw.statehole, new ObjSystemTimeMillis(InternalSystemContext.context.scope())));
//        valsMap.put("systimenano", Val.of(StateHoleFw.statehole, new ObjSystemTimeNano(InternalSystemContext.context.scope())));
//        valsMap.put("sysconsole", Val.of(StateHoleFw.statehole, new ObjSystemConsole(InternalSystemContext.context.scope())));

//        valsMap.put("unspecified-call-err", ErrFw.unspecifiedCall.asVal());
    }


    public static final Val valsCenv = symbolMapVitEnv(val(FW.telephonist("vals", (arg1, c) -> {
        if (!arg1.type().equals(ExprFw.symbol))
            return Val.unspecified;
        String string = arg1._unpack().toString();
        Val ret = valsMap.get(string);
        if (ret != null)
            return VitFw.wrap(val(ret));
        return Val.unspecified;
    })));

    public static Val symbolMapVitEnv(Vit telemap) {
        Vit arg = var(symbol("arg"));
        Vit argExpr = arg.call(symbol("expr"));
        Vit parseArg = telemap.call(argExpr);
        return VitiateTelephonistFw.vitiate(
                FW.vIf(val(eq).call(parseArg).call(Val.unspecified).call(symbol("not")),
                        parseArg,
                        val(Val.unspecified)
                ), symbol("arg"), InternalSystemContext.context);
    }
}
