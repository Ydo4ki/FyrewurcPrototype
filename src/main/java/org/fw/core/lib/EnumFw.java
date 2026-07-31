package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.RtEnv;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class EnumFw {
    public static final Type enumeration = FW.telephonist("Enum", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, EnumFw.enumeration)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
            Enum anEnum = instance._unpack();
            for (Val value : anEnum.values) {
                if (value._unpack(Val.class).equals(arg)) return value;
            }
            return null;
        }
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) {
                return null;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            return VitFw.wrap(Vit.val(EnumFw.enumeration.asVal()).call(symbol("constructor")).call(VitFw.unwrap0(retVit)));
        } else if (arg.equals(symbol("constructor"))) {
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

    public static Val toExpr(Val arg, RtEnv rtEnv) {
        EnumFw.Enum value = arg._unpack();
        List<Expr> finElements = new ArrayList<>();
        finElements.add(EnumFw.enumeration.asVal().toExpr(rtEnv));
        List<Expr> elements = new ArrayList<>();
        for (Val val : value.values()) {
            elements.add(val._unpack(Val.class).toExpr(rtEnv));
        }
        finElements.add(ExprList.of(BracketsTypes.square, elements));
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, finElements));
    }

    private static final class Enum {
        private final Val[] values;

        private Enum(Val[] values) {
            this.values = values;
        }

        public Val[] values() {
            return values;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Enum that = (Enum) obj;
            return Arrays.equals(this.values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash((Object[]) values);
        }

        @Override
        public String toString() {
            return "Enum[" +
                    "values=" + Arrays.toString(values) + ']';
        }

    }
}
