package org.fw.core.base;

import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.lib.expr.ExprFw;

import java.util.*;

import static org.fw.core.FW.symbol;

public abstract class Val {
    Val() {}

    public abstract Type type();

    protected abstract Object value();

    public abstract Type asType();

    public Val call(Val arg, Context context) {
        Objects.requireNonNull(context);
        // evil
        // this is to evil
//        return Scope.performAndDie(context.scope(), scope
//                -> type().callInstance(this, arg, new Context(context.rtEnv(), scope)));
        try {
            Val v = type().callInstance(this, arg, context);
            if (v == null)
                return Unspecified.unspecified(this, arg);
            return v;
        } catch (Exception e) {
            System.out.println("UNEXPECTED EXCEPTION, AAAAA:");
            e.printStackTrace(System.out);
            return Unspecified.unspecified(this, arg);
        }
    }

    public Expr toExpr(Context context) {
        if (type().equals(Call.call_t)) return ExprList.of(
                BracketsTypes.round,
                Symbol.of("Call"),
                Call.getVal(this, context).toExpr(context),
                Call.getArg(this, context).toExpr(context)
        ); // huh
        Val function = context.rtEnv().get(symbol("to-expr"), context);
        Val result = function.call(this, context);
        if (ExprFw.isExpr(result))
            return result._unpack();

        return ExprList.of(BracketsTypes.braces);
//        return type().instanceToExpr(this, context);
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
                return value.representation().get().toString();
            }
            return "Val[" +
                    "type=" + type + ", " +
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
            if (value == null) value = new TelephonistType.Telephonist(() -> Symbol.of(this.toString()), (arg, context) -> {
                if (FwUtils.isTypeApiCall(arg, asType(), context)) {
                    Val instance = Call.getVal(arg, context);
                    Val cArg = Call.getArg(arg, context);

                    return instance.call(cArg, context); // so here we're going in the opposite direction
                }
                return Unspecified.unspecified; // idk
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


