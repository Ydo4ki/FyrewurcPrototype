package org.fw.core.lib;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.state.obj.State;
import org.fw.core.util.FwUtils;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

public final class StrFw {
    public static final Type str = FW.telephonist("Str", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StrFw.str)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);

            String value = instance._unpack();
            assert value != null;
            if (cArg.equals(symbol("sub"))) {
                return FW.telephonist((start) -> {
                    if (start.type() != DIntFw.dint)
                        return null;
                    int s = start._unpack(BigInteger.class).intValue();

                    return FW.telephonist((end) -> {
                        if (end.type() != DIntFw.dint)
                            return null;
                        int e = end._unpack(BigInteger.class).intValue();

                        try {
                            return str(value.substring(s, e));
                        } catch (StringIndexOutOfBoundsException ee) {
                            return null;
                        }
                    });
                });
            } if (cArg.equals(symbol("size"))) {
                return DIntFw.dint(value.length());
            }
        }
        return null;
    }).asType();
    public static final Val strToExpr = FW.telephonist((arg) -> {
        Type type = arg.type();
        if (type.equals(str)) {
            return symbol('"' + arg._unpack(String.class) + '"');
        }
        return null;
    });

    public static Val str(String string) {
        return Val.of(str, string);
    }

    public static final class ParseStrCEnvFw {
        private static final Vit arg = var.call(symbol("arg"));
        private static final Vit argExpr = arg.call(symbol("expr"));
        private static final Vit argCEnv = arg.call(symbol("comp-env"));

        public static Val symbolMapEnv(Vit telemap) {
            Vit parseArg = telemap.call(argExpr);
            // what the heck is this
            // how's it suppose to work
            // WHY IT WORKS
            Vit body = FW.vIf(val(Unspecified.isUnspecified).call(parseArg).call(symbol("not")),
                    Vit.val(VitFw.vitVal.asVal()).call(symbol("constructor"))
                            .call(parseArg),
                    parseArg
            );
            return State.performAndDie(state -> FW.telephonist((arg1) -> body.eval(RtEnv.of(FW.telephonist((arg2) -> {
                if (arg2.equals(symbol("arg"))) return arg1;
                return null;
            })), state)));
        }

        public static final Val parseStrCenv = symbolMapEnv(val(FW.telephonist("parseNum", (arg1) -> {
            Val str = ExprFw.symbolToString.call(arg1);
            if (!str.type().equals(StrFw.str))
                return null;

            String s = str._unpack();
            s = s.replace("\\n", "\n");
            if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
                return str(s.substring(1, s.length() - 1)); // uh okay
            }
            return null;
        })));
    }

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Str"), StrFw.str.asVal()),
                    DeclaredFw.declared(symbol("expr2str"), FW.telephonist((arg) -> {
                        if (ExprFw.isExpr(arg)) {
                            return StrFw.str(arg._unpack().toString());
                        }
                        return null;
                    }))
            )),
            ParseStrCEnvFw.parseStrCenv
    ));
}
