package org.fw.lib.comp;

import org.fw.FW;
import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.StrFw;
import org.fw.lib.VitFw;
import org.fw.lib.telephonist.VitiateTelephonistFw;
import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;
import org.fw.vit.Vit;

import static org.fw.FW.symbol;
import static org.fw.lib.ValsFw.eq;
import static org.fw.vit.Vit.val;
import static org.fw.vit.Vit.var;

public final class ParseStrCEnvFw {
    private static final Context context = new Context(RtEnv.unspecified, Scope.eternal());

    private static final Vit arg = var(symbol("arg"));
    private static final Vit argExpr = arg.call(symbol("expr"));
    private static final Vit argCEnv = arg.call(symbol("comp-env"));

    public static Val symbolMapEnv(Vit telemap) {
        Vit parseArg = telemap.call(argExpr);
        return VitiateTelephonistFw.vitiate(
                FW.vIf(val(eq).call(parseArg).call(Val.unspecified).call(symbol("not")),
                        Vit.val(VitFw.vitVal.asVal()).call(symbol("constructor"))
                                .call(parseArg),
                        val(Val.unspecified)
                ), symbol("arg"), context);
    }

    public static final Val parseStrCenv = symbolMapEnv(val(FW.telephonist("parseNum", (arg1, context1) -> {
        Val str = arg1.call(symbol("value"), context1);
        if (!str.type().equals(StrFw.str))
            return Val.unspecified;

        String s = str._unpack();
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return StrFw.str(s.substring(1, s.length() - 1)); // uh okay
        }
        return Val.unspecified;
    })));

}
