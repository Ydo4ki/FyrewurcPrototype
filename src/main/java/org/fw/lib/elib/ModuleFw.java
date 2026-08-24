package org.fw.lib.elib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.lib.elib.dvec.DVecBuilderFw;
import org.fw.lib.elib.dvec.DVecFw;
import org.fw.lib.elib.expr.SyntaxResolveFw;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.ExprFw;
import org.fw.core.vit.Vit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fw.core.FW.symbol;

// no I literally just made a telemap XD
public final class ModuleFw {
    public static final Type module = FW.telephonist("Module", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ModuleFw.module)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            Module module = instance._unpack();
            for (Val declared : module.declareds()) {
                if (DeclaredFw.getKey(declared).equals(arg)) {
                    return DeclaredFw.getValue(declared);
                }
            }
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist("Module.constructor", (arg1) -> {
                if (!arg1.type().equals(DVecFw.dVec))
                    return null;

                Val[] values = arg1._unpack(); // Ok I don't even care at this point
                for (Val value : values) {
                    if (!value.type().equals(DeclaredFw.declared))
                        return null;
                }

                return Val.of(ModuleFw.module, new Module(values));
            });
        } else if (arg.equals(symbol("contains-key"))) {
            return FW.telephonist("Module.contains-key", (arg1) -> {
                if (!arg1.type().equals(ModuleFw.module)) return null;
                Module mod = arg1._unpack();
                return FW.telephonist((key) -> mod.containsKey(key) ? BoolFw._true : BoolFw._false);
            });
        }

        return null;
    }).asType();
    public static final Val moduleToExpr = FW.telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
        if (type.equals(module)) {
            return toExpr(arg, toExpr);
        }
        return null;
    });

    public static Val module(Val... values) {
        for (Val value : values) {
            if (!value.type().equals(DeclaredFw.declared))
                throw new IllegalArgumentException(value.toString());
        }
        return Val.of(ModuleFw.module, new Module(values));
    }

    public static Val toExpr(Val arg, Val toExpr) {
        return ExprFw.wrap(arg._unpack(ModuleFw.Module.class).toExpr(toExpr));
    }

    // todo: replace with map, order shouldn't matter
    private static final class Module {
        private final Val[] declareds;

        private Module(Val[] declareds) {
            this.declareds = declareds;
        }

        public Val[] declareds() {
            return declareds;
        }

        public Expr toExpr(Val toExpr) {
            List<Expr> elements = new ArrayList<>();
            elements.add(ModuleFw.module.asVal().toExpr(toExpr));
            for (Val declared : declareds) {
                elements.add(declared.toExpr(toExpr));
            }
            return ExprList.of(BracketsTypes.round, elements);
        }

        public boolean containsKey(Val key) {
            for (Val declared : declareds) {
                if (DeclaredFw.getKey(declared).equals(key)) return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Module module = (Module) o;
            if (declareds.length != module.declareds.length) return false;
            for (int i = 0; i < declareds.length; i++) {
                if (!declareds[i].equals(module.declareds[i])) return false;
            }
            return true;
//            return Objects.deepEquals(declareds, module.declareds);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(declareds);
        }
    }

    public static final class ModuleCEnvFw {
        public static final Type moduleCompEnv = FW.telephonist("ModuleCEnvFn", (arg) -> {
            if (arg.equals(symbol("constructor"))) {
                return FW.telephonist(ModuleCEnvFw::compEnv);
            }
            if (FwUtils.isTypeApiCall(arg, ModuleCEnvFw.moduleCompEnv)) {
                Val instance = Call.getVal(arg);
                arg = Call.getArg(arg);
                Val payload = instance._unpack(Val.class);
                if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
                    Val exprVal = arg.call(symbol("expr"));
                    Val compEnv = arg.call(symbol("comp-env"));
                    Expr expr = exprVal._unpack();
                    if (expr instanceof Symbol) {
                        if (payload.type() == ModuleFw.module) {
                            if (module.asVal().call(symbol("contains-key")).call(payload).call(exprVal) == BoolFw._true) {
                                Val value = payload.call(exprVal);
                                return VitFw.wrap(Vit.val(value));
                            }
                        }
                        Val value = payload.call(exprVal);
                        if (Unspecified.isUnspecified(value))
                            return null;
                        return VitFw.wrap(Vit.val(value));
                    }
                    return null;
                }
            }
            return null;
        }).asType();

        public static Val compEnv(Val module) {
            return Val.of(moduleCompEnv, module);
        }
    }


    public static final Val directivesCenv = FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "module": {
                        Vit builder = Vit.val(DVecBuilderFw.emptyBuilder);
                        for (int i = 1; i < isize; i++) {
                            Expr expr1 = exprVal.call(DIntFw.dint(i))._unpack();
                            Val val = compEnv.call(CompEnv.syntaxResolve(expr1, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(val.type()))
                                return null;

                            builder = builder.call(val._unpack(Vit.class));
                        }
                        builder = Vit.call(DVecBuilderFw.dvecbf, builder);

                        return VitFw.wrap(Vit.val(ModuleFw.module.asVal()).call(symbol("constructor")).call(builder));
                    }
                }
            }
        }
        return null;
    });

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Module"), ModuleFw.module.asVal()),
                    DeclaredFw.declared(symbol("ModuleCompEnv"), ModuleFw.ModuleCEnvFw.moduleCompEnv.asVal())
            ),
            directivesCenv
    );
}
