package org.fw.lib.elib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.BoolFw;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.constraint.ConstraintFw;
import org.fw.lib.elib.expr.ExprFw;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.core.util.FwUtils;

import java.util.Objects;

import static org.fw.core.FW.symbol;

// for things like CompEnv
public final class ChainLinkFw {
    public static final Type chainLinkType = FW.telephonist((arg) -> {
        if (arg.equals(symbol("constructor"))) {
            return FW.telephonist((arg1) -> {
                if (!ConstraintFw.isConstraint(arg1))
                    return null;

                return Val.of(ChainLinkFw.chainLinkType, new ChainLinkTypeRecord(arg1));
            });
        }
        if (FwUtils.isTypeApiCall(arg, ChainLinkFw.chainLinkType)) {
            Val instanceType = Call.getVal(arg);
            arg = Call.getArg(arg);
            ChainLinkTypeRecord typeInfo = instanceType._unpack();
            Type type = instanceType.asType();

            if (FwUtils.isTypeApiCall(arg, type)) {
                ChainLinkFw.ChainLinkRecord instance = Call.getVal(arg)._unpack();
                Val cArg = Call.getArg(arg);

                Val ret = instance.resolver().call(cArg);

//                    if (Unspecified.isUnspecified(ret))
                if (typeInfo.constraint.call(symbol("check")).call(ret) != BoolFw._true)
                    return instance.parentCEnv().call(cArg);

                return ret;
//                if (cArg.type().equals(SyntaxResolveFw.syntaxResolve)) {
//
//                }
            } else if (arg.equals(symbol("builder"))) {
                return FW.telephonist("*.builder", (resolver) -> {
                    return FW.telephonist((parentCEnv) -> {
                        return Val.of(type, new ChainLinkFw.ChainLinkRecord(resolver, parentCEnv));
                    });
                });
            }

            return null;
        }
        return null;
    }).asType();
    public static final Val chainLinkToExpr = FW.telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
        if (type.asVal().type().equals(chainLinkType)) {
            ChainLinkRecord env = arg._unpack();
            return ExprFw.wrap(env.toExpr(toExpr));
        }
        return null;
    });


    public static Val chain(Type type, Val primary, Val parent) {
        return type.asVal().call(symbol("builder")).call(primary).call(parent);
    }


    public static Val chain(Type type, Val... links) {
        Val actual = links[0];
        for (int i = 1; i < links.length; i++) {
            if (links[i] == null)
                continue;
            actual = chain(type, actual, links[i]);
        }
        return actual;
    }

    public static final class ChainLinkTypeRecord {
        private final Val constraint;

        public ChainLinkTypeRecord(Val constraint) {
            this.constraint = constraint;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ChainLinkTypeRecord that = (ChainLinkTypeRecord) o;
            return Objects.equals(constraint, that.constraint);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(constraint);
        }
    }

    public static final class ChainLinkRecord {
        private final Val primary;
        private final Val parent;

        ChainLinkRecord(Val primary, Val parent) {
            this.primary = primary;
            this.parent = parent;
        }

        public Val resolver() {
            return primary;
        }

        public Val parentCEnv() {
            return parent;
        }

        public Expr toExpr(Val toExpr) {
            return ExprList.of(BracketsTypes.round, Symbol.of("chain-link"), primary.toExpr(toExpr), parent.toExpr(toExpr));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ChainLinkRecord that = (ChainLinkRecord) o;
            return Objects.equals(primary, that.primary) && Objects.equals(parent, that.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(primary, parent);
        }
    }
}
