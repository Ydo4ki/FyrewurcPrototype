package org.fw.std;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.CallFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.commons.ValAdapter;

import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;
import org.fw.esast.expr.*;
import org.fw.esast.extern.BracketsTypes;
import org.fw.esast.extern.Expr;
import org.fw.esast.extern.ExprList;
import org.fw.esast.extern.Symbol;
import org.fw.core.vit.Vit;
import org.fw.std.dvec.DVecFw;

import java.util.Objects;

import static org.fw.core.FW.symbol;

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
    public static final Type declared = FW.telephonist_native("Declared", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DeclaredFw.declared)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Declared decl = instance._UNPACK_();
            if (arg.equalsSymbol("key")) {
                return decl.key();
            } else if (arg.equalsSymbol("value")) {
                return decl.value();
            }
        } else if (arg.equalsSymbol("builder")) {
            return FW.telephonist_native("Declared.builder",
                    (name) -> FW.telephonist_native(
                            (value) -> declared(name, value)));
        }
        return null;
    }).asType();

    public static Val getKey(Val declared) {
        return (Val) declared.call(symbol("key"));
    }

    public static Val getValue(Val declared) {
        return (Val) declared.call(symbol("value"));
    }


    public static Val declared(ValAdapter key, ValAdapter value) {
        return Val._NEW_INSTANCE_(DeclaredFw.declared, new Declared(key.asVal(), value.asVal()));
    }

    public static Expr toExpr(Val arg, CompEnv toExpr) {
        return arg._UNPACK_(DeclaredFw.Declared.class).toExpr(toExpr);
    }

    private static final class Declared {
        private final Val key;
        private final Val value;

        private Declared(Val key, Val value) {
            this.key = key;
            this.value = value;
        }

        public Expr toExpr(CompEnv toExpr) {
            if (key.getType() == SymbolFw.symbol) {
                return ExprList.of(BracketsTypes.round, Symbol.of(":"), key._UNPACK_(Symbol.class), value.toExpr(toExpr));
            }
            return ExprList.of(BracketsTypes.round, Symbol.of("Declared"), key.toExpr(toExpr), value.toExpr(toExpr));
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

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist_native("DeclaredFw.directivesCenv", (arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of((Val) arg.get("chain"));
            arg = (Val) (Val) arg.get("passing");

            Type type = arg.getType();
            if (type.equals(declared)) {
                return ExprFw.wrap(toExpr(arg, compEnv));
            }
            return null;
        } else if (arg.getType().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = (Val) (Val) arg.get("passing");
            Value compEnv = (Val) arg.get("chain");
            if (val == declared.asVal()) {
                return FW.telephonist_native(c -> {
                    if (c.getType() != DVecFw.dVec)
                        return null;
                    Val[] args = c._UNPACK_();
                    if (args.length > 2)
                        return null;
                    Val val1 = declared.asVal();
                    Value b = (Val) val1.get("builder");
                    for (Val arg1 : args) {
                        b = b.call(arg1);
                    }
                    return Operation.pure(b).asVal();
                });
            }
        }else if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = exprVal._UNPACK_(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case ":": {
                        if (isize != 3)
                            return VitErrorFw.rrror(f, "3 elements expected");

                        Val name = (Val) (Val) exprVal.call(DIntFw.dint(1));
                        if (!name.getType().equals(SymbolFw.symbol))
                            return VitErrorFw.rrror(ExprFw.unwrap(name), "Symbol expected"); // symbol expected

                        Val value = (Val) (Val) compEnv.call(CompEnv.syntaxResolve(((Val) (Val) exprVal.call(DIntFw.dint(2)))._UNPACK_(Expr.class), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(value.getType()))
                            return value; // error idk

                        return VitFw.wrap(Vit.val(declared.asVal()).call(symbol("builder")).call(name).call(value._UNPACK_(Vit.class)));
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
            DeclaredFw.directivesCenv.asValue()
    );
}
