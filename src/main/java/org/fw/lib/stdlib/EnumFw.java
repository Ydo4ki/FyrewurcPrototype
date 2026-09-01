package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.lib.stdlib.expr.ExprFw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class EnumFw {
    public static final Type enumeration = FW.telephonist("Enum", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, EnumFw.enumeration)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            Enum anEnum = instance._unpack();
            for (Val value : anEnum.values) {
                if (value._unpack(Val.class).equals(arg)) return value;
            }
            return null;
        }
        if (arg.equals(symbol("construct"))) {
            return FW.telephonist("Enum.constructor", (payload) -> {
                if (!payload.type().equals(DVecFw.dVec))
                    return null;
                Val[] keys = payload._unpack();
                Val[] values = new Val[keys.length];
                Type resultingType = Val.of(EnumFw.enumeration, new Enum(values)).asType();
                for (int i = 0; i < keys.length; i++) {
                    if (!keys[i].type().equals(SymbolFw.symbol))
                        return null;

                    values[i] = Val.of(resultingType, keys[i]);
                }
                return resultingType.asVal();
            });
        }
        return null;
    }).asType();

    public static Type enumeration(String... keys) {
        Val[] valuesV = new Val[keys.length];
        Type resultingType = Val.of(EnumFw.enumeration, new Enum(valuesV)).asType();
        for (int i = 0; i < keys.length; i++) {
            valuesV[i] = Val.of(resultingType, symbol(keys[i]));
        }
        return resultingType;
    }

    public static Val toExpr(Val arg, Val toExpr) {
        EnumFw.Enum value = arg._unpack();
        List<Expr> finElements = new ArrayList<>();
        finElements.add(EnumFw.enumeration.asVal().toExpr(toExpr));
        List<Expr> elements = new ArrayList<>();
        for (Val val : value.values()) {
            elements.add(val._unpack(Val.class).toExpr(toExpr));
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
                Object a = value._unpack();
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
