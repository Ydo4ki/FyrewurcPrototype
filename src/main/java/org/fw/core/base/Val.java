package org.fw.core.base;

import org.fw.core.base.context.RtEnv;
import org.fw.core.commons.PureCallable;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

import java.util.*;

import static org.fw.core.FW.symbol;

public abstract class Val implements PureCallable<Val> {
    Val() {}

    public abstract Type type();

    protected abstract Object value();

    public abstract Type asType();

    @Override
    public Val call(Val arg) {
        return type().callInstance(this, arg);
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
        if (type().equals(Call.call_t)) return ExprList.of(
                BracketsTypes.round,
                Symbol.of("Call"),
                Call.getVal(this).toExpr(toExpr),
                Call.getArg(this).toExpr(toExpr)
        ); // huh
        Val result = toExpr.call(ToExprFn.toExprResolve(this, toExpr));
        if (result._unpack() instanceof Expr)
            return result._unpack();

        return ExprList.of(BracketsTypes.braces);
    }

    public static Val of(Type type, Object value) {
        if (type instanceof TelephonistType && !(value instanceof TelephonistType.Telephonist))
            throw new Error();
        return new Box(Objects.requireNonNull(type), value);
    }

    public static Val ofTelephonist(int depth) {
        return TelephonistVal.of(depth);
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

    public static final class Box extends Val {
        private final Type type;
        private final Object value;
        private Type _asType;

        Box(Type type, Object value) {
            this.type = type;
            this.value = value;
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
                TelephonistType.Telephonist value = this._unpack();
                if (value.representation() == null || value.representation().get() == null) return "*";
                return value.representation().get().toString();
            }
            return "Val[" +
                    "type=" + type.asVal() + ", " +
                    "value=" + value + ']';
        }
    }

    // todo: merge this with TelephonistType and make Val final class/record
    public static final class TelephonistVal extends Val {

        private final int depth;
        private TelephonistType asType;
        private Object value;

        TelephonistVal(int depth) {
            this.depth = depth;
        }

        @Override
        public Type type() {
            return of(depth + 1).asType();
        }

        @Override
        public Object value() {
            if (value == null) value = new TelephonistType.Telephonist(() -> Symbol.of(this.toString()), (arg) -> {
                if (FwUtils.isTypeApiCall(arg, asType())) {
                    Val instance = Call.getVal(arg);
                    Val cArg = Call.getArg(arg);

                    return instance.call(cArg); // so here we're going in the opposite direction
                }
                throw new RuntimeException("I have no idea when is this suppose to happen so if you see this message now you know");
//                return Unspecified.unspecified; // idk
            });
            return value;
        }

        @Override
        public Type asType() {
            if (asType == null) asType = new TelephonistType(this);
            return asType;
        }

        @Override
        public String toString() {
            return "Telephonist" + (depth == 0 ? "" : depth);
        }

        private static final List<TelephonistVal> preTelephonists = new ArrayList<>();
        public static TelephonistVal of(int depth) {
            int cs;
            while ((cs = preTelephonists.size()) <= depth) {
                preTelephonists.add(new TelephonistVal(cs));
            }
            return preTelephonists.get(depth);
        }
    }
}
