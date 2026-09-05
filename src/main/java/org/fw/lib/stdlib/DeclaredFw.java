package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.commons.ValAdapter;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.vit.Vit;
import org.fw.lib.stdlib.expr.*;

import java.util.Objects;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class DeclaredFw {

//    public static final Val colon = FW.telephonist(":", (arg, context) -> {
//        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
//            Val size = arg.call(symbol("size"), context);
//            Val cEnv = arg.call(symbol("comp-env"), context);
//
//            int isize = size._unpack(BigInteger.class).intValue();
//            if (isize != 2)
//                return Val.unspecified;
//
//            Val name = arg.call(DIntFw.dint(0), context);
//            if (!name.type().equals(ExprFw.symbol))
//                return Val.unspecified; // symbol expected
//
//            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
//            if (!VitFw.isVit(value.type())) return value; // error idk
//
//            try {
//                return VitFw.wrap(Vit.val(DeclaredFw.declared.asVal()).call(symbol("builder")).call(name).call(VitFw.unwrap(value)));
//            } catch (VitCompilationException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return Val.unspecified;
//    });

    // I hope it will be possible to make it a struct later
    public static final Type declared = FW.telephonist("Declared", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DeclaredFw.declared)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            Declared decl = instance._unpack();
            if (arg.equalsSymbol("key")) {
                return decl.key();
            } else if (arg.equalsSymbol("value")) {
                return decl.value();
            }
        } else if (arg.equalsSymbol("builder")) {
            return FW.telephonist("Declared.builder",
                    (name) -> FW.telephonist(
                            (value) -> declared(name, value)));
        }
        return null;
    }).asType();
    public static final Val declaredToExpr = FW.telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
        if (type.equals(declared)) {
            Expr expr = toExpr(arg, toExpr);
            return ExprFw.wrap(expr);
        }
        return null;
    });

    public static Val getKey(Val declared) {
        return declared.call(symbol("key"));
    }

    public static Val getValue(Val declared) {
        return declared.call(symbol("value"));
    }


    public static Val declared(ValAdapter key, ValAdapter value) {
        return Val.of(DeclaredFw.declared, new Declared(key.asVal(), value.asVal()));
    }

    public static Expr toExpr(Val arg, Val toExpr) {
        return arg._unpack(DeclaredFw.Declared.class).toExpr(toExpr);
    }

    private static final class Declared {
        private final Val key;
        private final Val value;

        private Declared(Val key, Val value) {
            this.key = key;
            this.value = value;
        }

        public Expr toExpr(Val toExpr) {
            return ExprList.of(BracketsTypes.round, Symbol.of("Declared"), key.toExpr_Old(toExpr), value.toExpr_Old(toExpr));
        }

        public Val key() {
            return key;
        }

        public Val value() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Declared that = (Declared) obj;
            return Objects.equals(this.key, that.key) &&
                    Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "Declared[" +
                    "key=" + key + ", " +
                    "value=" + value + ']';
        }
    }

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case ":": {
                        if (isize != 3)
                            return VitErrorFw.rrror(f, "3 elements expected");

                        Val name = exprVal.call(DIntFw.dint(1));
                        if (!name.type().equals(SymbolFw.symbol))
                            return VitErrorFw.rrror(ExprFw.unwrap(name), "Symbol expected"); // symbol expected

                        Val value = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._unpack(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(value.type()))
                            return value; // error idk

                        return VitFw.wrap(Vit.val(declared.asVal()).call(symbol("builder")).call(name).call(value._unpack(Vit.class)));
                    }
                }
            }
        }
        return null;
    }));

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Declared"), DeclaredFw.declared.asVal())
            ),
            DeclaredFw.directivesCenv.asVal()
    );
}
