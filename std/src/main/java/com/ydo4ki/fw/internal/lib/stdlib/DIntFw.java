package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.esast.Symbol;
import org.fw.core.state.obj.State;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

// lmao I completely forgot we wanted to get rid of this
public final class DIntFw {
    public static final Type dint = FW.telephonist_native("DInt", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DIntFw.dint)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            BigInteger value = unwrap(instance);
            assert value != null;
            if (cArg.getType() == SymbolFw.symbol) {
                String sym = cArg._UNPACK_().toString();
                switch (sym) {
                    case "neg":
                        return dint(value.negate());
                    case "+":
                        return bop(instance, BigInteger::add);
                    case "-":
                        return bop(instance, BigInteger::subtract);
                    case "*":
                        return bop(instance, BigInteger::multiply);
                    case "/":
                        return bop(instance, BigInteger::divide);
                    case "%":
                        return bop(instance, BigInteger::mod);
                    case "<<":
                        return bop(instance, (a, b) -> b.bitLength() > 32 ? BigInteger.ZERO : a.shiftLeft(b.intValue()));
                    case ">>":
                        return bop(instance, (a, b) -> b.bitLength() > 32 ? BigInteger.ZERO : a.shiftRight(b.intValue()));
                    case "<=>":
                        return bop(instance, (a, b) -> BigInteger.valueOf(a.compareTo(b)));
                }
            }
        } else if (arg.equalsSymbol("parse")) {
            return FW.telephonist_native((arg1) -> {
                if (arg1.getType().equals(StrFw.str)) {
                    String string = arg1._UNPACK_();
                    try {
                        BigInteger i = new BigInteger(string);
                        return dint(i);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            });
        }
        return null;
    }).asType();

    public static final CompEnv dint2exprCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = (Val) (Val) arg.get("passing");
            Value compEnv = (Val) arg.get("chain");
            if (val.getType() == dint) {
                return ExprFw.wrap(Symbol.of(val._UNPACK_().toString()));
            }
        }
        return null;
    }));

    private static Val bop(Val instance, FwUtils.BigBinaryOperator operator) {
        BigInteger value = unwrap(instance);
        assert value != null;
        return FW.telephonist_native((arg1) -> {
            if (arg1.getType().equals(DIntFw.dint)) {
                BigInteger v2 = unwrap(arg1);
                return dint(operator.apply(value, v2));
            }
            return null;
        });
    }

    public static Val dint(long value) {
        return dint(BigInteger.valueOf(value));
    }

    public static Val dint(BigInteger value) {
        return Val._NEW_INSTANCE_(dint, value);
    }

    public static BigInteger unwrap(Val dint) {
        if (dint.getType().equals(DIntFw.dint)) return unwrap0(dint);
        throw new IllegalArgumentException(dint.toString());
    }

    public static BigInteger unwrap0(Val dint) {
        return dint._UNPACK_();
    }

    public static final class ParseDIntCEnvFw {
        public static final Val parseNumCenv;

        static {
            Vit parseArg = val(FW.telephonist_native("parseNum", (arg1) -> {
                return Vit.val(dint.asVal()).call(symbol("parse")).call((Val) ExprFw.symbolToString.call(arg1))
                        .eval();
            })).call(var.call(symbol("arg")).call(symbol("expr")));
            // what the heck is this
            // how's it suppose to work
            // WHY IT WORKS
            Vit body = FW.vIf(val(Unspecified.isUnspecified).call(parseArg).call(symbol("not")),
                    Vit.val(VitFw.vitVal.asVal()).call(symbol("construct"))
                            .call(parseArg),
                    parseArg
            );
            parseNumCenv = State.performAndDie(state -> FW.telephonist((arg1) -> {
                Val rtEnv = FW.telephonist((arg2) -> {
                    if (arg2.equalsSymbol("arg")) return arg1;
                    return null;
                });
                return (Val) body.eval(rtEnv, state);
            }));
        }
    }

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("DInt"), DIntFw.dint.asVal()),
                    DeclaredFw.declared(symbol("parseDIntCEnv"), ParseDIntCEnvFw.parseNumCenv)
            ),
            CompEnv.compEnv(
                    ParseDIntCEnvFw.parseNumCenv,
                    dint2exprCenv.asValue()
            )
    );
}
