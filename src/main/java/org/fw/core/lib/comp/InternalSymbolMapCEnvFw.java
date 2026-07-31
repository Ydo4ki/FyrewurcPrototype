package org.fw.core.lib.comp;

import org.fw.core.FW;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Unspecified;
import org.fw.core.base.Val;
import org.fw.core.base.context.Context;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.j.JValConstGetter;
import org.fw.core.vit.Vit;

import java.util.HashMap;
import java.util.Map;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

@Deprecated
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


    public static final Val valsCenv = symbolMapVitEnv(val(FW.telephonist("vals", (arg1) -> {
        if (!arg1.type().equals(SymbolFw.symbol))
            return null;
        String string = arg1._unpack().toString();
        Val ret = valsMap.get(string);
        if (ret != null)
            return VitFw.wrap(val(ret));
        return null;
    })));

    public static Val symbolMapVitEnv(Vit telemap) {
        Vit arg = var(symbol("arg"));
        Vit argExpr = arg.call(symbol("expr"));
        Vit parseArg = telemap.call(argExpr);
        return FW.telephonist((arg1) -> {
            if (Unspecified.isUnspecified(arg1)) return null;
            else return parseArg.eval(Context.outOf);
        });
//        return VitiateTelephonistFw.vitiate(
//                FW.vIf(val(eq).call(parseArg).call(null).call(symbol("not")),
//                        parseArg,
//                        val(null)
//                ), symbol("arg"), InternalSystemContext.context);
    }
}
