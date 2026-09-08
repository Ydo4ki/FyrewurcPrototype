package org.fw.std;

import org.fw.base.CallFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;

import org.fw.core.abstrait.Value;
import org.fw.std.dvec.DVecFw;
import org.fw.core.util.FwUtils;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class EnumFw {
    public static final Type enumeration = FW.telephonist_native("Enum", (d) -> {
        Val arg = d.arg();
        if (FwUtils.isTypeApiCall(arg, EnumFw.enumeration)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            Enum anEnum = instance._UNPACK_();
            for (Val value : anEnum.values) {
                if (((Val) value._UNPACK_()).equals(arg)) return value;
            }
            return null;
        }
        if (arg.equalsSymbol("construct")) {
            return FW.lambda_native("Enum.construct", (payload) -> {
                if (!payload.getType().equals(DVecFw.dVec))
                    return null;
                Val[] keys = payload._UNPACK_();
                Val[] values = new Val[keys.length];
                Type resultingType = Val._NEW_INSTANCE_(EnumFw.enumeration, new Enum(values)).asType();
                for (int i = 0; i < keys.length; i++) {
                    if (!keys[i].getType().equals(SymbolFw.symbol))
                        return null;

                    values[i] = Val._NEW_INSTANCE_(resultingType, keys[i]);
                }
                return resultingType.asVal();
            });
        }
        return null;
    }).asType();

    public static Type enumeration(String... keys) {
        Val[] valuesV = new Val[keys.length];
        Type resultingType = Val._NEW_INSTANCE_(EnumFw.enumeration, new Enum(valuesV)).asType();
        for (int i = 0; i < keys.length; i++) {
            valuesV[i] = Val._NEW_INSTANCE_(resultingType, symbol(keys[i]));
        }
        return resultingType;
    }

    public static Val toExpr(Val arg, CompEnv toExpr) {
        EnumFw.Enum value = arg._UNPACK_();
        List<Expr> finElements = new ArrayList<>();
        Value value2 = EnumFw.enumeration.asVal();
        finElements.add(toExpr.toExpr(value2));
        List<Expr> elements = new ArrayList<>();
        for (Val val : value.values()) {
            Value value1 = (Val) val._UNPACK_();
            elements.add(toExpr.toExpr(value1));
        }
        finElements.add(ExprList.of(BracketsTypes.square, elements));
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, finElements));
    }

    private static final class Enum {
        private final Val[] values;
        private final Object[] payloads;
        private final int hash;

        private Enum(Val[] values) {
            this.values = values;
            this.payloads = new Object[values.length];
            int result = 1;
            for (int i = 0; i < values.length; i++) {
                Val value = values[i];
                if (value == null) continue;
                Object a = value._UNPACK_();
                payloads[i] = a;
                result = 31 * result + (a == null ? 0 : a.hashCode());
            }
            this.hash = result;
        }

        public Val[] values() {
            return values;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Enum that = (Enum) obj;
            return Arrays.equals(this.payloads, that.payloads);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public String toString() {
            return "Enum[" +
                    "values=" + Arrays.toString(payloads) + ']';
        }

    }
}
