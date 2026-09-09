package org.fw.std;

import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.core.util.FwUtils;

import java.util.Objects;

import static org.fw.core.FW.symbol;

// for things like CompEnv
public final class ChainLinkFw {
    public static final Type chainLinkType = FW.lambda_native((arg) -> {
        if (arg.equalsSymbol("construct")) {
            return FW.lambda_native((arg1) -> {
                if (!ConstraintFw.isConstraint(arg1))
                    return null;

                return Val._NEW_INSTANCE_(ChainLinkFw.chainLinkType, new ChainLinkTypeRecord(arg1));
            });
        }
        if (FwUtils.isTypeApiCall(arg, ChainLinkFw.chainLinkType)) {
            Val instanceType = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            ChainLinkTypeRecord typeInfo = instanceType._UNPACK_();
            Type type = instanceType.asType();

            if (FwUtils.isTypeApiCall(arg, type)) {
                ChainLinkFw.ChainLinkRecord instance = ((Val) CallFw.getVal(arg))._UNPACK_();
                Val cArg = (Val) CallFw.getArg(arg);

                Val ret = (Val) instance.resolver().call(cArg);

//                    if (Unspecified.isUnspecified(ret))
                Val arg1 = symbol("check");
                Val val = ((Val) typeInfo.constraint.call(arg1));
                if (val.call(ret) != BoolFw._true) {
                    return instance.parentCEnv().call(cArg);
                }

                return ret;
//                if (cArg.type().equals(SyntaxResolveFw.syntaxResolve)) {
//
//                }
            } else if (arg.equalsSymbol("builder")) {
                return FW.lambda_native("*.builder", (resolver) -> {
                    return FW.lambda_native((parentCEnv) -> {
                        return Val._NEW_INSTANCE_(type, new ChainLinkFw.ChainLinkRecord(resolver, parentCEnv));
                    });
                });
            }

            return null;
        }
        return null;
    }).asType();
//    public static final Val chainLinkToExpr = FW.telephonist((arg) -> {
//        if (arg.getType() != ToExprFn.toExprResolve)
//            return null;
//        Val toExpr = arg.call(symbol("chain"));
//        arg = arg.call(symbol("passing"));
//
//        Type type = arg.getType();
//        if (type.asVal().getType().equals(chainLinkType)) {
//            ChainLinkRecord env = arg._unpack();
//            return ExprFw.wrap(env.toExpr(toExpr));
//        }
//        return null;
//    });


    public static Value chain(Type type, Value parent, Value primary) {
        Val val = type.asVal();
        return val.get("builder").call(primary).call(parent);
    }


    public static Value chain(Type type, Value... links) {
        int i = 0;
        Value actual = links[i++];
        while (actual == null) {
            if (i == links.length) return FW.lambda(a -> null);
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

//        public Expr toExpr(CompEnv toExpr) {
//            return ExprList.of(BracketsTypes.round, Symbol.of("chain-link"), toExpr.toExpr(primary), toExpr.toExpr(parent));
//        }

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
