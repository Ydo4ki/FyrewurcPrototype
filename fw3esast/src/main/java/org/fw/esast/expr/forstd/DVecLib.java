package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitUtils;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.ExprVitCompilationException;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.std.dvec.DVecBuilderFw;
import org.fw.std.dvec.DVecFw;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class DVecLib {
    public static final CompEnv dvec2exprCenv = CompEnv.of(FW.lambda_native("dvec2exprCenv", (arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = (Val) arg.get("passing");

            Type type = arg.getType();
            if (type.equals(DVecFw.dVec)) {
                Val[] vec = arg._UNPACK_();
                List<Expr> elements = new ArrayList<>();
                for (Val val : vec) {
                    elements.add(compEnv.toExpr(val));
                }
                return ExprFw.wrap(ExprList.of(BracketsTypes.square, elements));
            } else if (type.equals(DVecBuilderFw.dVecBuilder)) {
                Val[] vec = arg._UNPACK_();
                List<Expr> elements = new ArrayList<>();
                Value value = type.asVal();
                elements.add(compEnv.toExpr(value));
                for (Val val : vec) {
                    elements.add(compEnv.toExpr(val));
                }
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
            }
            return null;
        }
        return null;
    }));
    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("DVec"), DVecFw.dVec.asVal()),
                    DeclaredFw.declared(symbol("DVecBuilder"), DVecBuilderFw.dVecBuilder.asVal()),
                    DeclaredFw.declared(symbol("dvecbf"), DVecBuilderFw.dvecbf)
            ),
            CompEnv.compEnv(
                    DVecConstructorCEnvFw.dVecConstructorCenv,
                    dvec2exprCenv.asValue()
            )
    );

    public static final class DVecConstructorCEnvFw {
        public static final Val dVecConstructorCenv = FW.lambda_native("dVecConstructorCenv", (arg) -> {
            if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
                Val exprVal = (Val) arg.call(symbol("expr"));
                Val compEnv = (Val) arg.call(symbol("comp-env"));
                Expr expr = ExprFw.unwrap(exprVal);
                if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.square)) {
                    ExprList list = (ExprList) expr;
                    if (list.size() == 0) {
                        return VitFw.wrap(Vit.val(DVecBuilderFw.dvecbf.call(DVecBuilderFw.emptyBuilder)));
                    }

                    Vit ctor = Vit.val(DVecBuilderFw.emptyBuilder);
                    for (int i = 0; i < list.size(); i++) {
                        Expr f = list.get(i);
                        Val elVitVal = (Val)CompEnv.of(compEnv).compileV(ExprFw.wrap(f));
                        if (!VitFw.isVit(elVitVal.getType()))
                            return elVitVal;

                        Vit vit;
                        try {
                            vit = VitLib.unwrap(elVitVal, f);
                        } catch (ExprVitCompilationException e) {
                            throw new RuntimeException(e);
                        }
                        ctor = ctor.call(VitUtils.simplify(vit));
                    }

                    ctor = Vit.val(DVecBuilderFw.dvecbf).call(ctor);

                    return VitFw.wrap(ctor);
                }
            }
            return null;
        });
    }
}
