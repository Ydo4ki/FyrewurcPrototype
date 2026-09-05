package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.BoolFw;
import org.fw.core.base.CallFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.lib.stdlib.expr.ToExprFn;
import org.fw.core.util.FwUtils;

import java.util.Objects;

import static org.fw.core.FW.symbol;

// for things like CompEnv
public final class ChainLinkFw {
    public static final Type chainLinkType = FW.telephonist((arg) -> {
        if (arg.equalsSymbol("construct")) {
            return FW.telephonist((arg1) -> {
                if (!ConstraintFw.isConstraint(arg1))
                    return null;

                return Val.of(ChainLinkFw.chainLinkType, new ChainLinkTypeRecord(arg1));
            });
        }
        if (FwUtils.isTypeApiCall(arg, ChainLinkFw.chainLinkType)) {
            Val instanceType = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            ChainLinkTypeRecord typeInfo = instanceType._unpack();
            Type type = instanceType.asType();

            if (FwUtils.isTypeApiCall(arg, type)) {
                ChainLinkFw.ChainLinkRecord instance = CallFw.getVal(arg)._unpack();
                Val cArg = CallFw.getArg(arg);

                Val ret = instance.resolver().call(cArg);

//                    if (Unspecified.isUnspecified(ret))
                if (typeInfo.constraint.call(symbol("check")).call(ret) != BoolFw._true)
                    return instance
                            .parentCEnv()
                            .call(cArg);

                return ret;
//                if (cArg.type().equals(SyntaxResolveFw.syntaxResolve)) {
//
//                }
            } else if (arg.equalsSymbol("builder")) {
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


    public static Val chain(Type type, Val parent, Val primary) {
        return type.asVal().call(symbol("builder")).call(primary).call(parent);
    }


    public static Val chain(Type type, Val... links) {
        int i = 0;
        Val actual = links[i++];
        while (actual == null) {
            if (i == links.length) return FW.telephonist(a -> null);
            actual = links[i++];
        }
        for (; i < links.length; i++) {
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
            this.primary = Objects.requireNonNull(primary);
            this.parent = Objects.requireNonNull(parent);
        }

        public Val resolver() {
            return primary;
        }

        public Val parentCEnv() {
            return parent;
        }

        public Expr toExpr(Val toExpr) {
            return ExprList.of(BracketsTypes.round, Symbol.of("chain-link"), primary.toExpr_Old(toExpr), parent.toExpr_Old(toExpr));
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
