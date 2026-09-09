package com.ydo4ki.fw.internal.lib.stdlib;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;

import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.core.state.obj.State;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.function.BiFunction;

public final class StrFw {
    public static final Type str = FW.lambda_native("Str", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StrFw.str)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            String value = instance._UNPACK_();
            assert value != null;
            if (cArg.getType() == SymbolFw.symbol) {
                String s = cArg._UNPACK_().toString();
                switch (s) {
                    case "sub": {
                        return FW.lambda_native((start) -> {
                            if (start.getType() != DIntFw.dint)
                                return null;
                            int st = ((BigInteger) start._UNPACK_()).intValue();

                            return FW.lambda_native((end) -> {
                                if (end.getType() != DIntFw.dint)
                                    return null;
                                int e = ((BigInteger) end._UNPACK_()).intValue();

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
        String value = instance._UNPACK_();
        assert value != null;
        return FW.lambda_native((arg1) -> {
            if (arg1.getType().equals(StrFw.str)) {
                String v2 = arg1._UNPACK_();
                return str(operator.apply(value, v2));
            }
            return null;
        });
    }

    public static Val str(String string) {
        return Val._NEW_INSTANCE_(str, string);
    }

    public static final class ParseStrCEnvFw {
        private static final Vit arg = var.call(symbol("arg"));
        private static final Vit argExpr = arg.call(symbol("expr"));
        private static final Vit argCEnv = arg.call(symbol("comp-env"));

        public static final Val parseStrCenv;

        static {
            Vit parseArg = val(FW.lambda_native("parseNum", (arg1) -> {
                Value str1 = (Val) ExprFw.symbolToString.call(arg1);
                if (!str1.getTypeValue().impliesEquality(StrFw.str.asVal()))
                    return null;

                String s = ((Val)str1)._UNPACK_();
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
            parseStrCenv = State.performAndDie(state -> FW.lambda((arg3) -> {
                Val rtEnv = FW.lambda((arg2) -> {
                    if (arg2.equalsSymbol("arg")) return arg3;
                    return null;
                });
                return (Val) body.eval(rtEnv, state);
            }));
        }
    }

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Str"), StrFw.str.asVal()),
                    DeclaredFw.declared(symbol("expr2str"), FW.lambda_native((arg) -> {
                        if (ExprFw.isExpr(arg)) {
                            return StrFw.str(arg._UNPACK_().toString());
                        }
                        return null;
                    }))
            ),
            ParseStrCEnvFw.parseStrCenv
    );
}
