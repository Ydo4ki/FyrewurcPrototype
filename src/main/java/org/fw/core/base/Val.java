package org.fw.core.base;

import org.fw.core.base.contract.CallContract;
import org.fw.core.commons.ValAdapter;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.ToExprFn;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

import java.util.*;

import static org.fw.core.FW.symbol;

public final class Val implements ValAdapter {
    private final Type type;
    private final Object value;
    private Type _asType;

    Val(Type type, Object value, Type _asType) {
        this.type = type;
        this.value = Objects.requireNonNull(value);
        this._asType = _asType;
    }

    @Override
    public Val asVal() {
        return this;
    }

    public Type type() {
        return type;
    }

    public Type asType() {
        if (_asType == null) {
            _asType = new Type.ValType(this);
        }
        return _asType;
    }

    public Val call(Val arg) {
        return type().callInstance(this, arg);
    }

    public Val call(Val arg, Val... rest) {
        Val ret = this.call(arg);
        for (Val val : rest) {
            ret = ret.call(val);
        }
        return ret;
    }

    public Val get(String property) {
        return call(symbol(property));
    }

    public CallContract callContract() {
        return type().instanceContract(this);
    }

    public Expr toExpr(CompEnv compEnv) {
        return compEnv.toExpr(this);
    }

    @Deprecated
    public Expr toExpr_Old(Val toExpr) {
        if (type().equals(CallFw.call_t)) return ExprList.of(
                BracketsTypes.round,
                Symbol.of("Call"),
                CallFw.getVal(this).toExpr_Old(toExpr),
                CallFw.getArg(this).toExpr_Old(toExpr)
        ); // huh
        Val result = toExpr.call(ToExprFn.toExprResolve(this, toExpr));
        if (result._unpack() instanceof Expr)
            return result._unpack();

        return ExprList.of(BracketsTypes.braces);
    }

    @SuppressWarnings("unchecked")
    public <T> T _unpack() {
        return (T)value;
    }

    @SuppressWarnings("unused")
    public <T> T _unpack(Class<T> cls) {
        return _unpack();
    }

    public boolean equalsSymbol(String symbol) {
        return this.type() == SymbolFw.symbol && this._unpack().toString().equals(symbol);
    }

    public static Val of(Type type, Object value) {
        if (type instanceof Type.TelephonistType && !(value instanceof Type.TelephonistType.Telephonist))
            throw new IllegalArgumentException();
        return new Val(Objects.requireNonNull(type), value, null);
    }

    public static Val ofTelephonist(int depth) {
        return Type.TelephonistType.of(depth).asVal();
    }

    static Val telephonistVal(Type.TelephonistType asType) {
        return new Val(
                Type.TelephonistType.of(asType.getDepth() + 1),
                new Type.TelephonistType.Telephonist((arg) -> {
                    if (FwUtils.isTypeApiCall(arg, asType)) {
                        Val instance = CallFw.getVal(arg);
                        Val cArg = CallFw.getArg(arg);

                        return instance.call(cArg); // so here we're going in the opposite direction
                    }
                    return null;
                }, CallContract.c(arg -> {
                    if (FwUtils.isTypeApiCall(arg, asType)) {
                        Constraint instance = CallFw.getVal(arg);
                        Constraint cArg = CallFw.getArg(arg);

                        return instance.call(cArg);
                    }
                    return null;
                })),
                asType
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Val that = (Val) obj;
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
