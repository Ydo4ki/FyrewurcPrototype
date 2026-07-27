package org.fw.core.lib;

import static org.fw.core.FW.symbol;
import static org.fw.core.lib.ValsFw.eq;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.telephonist.VitiateTelephonistFw;
import org.fw.core.state.obj.State;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

public final class StrFw {
    public static final Type str = FW.telephonist("Str", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, StrFw.str, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            String value = instance._unpack();
            assert value != null;
            if (cArg.equals(symbol("sub"))) {
                return FW.telephonist((start, context1) -> {
                    if (start.type() != DIntFw.dint)
                        return Val.unspecified;
                    int s = start._unpack(BigInteger.class).intValue();

                    return FW.telephonist((end, context2) -> {
                        if (end.type() != DIntFw.dint)
                            return Val.unspecified;
                        int e = end._unpack(BigInteger.class).intValue();

                        try {
                            return str(value.substring(s, e));
                        } catch (StringIndexOutOfBoundsException ee) {
                            return Val.unspecified;
                        }
                    });
                });
            } if (cArg.equals(symbol("size"))) {
                return DIntFw.dint(value.length());
            }
        }
        return Val.unspecified;
    }).asType();

    public static Val str(String string) {
        return Val.of(str, string);
    }

    public static final class ParseStrCEnvFw {
        private static final Context context = new Context(RtEnv.unspecified, State.eternal());

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
            s = s.replace("\\n", "\n");
            if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
                return str(s.substring(1, s.length() - 1)); // uh okay
            }
            return Val.unspecified;
        })));
    }

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Str"), StrFw.str.asVal()),
                    DeclaredFw.declared(symbol("expr2str"), FW.telephonist((arg, context) -> {
                        if (ExprFw.isExpr(arg)) {
                            return StrFw.str(arg._unpack().toString());
                        }
                        return Val.unspecified;
                    }))
            )),
            ParseStrCEnvFw.parseStrCenv
    ));
}
