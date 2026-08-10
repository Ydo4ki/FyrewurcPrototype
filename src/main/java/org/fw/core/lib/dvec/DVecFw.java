package org.fw.core.lib.dvec;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.*;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.DeclaredFw;
import org.fw.core.lib.ModuleFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.util.FwUtils;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fw.core.FW.*;

public final class DVecFw {
    // this already looks oldfashioned wtf
    public static final Type dVec = FW.telephonist("DVec", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DVecFw.dVec)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);
            Val[] vec = instance._unpack();

            if (cArg.type().equals(SymbolFw.symbol)) {
                String text = cArg._unpack().toString();
                switch (text) {
                    case "size":
                        return DIntFw.dint(vec.length);
                    // errrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
                    // that's probably it
                    case "iter-type":
                        return DVecIterFw.iterType(instance).asVal();
                    case "first":
                        return DVecIterFw.iterator(instance, 0);
                    case "last":
                        return DVecIterFw.iterator(instance, vec.length - 1);
                }
            } else
                // deprecated (probably)
                if (cArg.type().equals(DIntFw.dint)) {
                BigInteger v = DIntFw.unwrap0(cArg);
                // perhaps its better to use boxes for results of this
                // otherwise there's no way to distinguish "out of range" result from a proper one
                // except for duplicating range checks
                // todo
                if (v.bitLength() > 32)
                    return null; // out of range
                int i = v.intValue();
                if (i < 0 || i >= vec.length)
                    return null; // out of range
                return vec[i];
            }
        } else if (arg.equals(symbol("builder"))) {
            return DVecBuilderFw.emptyBuilder;
        }
        return null;
    }).asType();

    public static final Val dvecToExpr = telephonist((arg) -> {
        Type type = arg.type();
        if (type.equals(dVec)) {
            Val[] vec = arg._unpack();
            List<Expr> elements = new ArrayList<>();
            for (Val val : vec) {
                elements.add(val.toExpr(RtEnv.unspecified));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.square, elements));
        } else if (type.equals(DVecBuilderFw.dVecBuilder)) {
            Val[] vec = arg._unpack();
            List<Expr> elements = new ArrayList<>();
            elements.add(type.asVal().toExpr(RtEnv.unspecified));
            for (Val val : vec) {
                elements.add(val.toExpr(RtEnv.unspecified));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
        }
        return null;
    });

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
        public static final Val dVecConstructorCenv = telephonist(() -> "dVecConstructorCenv", (arg) -> {
            if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
                Val exprVal = arg.call(symbol("expr"));
                Val compEnv = arg.call(symbol("comp-env"));
                Expr expr = exprVal._unpack();
                if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.square)) {
                    ExprList list = (ExprList) expr;
                    if (list.size() == 0)
                        return VitFw.wrap(Vit.val(DVecBuilderFw.dvecbf.call(DVecBuilderFw.emptyBuilder)));

                    Vit ctor = Vit.val(DVecBuilderFw.emptyBuilder);
                    for (int i = 0; i < list.size(); i++) {
                        Expr f = list.get(i);
                        Val elVitVal = CompEnv.of(compEnv).compileV(ExprFw.wrap(f));
                        if (!VitFw.isVit(elVitVal.type()))
                            return elVitVal;

                        Vit vit = null;
                        try {
                            vit = VitFw.unwrap(elVitVal);
                        } catch (VitCompilationException e) {
                            throw new RuntimeException(e);
                        }
                        ctor = ctor.call(Vit.simplify(vit));
                    }

                    ctor = Vit.val(DVecBuilderFw.dvecbf).call(ctor);

                    return VitFw.wrap(ctor);
                }
            }
            return null;
        });
    }

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("DVec"), DVecFw.dVec.asVal()),
                    DeclaredFw.declared(symbol("DVecBuilder"), DVecBuilderFw.dVecBuilder.asVal()),
                    DeclaredFw.declared(symbol("dvecbf"), DVecBuilderFw.dvecbf)
            )),
            DVecConstructorCEnvFw.dVecConstructorCenv
    ));
}
