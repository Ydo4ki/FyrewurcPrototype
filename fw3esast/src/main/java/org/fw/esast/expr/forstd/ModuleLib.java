package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.Symbol;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.std.dvec.DVecBuilderFw;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class ModuleLib {
    public static final CompEnv module2exprCenv = CompEnv.of(FW.lambda_native("module2exprCenv",(arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = arg.get("passing").asVal();

            Type type = arg.getType();
            if (type.equals(ModuleFw.module)) {
                return toExpr(arg, compEnv);
            }
            return null;
        }
        return null;
    }));
    public static final Val directivesCenv = FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr")).asVal();
            Val compEnv = (Val) arg.call(FW.symbol("comp-env")).asVal();
            Expr expr = ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "module": {
                        Vit builder = Vit.val(DVecBuilderFw.emptyBuilder);
                        for (int i = 1; i < isize; i++) {
                            Expr expr1 = ((Val) exprVal.call(DIntFw.dint(i)).asVal())._UNPACK_();
                            Val val = (Val) compEnv.call(CompEnv.syntaxResolve(expr1, CompEnv.of(compEnv))).asVal();
                            if (!VitFw.isVit(val.getType()))
                                return val;

                            builder = builder.call((Vit) val._UNPACK_());
                        }
                        builder = Vit.call(DVecBuilderFw.dvecbf, builder);

                        return VitFw.wrap(Vit.val(ModuleFw.module.asVal()).call(symbol("construct")).call(builder));
                    }
                }
            }
        }
        return null;
    });
    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Module"), ModuleFw.module.asVal()),
                    DeclaredFw.declared(symbol("ModuleCompEnv"), ModuleCEnvFw.moduleCompEnv.asVal())
            ),
            CompEnv.compEnv(
                    directivesCenv,
                    module2exprCenv.asValue()
            )
    );

    public static Val toExpr(Val arg, CompEnv compEnv) {
        List<Expr> elements0 = new ArrayList<>();
        Value value = ModuleFw.module.asVal();
        elements0.add(compEnv.toExpr(value));
        List<Expr> elements = new ArrayList<>();

        for (Val declared : ((ModuleFw.Module) arg._UNPACK_()).declareds())
            elements.add(compEnv.toExpr(declared));

        elements0.add(ExprList.of(BracketsTypes.square, elements));
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements0));
    }

    public static final class ModuleCEnvFw {
        public static final Type moduleCompEnv = FW.lambda_native("ModuleCEnvFn", (arg) -> {
            if (arg.equalsSymbol("construct")) {
                return FW.lambda_native(ModuleCEnvFw::compEnv);
            }
            if (FwUtils.isTypeApiCall(arg, ModuleCEnvFw.moduleCompEnv)) {
                Val instance = CallFw.getVal(arg).asVal();
                arg = CallFw.getArg(arg).asVal();
                Val payload = instance._UNPACK_();
                if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
                    Val exprVal = arg.call(FW.symbol("expr")).asVal();
                    Value compEnv = arg.call(FW.symbol("comp-env"));
                    Expr expr = ExprFw.unwrap(exprVal);
                    if (expr instanceof Symbol) {
                        if (payload.getType() == ModuleFw.module) {
                            Val val = ModuleFw.module.asVal();
                            Value val1 = val.call(symbol("contains-key"));
                            Value val2 = val1.call(payload);
                            if (val2.call(exprVal).impliesEquality(BoolFw._true)) {
                                Value value = payload.call(exprVal);
                                return VitFw.wrap(Vit.val(value));
                            }
                        }
                        Value value = payload.call(exprVal);
                        if (Unspecified.isUnspecified(value))
                            return null;
                        return VitFw.wrap(Vit.val(value));
                    }
                    return null;
                }
            }
            return null;
        }).asType();

        public static final Type moduleCompEnvToExpr = FW.lambda_native("ModuleCEnvToExprFn", (arg) -> {
            if (arg.equalsSymbol("construct")) {
                return FW.lambda_native(ModuleCEnvFw::toExprCompEnv);
            }
            if (FwUtils.isTypeApiCall(arg, ModuleCEnvFw.moduleCompEnvToExpr)) {
                Val instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);
                Val payload = instance._UNPACK_();
                if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
                    Val val = (Val) arg.call(FW.symbol("passing"));
                    Val compEnv = (Val) arg.call(FW.symbol("chain"));

                    if (payload.getType() == ModuleFw.module) {
                        Val val3 = ModuleFw.module.asVal();
                        Value val2 = val3.call(FW.symbol("contains-key"));
                        Value val1 = val2.call(payload);
                        if (val1.call(val).impliesEquality(BoolFw._true)) {
                            return payload.call(val);
                        }
                    }
                    Val value = payload.call(val).asVal();
                    if (!ExprFw.isExpr(value))
                        return null;
//                    if (Unspecified.isUnspecified(value))
//                        return null;
                    return value;
                }
            }
            return null;
        }).asType();

        public static Value compEnv(Value module) {
            Val val = moduleCompEnv.asVal();
            return val.get("construct").call(module);
        }
        public static Value toExprCompEnv(Value module) {
            Val val = moduleCompEnvToExpr.asVal();
            return val.get("construct").call(module);
        }
        public static Val compEnv(Val module) {
            return Val._NEW_INSTANCE_(moduleCompEnv, module);
        }
        public static Val toExprCompEnv(Val module) {
            return Val._NEW_INSTANCE_(moduleCompEnvToExpr, module);
        }
    }
}
