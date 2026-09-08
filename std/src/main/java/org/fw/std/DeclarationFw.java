package org.fw.std;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.*;
import org.fw.core.FW;

import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.esast.expr.*;
import org.fw.std.dvec.DVecFw;
import org.fw.core.util.FwUtils;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;

import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class DeclarationFw {

    // I hope it will be possible to make it a struct later
    public static final Type declaration = FW.telephonist_native("Declaration", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DeclarationFw.declaration)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Declaration decl = instance._UNPACK_();
            if (arg.equalsSymbol("key")) {
                return decl.key();
            } else if (arg.equalsSymbol("constraint")) {
                return decl.constraint();
            }
        } else if (arg.equalsSymbol("builder")) {
            return FW.telephonist_native("Declaration.builder", (key) -> {
                return FW.telephonist_native("(call Declaration.builder " + key + ")", (constraint) -> {
                            if (!ConstraintFw.isConstraint(constraint))
                                return null;

                            return Val._NEW_INSTANCE_(DeclarationFw.declaration, new Declaration(key, constraint));
                        });
            });
        }
        return null;
    }).asType();

    public static Val getKey(Val declaration) {
        return (Val) declaration.get("key");
    }

    public static Val getConstraint(Val declaration) {
        return (Val) declaration.get("constraint");
    }

    public static Val declaration(Val key, Val constraint) {
        if (!ConstraintFw.isConstraint(constraint))
            throw new IllegalArgumentException();

        return Val._NEW_INSTANCE_(declaration, new Declaration(key, constraint));
    }

    public static Expr toExpr(Val arg, CompEnv toExpr) {
        return arg._UNPACK_(Declaration.class).toExpr(toExpr);
    }

    private static final class Declaration {
        private final Val key;
        private final Val constraint;

        private Declaration(Val key, Val constraint) {
            this.key = key;
            this.constraint = constraint;
        }

        public Expr toExpr(CompEnv toExpr) {
            if (key.getType() == SymbolFw.symbol)
                return ExprList.of(BracketsTypes.round, Symbol.of("="), ExprFw.unwrap(key), toExpr.toExpr(constraint));
            return ExprList.of(BracketsTypes.round, Symbol.of("Declaration"), toExpr.toExpr(key), toExpr.toExpr(constraint));
        }

        public Val key() {
            return key;
        }

        public Val constraint() {
            return constraint;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Declaration that = (Declaration) obj;
            return Objects.equals(this.key, that.key) &&
                    Objects.equals(this.constraint, that.constraint);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, constraint);
        }

        @Override
        public String toString() {
            return "Declaration[" +
                    "key=" + key + ", " +
                    "constraint=" + constraint + ']';
        }
    }

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = (Val) arg.get("passing");

            Type type = arg.getType();
            if (type.equals(DeclarationFw.declaration)) {
                Val key = (Val) arg.get("key");
                return ExprFw.wrap(toExpr(arg, compEnv));
            }
            return null;
        } else if (arg.getType().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = (Val) arg.get("passing");
            Val compEnv = (Val) arg.get("chain");
            if (val == DeclarationFw.declaration.asVal()) {
                return FW.telephonist_native(c -> {
                    if (c.getType() != DVecFw.dVec)
                        return null;
                    Val[] args = c._UNPACK_();
                    if (args.length > 2)
                        return null;
                    Val val1 = declaration.asVal();
                    Val b = (Val) val1.get("builder");
                    for (Val arg1 : args) {
                        b = (Val) b.call(arg1);
                    }
                    return Operation.pure(b).asVal();
                });
            }
        } else if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = (Expr) ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "=": {
                        if (isize != 3)
                            return VitErrorFw.rrror(f, "3 elements expected");

                        Val name = (Val) exprVal.call(DIntFw.dint(1));
                        if (!name.getType().equals(SymbolFw.symbol))
                            return VitErrorFw.rrror(ExprFw.unwrap(name), "Symbol expected"); // symbol expected

                        Val val = ((Val) exprVal.call(DIntFw.dint(2)));
                        Val value = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(value.getType()))
                            return value; // error idk

                        return VitFw.wrap(Vit.val(declaration.asVal()).call(symbol("builder")).call(name).call(value._UNPACK_(Vit.class)));
                    }
                }
            }
        }
        return null;
    }));

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Declaration"), declaration.asVal())
            ),
            directivesCenv.asValue()
    );
}
