package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.adapter.ValAdapter;
import org.fw.core.base.*;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;
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
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            Declared decl = instance._unpack();
            if (arg.equals(symbol("key"))) {
                return decl.key();
            } else if (arg.equals(symbol("value"))) {
                return decl.value();
            }
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2)
                return null;

            Val name = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(name.type()))
                return name; // error idk

            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(value.type())) return value; // error idk

            try {
                return VitFw.wrap(Vit.val(DeclaredFw.declared.asVal()).call(symbol("builder")).call(VitFw.unwrap(name)).call(VitFw.unwrap(value)));
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Declared.builder",
                    (name) -> FW.telephonist(
                            (value) -> declared(name, value)));
        }
        return null;
    }).asType();
    public static final Val declaredToExpr = telephonist((arg) -> {
        Type type = arg.type();
        if (type.equals(declared)) {
            Expr expr = toExpr(arg, RtEnv.unspecified);
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


    public static Val declared(Val key, ValAdapter value) {
        return declared(key, value.asVal());
    }

    public static Val declared(Val key, Val value) {
        return Val.of(DeclaredFw.declared, new Declared(key, value));
    }

    public static Expr toExpr(Val arg, RtEnv rtEnv) {
        return arg._unpack(DeclaredFw.Declared.class).toExpr(rtEnv);
    }

    private static final class Declared {
        private final Val key;
        private final Val value;

        private Declared(Val key, Val value) {
            this.key = key;
            this.value = value;
        }

        public Expr toExpr(RtEnv rtEnv) {
            return ExprList.of(BracketsTypes.round, Symbol.of("Declared"), key.toExpr(rtEnv), value.toExpr(rtEnv));
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

    public static final CompEnv directivesCenv = CompEnv.of(telephonist((arg) -> {
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
                            return null;

                        Val name = exprVal.call(DIntFw.dint(1));
                        if (!name.type().equals(SymbolFw.symbol))
                            return null; // symbol expected

                        Val value = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._unpack(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(value.type()))
                            return value; // error idk

                        return VitFw.wrap(Vit.val(declared.asVal()).call(symbol("builder")).call(name).call(VitFw.unwrap0(value)));
                    }
                }
            }
        }
        return null;
    }));

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Declared"), DeclaredFw.declared.asVal())
            )),
            DeclaredFw.directivesCenv.asVal()
    ));
}
