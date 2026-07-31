package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.base.context.Context;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.util.FwUtils;

import java.util.Objects;

import static org.fw.core.FW.symbol;

// for things like CompEnv
public final class ChainLinkFw {
    public static final Type chainLinkType = FW.telephonist((arg, context) -> {
        if (arg.equals(symbol("constructor"))) {
            return FW.telephonist((arg1, context1) -> {
                if (!ConstraintFw.isConstraint(arg1))
                    return null;

                return Val.of(ChainLinkFw.chainLinkType, new ChainLinkTypeRecord(arg1));
            });
        }
        if (FwUtils.isTypeApiCall(arg, ChainLinkFw.chainLinkType, context)) {
            Val instanceType = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            ChainLinkTypeRecord typeInfo = instanceType._unpack();
            Type type = instanceType.asType();

            if (FwUtils.isTypeApiCall(arg, type, context)) {
                ChainLinkFw.ChainLinkRecord instance = Call.getVal(arg, context)._unpack();
                Val cArg = Call.getArg(arg, context);

                if (cArg.type().equals(SyntaxResolveFw.syntaxResolve)) {
                    Val ret = instance.resolver().call(cArg, context);

//                    if (Unspecified.isUnspecified(ret))
                    if (typeInfo.constraint.call(symbol("check"), context).call(ret, context) != BoolFw._true)
                        return instance.parentCEnv().call(cArg, context);

                    return ret;
                }
            } else if (arg.equals(symbol("builder"))) {
                return FW.telephonist("*.builder", (resolver, context1) -> {
                    return FW.telephonist(() -> "(call *.builder " + resolver.toExpr(context1) + ")", (parentCEnv, c) -> {
                        return Val.of(type, new ChainLinkFw.ChainLinkRecord(resolver, parentCEnv));
                    });
                });
            }

            return null;
        }
        return null;
    }).asType();

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

        public Expr toExpr(Context context) {
            return ExprList.of(BracketsTypes.round, Symbol.of("chain-link"), primary.toExpr(context), parent.toExpr(context));
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
