package org.fw.lib.comp;

import org.fw.FW;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.*;
import org.fw.lib.expr.*;
import org.fw.lib.state.OperationFw;
import org.fw.state.obj.ObjSystemConsole;
import org.fw.state.obj.ObjSystemTimeMillis;
import org.fw.state.obj.ObjSystemTimeNano;
import org.fw.state.obj.Scope;
import org.fw.lib.state.StateHoleFw;
import org.fw.state.operation.Operation;
import org.fw.vit.RtEnv;
import org.fw.vit.Vit;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.fw.FW.symbol;
import static org.fw.lib.ValsFw.eq;
import static org.fw.vit.Vit.val;
import static org.fw.vit.Vit.var;

public final class InternalSymbolMapCEnvFw {

    private static final Map<String, Val> valsMap = new HashMap<>();
    static {
        valsMap.put("jval", JValConstGetter.jval);

//        valsMap.put("unspecified_call", UnspecifiedCallFw.unspecified_call.asVal());

        valsMap.put(":", DeclaredFw.colon);

        valsMap.put("systime", Val.of(StateHoleFw.statehole, new ObjSystemTimeMillis(InternalSystemContext.context.scope())));
        valsMap.put("systimenano", Val.of(StateHoleFw.statehole, new ObjSystemTimeNano(InternalSystemContext.context.scope())));
        valsMap.put("sysconsole", Val.of(StateHoleFw.statehole, new ObjSystemConsole(InternalSystemContext.context.scope())));

//        valsMap.put("unspecified-call-err", ErrFw.unspecifiedCall.asVal());
    }


    public static final Val valsCenv = symbolMapVitEnv(val(FW.telephonist("vals", (arg1, _) -> {
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
