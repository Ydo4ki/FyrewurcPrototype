package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

import java.util.Objects;
import java.util.function.Supplier;

import static org.fw.core.FW.symbol;

public final class DeclarationFw {

    // I hope it will be possible to make it a struct later
    public static final Type declaration = FW.telephonist("Declaration", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DeclarationFw.declaration)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            Declaration decl = instance._unpack();
            if (arg.equalsSymbol("key")) {
                return decl.key();
            } else if (arg.equalsSymbol("constraint")) {
                return decl.constraint();
            }
        } else if (arg.equalsSymbol("builder")) {
            return FW.telephonist("Declaration.builder", (key) -> {
                return FW.telephonist(((Supplier<String>) () -> "(call Declaration.builder " + key + ")").get().toString(), (constraint) -> {
                            if (!ConstraintFw.isConstraint(constraint))
                                return null;

                            return Val.of(DeclarationFw.declaration, new Declaration(key, constraint));
                        });
            });
        }
        return null;
    }).asType();

    public static Val getKey(Val declaration) {
        return declaration.get("key");
    }

    public static Val getConstraint(Val declaration) {
        return declaration.get("constraint");
    }

    public static Val declaration(Val key, Val constraint) {
        if (!ConstraintFw.isConstraint(constraint))
            throw new IllegalArgumentException();

        return Val.of(declaration, new Declaration(key, constraint));
    }

    public static Expr toExpr(Val arg, CompEnv toExpr) {
        return arg._unpack(DeclarationFw.Declaration.class).toExpr(toExpr);
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
                return ExprList.of(BracketsTypes.round, Symbol.of("="), key._unpack(Symbol.class), constraint.toExpr(toExpr));
            return ExprList.of(BracketsTypes.round, Symbol.of("Declaration"), key.toExpr(toExpr), constraint.toExpr(toExpr));
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

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = arg.get("passing");

            Type type = arg.getType();
            if (type.equals(DeclarationFw.declaration)) {
                Val key = arg.get("key");
                return ExprFw.wrap(toExpr(arg, compEnv));
            }
            return null;
        } else if (arg.getType().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");
            if (val == DeclarationFw.declaration.asVal()) {
                return FW.telephonist(c -> {
                    if (c.getType() != DVecFw.dVec)
                        return null;
                    Val[] args = c._unpack();
                    if (args.length > 2)
                        return null;
                    Val b = val.get("builder");
                    for (Val arg1 : args) {
                        b = b.call(arg1);
                    }
                    return Operation.pure(b).asVal();
                });
            }
        } else if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "=": {
                        if (isize != 3)
                            return VitErrorFw.rrror(f, "3 elements expected");

                        Val name = exprVal.call(DIntFw.dint(1));
                        if (!name.getType().equals(SymbolFw.symbol))
                            return VitErrorFw.rrror(ExprFw.unwrap(name), "Symbol expected"); // symbol expected

                        Val value = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._unpack(Expr.class), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(value.getType()))
                            return value; // error idk

                        return VitFw.wrap(Vit.val(declaration.asVal()).call(symbol("builder")).call(name).call(value._unpack(Vit.class)));
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
            directivesCenv.asVal()
    );
}
