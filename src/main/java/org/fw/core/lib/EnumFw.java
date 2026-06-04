package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
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
    public static final Type enumeration = FW.telephonist("Enum", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, EnumFw.enumeration, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            Enum anEnum = instance._unpack();
            for (Val value : anEnum.values) {
                if (value._unpack(Val.class).equals(arg)) return value;
            }
            return Val.unspecified;
        }
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) {
                return Val.unspecified;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            return VitFw.wrap(Vit.val(EnumFw.enumeration.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist("Enum.constructor", (payload, context1) -> {
                if (!payload.type().equals(DVecFw.dVec))
                    return Val.unspecified;
                Val[] keys = payload._unpack();
                Val[] values = new Val[keys.length];
                Type resultingType = Val.of(EnumFw.enumeration, new Enum(values)).asType();
                for (int i = 0; i < keys.length; i++) {
                    if (!keys[i].type().equals(ExprFw.symbol))
                        return Val.unspecified;

                    values[i] = Val.of(resultingType, keys[i]);
                }
                return resultingType.asVal();
            });
        }
        return Val.unspecified;
    }).asType();

    public static Val toExpr(Val arg, Context context) {
        EnumFw.Enum value = arg._unpack();
        List<Expr> finElements = new ArrayList<>();
        finElements.add(EnumFw.enumeration.asVal().toExpr(context));
        List<Expr> elements = new ArrayList<>();
        for (Val val : value.values()) {
            elements.add(val._unpack(Val.class).toExpr(context));
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
