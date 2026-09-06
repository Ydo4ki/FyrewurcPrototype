package org.fw.core.base;

import org.fw.core.commons.ValAdapter;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;

import java.util.*;
import java.util.function.BiFunction;

import static org.fw.core.FW.symbol;

public final class Val implements ValAdapter {
    private final Type type;
    private final Object value;
    private Type _asType;

    private Val(Type type, Object value, Type _asType) {
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
        this._asType = _asType;
    }

    @Override
    public Val asVal() {
        return this;
    }

    public Type getType() {
        return type;
    }

    public Type asType() {
        if (_asType == null) {
            _asType = new Type.ValType(this);
        }
        return _asType;
    }

    public Val call(Val arg) {
        return getType().callInstance(this, arg);
    }

    public Val get(String property) {
        return call(symbol(property));
    }

    public Val call(Val arg, Val... rest) {
        return call0(arg, rest, Val::call);
    }

    public Val get(String property, String... rest) {
        return call0(property, rest, Val::get);
    }

    private <T> Val call0(T arg, T[] rest, BiFunction<Val, T, Val> function) {
        Val ret = function.apply(this, arg);
        for (T val : rest) {
            ret = function.apply(ret, val);
        }
        return ret;
    }

    public Expr toExpr(CompEnv compEnv) {
        return compEnv.toExpr(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T _unpack() {
        return (T)value;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T _unpack(Class<T> cls) {
        if ((cls == Symbol.class || cls == Expr.class) && type == SymbolFw.symbol) return (T) Symbol.of((String) value);
        return _unpack();
    }

    public boolean equalsSymbol(String symbol) {
        return this.getType() == SymbolFw.symbol && this._unpack().toString().equals(symbol);
    }

    public static Val of(Type type, Object value) {
        if (type instanceof Type.TelephonistType && type != ofTelephonist(0).asType()) {
            throw new IllegalArgumentException();
        }
        return new Val(Objects.requireNonNull(type), value, null);
    }

    public static Val ofTelephonist(int depth) {
        return Type.TelephonistType.of(depth).asVal();
    }

    static Val telephonistVal(Type.TelephonistType asType) {
        return new Val(
                Type.TelephonistType.of(asType.getDepth() + 1),
                new Type.TelephonistType.Telephonist(null, (arg) -> {
                    if (FwUtils.isTypeApiCall(arg, asType)) {
                        Val instance = CallFw.getVal(arg);
                        Val cArg = CallFw.getArg(arg);

                        return instance.call(cArg); // so here we're going in the opposite direction
                    }
                    return null;
                }/*, CallContract.c(arg -> {
                    if (FwUtils.isTypeApiCall(arg, asType)) {
                        Constraint instance = CallFw.getVal(arg);
                        Constraint cArg = CallFw.getArg(arg);

                        return instance.call(cArg);
                    }
                    return null;
                })*/),
                asType
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Val that = (Val) obj;

        if (this.value.getClass() != that.value.getClass())
            return false;

        if (!Objects.equals(this.type, that.type))
            return false;

        if (this.value.getClass().isArray())
            return _arrayEquals(this.value, that.value);

        return this.value.equals(that.value);
    }

    private static boolean _arrayEquals(Object e1, Object e2) {
        if (e1 instanceof Object[]) return Arrays.deepEquals((Object[]) e1, (Object[]) e2);
        else if (e1 instanceof byte[]) return Arrays.equals((byte[]) e1, (byte[]) e2);
        else if (e1 instanceof short[]) return Arrays.equals((short[]) e1, (short[]) e2);
        else if (e1 instanceof int[]) return Arrays.equals((int[]) e1, (int[]) e2);
        else if (e1 instanceof long[]) return Arrays.equals((long[]) e1, (long[]) e2);
        else if (e1 instanceof char[]) return Arrays.equals((char[]) e1, (char[]) e2);
        else if (e1 instanceof float[]) return Arrays.equals((float[]) e1, (float[]) e2);
        else if (e1 instanceof double[]) return Arrays.equals((double[]) e1, (double[]) e2);
        else if (e1 instanceof boolean[]) return Arrays.equals((boolean[]) e1, (boolean[]) e2);
        else return e1.equals(e2);
    }

    @Override
    public int hashCode() {
        return 31 * (31 + type.hashCode()) + value.hashCode();
    }

    @Override
    public String toString() {
        if (_asType instanceof Type.TelephonistType) return _asType.toString();
        if (type == DVecFw.dVec) return Arrays.toString((Object[]) value);
        if (type == Val.ofTelephonist(0).asType()) {
            return "*";
        }
        return "Val[" +
                "type=" + type.asVal() + ", " +
                "value=" + value + ']';
    }
}
