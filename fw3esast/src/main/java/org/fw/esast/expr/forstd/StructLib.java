package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.Symbol;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.std.StructFw;
import org.fw.std.VitFw;
import org.fw.std.dvec.DVecBuilderFw;
import org.fw.std.dvec.DVecFw;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class StructLib {
    public static final CompEnv directivesCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = (Val) arg.get("passing");

            Type type = arg.getType();
            if (type == StructFw.struct) {
                return toExpr(arg, compEnv);
            } else if (type.asVal().getType() == StructFw.struct) {
                return instanceToExpr(arg, compEnv);
            }
            return null;
        } else if (arg.getType().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = (Val) arg.get("passing");
            Val compEnv = (Val) arg.get("chain");
            if (val == StructFw.struct.asVal()) {
                return FW.lambda_native(c -> {
                    if (c.getType() != DVecFw.dVec)
                        return null;
                    Val[] args = c._UNPACK_();
                    if (args.length > 1)
                        return null;
                    Val b = (Val) val.get("construct");
                    for (Val arg1 : args) {
                        b = (Val) b.call(arg1);
                    }
                    return Operation.pure(b).asVal();
                });
            } else if (val.getType() == StructFw.struct) {
                int len = ((StructFw.Struct) val._UNPACK_()).fields.length;
                return FW.lambda_native(c -> {
                    if (c.getType() != DVecFw.dVec)
                        return null;
                    Val[] args = c._UNPACK_();
                    if (args.length > len)
                        return null;
                    Val b = (Val) val.get("builder");
                    for (Val arg1 : args) {
                        b = (Val) b.call(arg1);
                    }
                    return Operation.pure(b).asVal();
                });
            }
        } else if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "struct": {
                        Vit builder = Vit.val(DVecBuilderFw.emptyBuilder);
                        for (int i = 1; i < isize; i++) {
                            Expr expr1 = ((Val) exprVal.call(DIntFw.dint(i)))._UNPACK_();
                            Val val = (Val) compEnv.call(CompEnv.syntaxResolve(expr1, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(val.getType()))
                                return val;

                            builder = builder.call((Vit) val._UNPACK_());
                        }
                        builder = Vit.call(DVecBuilderFw.dvecbf, builder);

                        return VitFw.wrap(Vit.val(StructFw.struct.asVal()).call(symbol("construct")).call(builder));
                    }
                }
            }
        }
        return null;
    }));

    public static final Lib lib = Lib.of(
            StructFw.module,
            directivesCenv.asValue()
    );

    public static Val toExpr(Val arg, CompEnv toExpr) {
        StructFw.Struct value = arg._UNPACK_();
        List<Expr> finElements = new ArrayList<>();
        Value value1 = StructFw.struct.asVal();
        finElements.add(toExpr.toExpr(value1));
        List<Expr> elements = new ArrayList<>();
        for (Val val : value.fields) {
            elements.add(toExpr.toExpr(val));
        }
        finElements.add(ExprList.of(BracketsTypes.square, elements));
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, finElements));
    }

    public static Val instanceToExpr(Val arg, CompEnv toExpr) {
        Val[] value = arg._UNPACK_();
        List<Expr> elements = new ArrayList<>();
        Value value1 = arg.getType().asVal();
        elements.add(toExpr.toExpr(value1));
        for (Val val : value) {
            elements.add(toExpr.toExpr(val));
        }
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
    }
}
