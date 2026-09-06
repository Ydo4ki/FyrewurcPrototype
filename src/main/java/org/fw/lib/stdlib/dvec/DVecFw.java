package org.fw.lib.stdlib.dvec;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.*;
import org.fw.lib.stdlib.*;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.Lib;
import org.fw.lib.stdlib.expr.SyntaxResolveFw;
import org.fw.core.util.FwUtils;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;
import org.fw.core.vit.VitUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fw.core.FW.*;

public final class DVecFw {
    // this already looks oldfashioned wtf
    public static final Type dVec = FW.telephonist_native("DVec", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DVecFw.dVec)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);
            Val[] vec = instance._UNPACK();

            if (cArg.getType().equals(SymbolFw.symbol)) {
                String text = cArg._UNPACK().toString();
                switch (text) {
                    case "size": // ???
                        return DIntFw.dint(vec.length);
                    case "iter-type":
                        return DVecIterFw.iterType(instance).asVal();
                    case "first":
                        return DVecIterFw.iterator(instance, 0);
                    case "last":
                        return DVecIterFw.iterator(instance, vec.length - 1);
                }
            } else
                // deprecated (probably)
                if (cArg.getType().equals(DIntFw.dint)) {
                    BigInteger v = DIntFw.unwrap0(cArg);
                    // perhaps its better to use boxes for results of this
                    // otherwise there's no way to distinguish "out of range" result from a proper one
                    // except for duplicating range checks
                    // nevermind we just moved to iterators, just move this to a separate value later
                    // also I feel like we're still 30 years before finishing this as a usable language
                    if (v.bitLength() > 32)
                        return null; // out of range
                    int i = v.intValue();
                    if (i < 0 || i >= vec.length)
                        return null; // out of range
                    return vec[i];
                }
        } else if (arg.equalsSymbol("builder")) {
            return DVecBuilderFw.emptyBuilder;
        }
        return null;
    }).asType();

    public static final CompEnv dvec2exprCenv = CompEnv.of(FW.telephonist_native("dvec2exprCenv", (arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = arg.get("passing");

            Type type = arg.getType();
            if (type.equals(dVec)) {
                Val[] vec = arg._UNPACK();
                List<Expr> elements = new ArrayList<>();
                for (Val val : vec) {
                    elements.add(val.toExpr(compEnv));
                }
                return ExprFw.wrap(ExprList.of(BracketsTypes.square, elements));
            } else if (type.equals(DVecBuilderFw.dVecBuilder)) {
                Val[] vec = arg._UNPACK();
                List<Expr> elements = new ArrayList<>();
                elements.add(type.asVal().toExpr(compEnv));
                for (Val val : vec) {
                    elements.add(val.toExpr(compEnv));
                }
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
            }
            return null;
        }
        return null;
    }));

    public static <T> T[] arAppended(T[] value, T arg) {
        int i = value.length;
        value = Arrays.copyOf(value, i + 1);
        value[i] = arg;
        return value;
    }

    public static Val vec(Val... value) {
        return Val.of(dVec, value);
    }

    public static final class DVecConstructorCEnvFw {
        public static final Val dVecConstructorCenv = FW.telephonist_native("dVecConstructorCenv", (arg) -> {
            if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
                Val exprVal = arg.call(FW.symbol("expr"));
                Val compEnv = arg.call(FW.symbol("comp-env"));
                Expr expr = exprVal._UNPACK(Expr.class);
                if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.square)) {
                    ExprList list = (ExprList) expr;
                    if (list.size() == 0)
                        return VitFw.wrap(Vit.val(DVecBuilderFw.dvecbf.call(DVecBuilderFw.emptyBuilder)));

                    Vit ctor = Vit.val(DVecBuilderFw.emptyBuilder);
                    for (int i = 0; i < list.size(); i++) {
                        Expr f = list.get(i);
                        Val elVitVal = CompEnv.of(compEnv).compileV(ExprFw.wrap(f));
                        if (!VitFw.isVit(elVitVal.getType()))
                            return elVitVal;

                        Vit vit;
                        try {
                            vit = VitFw.unwrap(elVitVal, f);
                        } catch (VitCompilationException e) {
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

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("DVec"), DVecFw.dVec.asVal()),
                    DeclaredFw.declared(symbol("DVecBuilder"), DVecBuilderFw.dVecBuilder.asVal()),
                    DeclaredFw.declared(symbol("dvecbf"), DVecBuilderFw.dvecbf)
            ),
            CompEnv.compEnv(
                    DVecConstructorCEnvFw.dVecConstructorCenv,
                    dvec2exprCenv.asVal()
            )
    );
}
