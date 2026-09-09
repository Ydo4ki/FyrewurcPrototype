package org.fw.base;

import org.fw.core.abstrait.TypedValue;
import org.fw.core.abstrait.Value;
import org.fw.core.commons.ValAdapter;
import org.fw.core.state.obj.State;
import org.fw.core.util.FwUtils;

import java.util.*;
import java.util.function.BiFunction;

public final class Val implements ValAdapter, TypedValue {
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

    @Override
    public Value get(String property) {
        return TypedValue.super.get(property);
    }

    @Override
    public Value call(Value arg) {
        return getType().callInstance(this, arg);
    }

    @Override
    public Value invoke(State state) {
        return getType().invokeInstance(this, state);
    }

    public Value call(Val arg, Val... rest) {
        return call0(arg, rest, Value::call);
    }

    public Value get(String property, String... rest) {
        return call0(property, rest, Value::get);
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public <T> T _UNPACK_() {
        return (T)getValue();
    }

    Object getValue() {
        return value;
    }

    public boolean equalsSymbol(String symbol) {
        return this.getType() == SymbolFw.symbol && this._UNPACK_().toString().equals(symbol);
    }

    @Override
    public boolean impliesEquality(Val val) {
        return this.equals(val);
    }

    // why have one unsafe
    // if we can have two
    @Deprecated
    public static Val _NEW_INSTANCE_(Type type, Object value) {
        if (value instanceof Value && !(value instanceof Val))
            throw new IllegalArgumentException("If the value is another val, it must be concrete: " + value);

        if (type instanceof Type.TelephonistType && type != ofTelephonist(0).asType())
            throw new IllegalArgumentException();

        return of(type, value);
    }

    static Val of(Type type, Object value) {
        return new Val(type, value, null);
    }

    public static Val ofTelephonist(int depth) {
        if (depth < 0)
            throw new IllegalArgumentException();
        return Type.TelephonistType.of(depth).asVal();
    }

    static Val telephonistVal(Type.TelephonistType asType) {
        return new Val(
                Type.TelephonistType.of(asType.getDepth() + 1),
                new Type.TelephonistType.Telephonist("Telephonist" + asType.getDepth(), (k) -> {
                    if (FwUtils.isTypeApiCall(k.arg(), asType)) {
                        Value instance = CallFw.getVal(k.arg());
                        Value cArg = CallFw.getArg(k.arg());

                        return instance.call(cArg); // so here we're going in the opposite direction
                    }
                    return null;
                }, state -> null

                /*, CallContract.c(arg -> {
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
        return equals0((Val) obj);
    }

    public boolean equals(Val val) {
        return val != null && (val == this || equals0(val));
    }

    private boolean equals0(Val val) {
        if (this.value.getClass() != val.value.getClass())
            return false;

        if (!Objects.equals(this.type, val.type))
            return false;

        if (this.value.getClass().isArray())
            return _arrayEquals(this.value, val.value);

        return this.value.equals(val.value);
    }

    @Override
    public int hashCode() {
        return 31 * (31 + type.hashCode()) + value.hashCode();
    }

    @Override
    public String toString() {
        if (_asType instanceof Type.TelephonistType) return _asType.toString();
        if (value instanceof Object[]) return Arrays.toString((Object[]) value);
        if (type instanceof Type.TelephonistType) {
            return value.toString();
        }
        return "Val[" +
                "type=" + type.asVal() + ", " +
                "value=" + value + ']';
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

    private <T> Value call0(T arg, T[] rest, BiFunction<Value, T, Value> function) {
        Value ret = function.apply(this, arg);
        for (T val : rest) {
            ret = function.apply(ret, val);
        }
        return ret;
    }
}
