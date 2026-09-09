package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import org.fw.base.Val;
import org.fw.core.abstrait.Value;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.std.EnumFw;

import java.util.ArrayList;
import java.util.List;

public final class EnumLib {
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
}
