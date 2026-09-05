package org.fw.lib.stdlib;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.lib.stdlib.expr.Lib;
import org.fw.lib.stdlib.expr.ToExprFn;
import org.fw.core.state.obj.State;
import org.fw.core.util.FwUtils;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.function.BiFunction;

public final class StrFw {
    public static final Type str = FW.telephonist("Str", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StrFw.str)) {
            Val instance = CallFw.getVal(arg);
            Val cArg = CallFw.getArg(arg);

            String value = instance._unpack();
            assert value != null;
            if (cArg.getType() == SymbolFw.symbol) {
                String s = cArg._unpack().toString();
                switch (s) {
                    case "sub": {
                        return FW.telephonist((start) -> {
                            if (start.getType() != DIntFw.dint)
                                return null;
                            int st = start._unpack(BigInteger.class).intValue();

                            return FW.telephonist((end) -> {
                                if (end.getType() != DIntFw.dint)
                                    return null;
                                int e = end._unpack(BigInteger.class).intValue();

                                try {
                                    return str(value.substring(st, e));
                                } catch (StringIndexOutOfBoundsException ee) {
                                    return null;
                                }
                            });
                        });
                    }
                    case "size": {
                        return DIntFw.dint(value.length());
                    }
                    case "+": {
                        return bop(instance, String::concat);
                    }
                }
            }
        }
        return null;
    }).asType();

    private static Val bop(Val instance, BiFunction<String, String, String> operator) {
        String value = instance._unpack();
        assert value != null;
        return FW.telephonist((arg1) -> {
            if (arg1.getType().equals(StrFw.str)) {
                String v2 = arg1._unpack();
                return str(operator.apply(value, v2));
            }
            return null;
        });
    }


    public static final Val strToExpr = FW.telephonist((arg) -> {
        if (arg.getType() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.getType();
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

        public static final Val parseStrCenv;

        static {
            Vit parseArg = val(FW.telephonist("parseNum", (arg1) -> {
                Val str1 = ExprFw.symbolToString.call(arg1);
                if (!str1.getType().equals(StrFw.str))
                    return null;

                String s = str1._unpack();
                s = s.replace("\\n", "\n");
                if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
                    return str(s.substring(1, s.length() - 1)); // uh okay
                }
                return null;
            })).call(argExpr);
            // what the heck is this
            // how's it suppose to work
            // WHY IT WORKS
            Vit body = FW.vIf(val(Unspecified.isUnspecified).call(parseArg).call(symbol("not")),
                    val(VitFw.vitVal.asVal()).call(symbol("construct"))
                            .call(parseArg),
                    parseArg
            );
            // uh okay
            parseStrCenv = State.performAndDie(state -> FW.telephonist((arg3) -> body.eval(RtEnv.of(FW.telephonist((arg2) -> {
                if (arg2.equalsSymbol("arg")) return arg3;
                return null;
            })), state)));
        }
    }

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Str"), StrFw.str.asVal()),
                    DeclaredFw.declared(symbol("expr2str"), FW.telephonist((arg) -> {
                        if (ExprFw.isExpr(arg)) {
                            return StrFw.str(arg._unpack().toString());
                        }
                        return null;
                    }))
            ),
            ParseStrCEnvFw.parseStrCenv
    );
}
