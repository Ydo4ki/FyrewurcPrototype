package org.fw.core.base;

import org.fw.core.base.context.RtEnv;
import org.fw.core.base.contract.Constraint;
import org.fw.core.base.contract.CallContract;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.ToExprFn;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

import java.util.*;

import static org.fw.core.FW.symbol;

public abstract class Val {
    Val() {}

    public abstract Type type();

    protected abstract Object value();

    public abstract Type asType();

    public final Val call(Val arg) {
        return type().callInstance(this, arg);
    }

    public final Val call(Val arg, Val... rest) {
        Val ret = this.call(arg);
        for (Val val : rest) {
            ret = ret.call(val);
        }
        return ret;
    }

    public CallContract callContract() {
        return type().instanceContract(this);
    }

    public Expr toExpr(CompEnv compEnv) {
        return compEnv.toExpr(this);
    }

    @Deprecated
    public Expr toExpr(RtEnv rtEnv) {
        Val toExpr = rtEnv.get(symbol("to-expr"));
        return toExpr(toExpr);
    }

    @Deprecated
    public Expr toExpr(Val toExpr) {
        if (type().equals(CallFw.call_t)) return ExprList.of(
                BracketsTypes.round,
                Symbol.of("Call"),
                CallFw.getVal(this).toExpr(toExpr),
                CallFw.getArg(this).toExpr(toExpr)
        ); // huh
        Val result = toExpr.call(ToExprFn.toExprResolve(this, toExpr));
        if (result._unpack() instanceof Expr)
            return result._unpack();

        return ExprList.of(BracketsTypes.braces);
    }

    public static Val of(Type type, Object value) {
        if (type instanceof Type.TelephonistType && !(value instanceof Type.TelephonistType.Telephonist))
            throw new Error();
        return new Box(Objects.requireNonNull(type), value);
    }

    public static Val ofTelephonist(int depth) {
        return Type.TelephonistType.of(depth).asVal();
    }

    @SuppressWarnings("unchecked")
    public <T> T _unpack() {
        return (T)value();
    }

    @SuppressWarnings("unused")
    public <T> T _unpack(Class<T> cls) {
        return _unpack();
    }

    public final Val get(String property) {
        return call(symbol(property));
    }

    static final class Box extends Val {
        private final Type type;
        private final Object value;
        private Type _asType;

        Box(Type type, Object value) {
            this.type = type;
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        protected Object value() {
            return value;
        }

        @Override
        public Type asType() {
            if (_asType == null) {
                _asType = Type.of(this);
            }
            return _asType;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Box that = (Box) obj;
            if (!Objects.equals(this.type, that.type)) return false;
            boolean valEq = Objects.equals(this.value, that.value);
            if (!valEq && this.value.getClass().isArray() && that.value.getClass().isArray()) {
                valEq = Arrays.deepEquals((Object[]) this.value, (Object[]) that.value);
            }
            return valEq;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value);
        }

        @Override
        public String toString() {
            if (type == Val.ofTelephonist(0).asType()) {
                return "*";
            }
            return "Val[" +
                    "type=" + type.asVal() + ", " +
                    "value=" + value + ']';
        }
    }

    static final class TelephonistVal extends Val {

        private final Type type;
        private final Type.TelephonistType asType;
        private final Object value;

        TelephonistVal(Type.TelephonistType asType) {
            this.asType = asType;
            this.value = new Type.TelephonistType.Telephonist((arg) -> {
                if (FwUtils.isTypeApiCall(arg, asType())) {
                    Val instance = CallFw.getVal(arg);
                    Val cArg = CallFw.getArg(arg);

                    return instance.call(cArg); // so here we're going in the opposite direction
                }
                return null;
            }, CallContract.c(arg -> {
                if (FwUtils.isTypeApiCall(arg, asType())) {
                    Constraint instance = CallFw.getVal(arg);
                    Constraint cArg = CallFw.getArg(arg);

                    return instance.call(cArg);
                }
                return null;
            }));
            this.type = Type.TelephonistType.of(getDepth() + 1);
        }

        public int getDepth() {
            return asType.getDepth();
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public Type asType() {
            return asType;
        }

        @Override
        public String toString() {
            return asType.toString();
        }
    }
}
