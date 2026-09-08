package org.fw.std;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.stdlib.ExtendedFw;
import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.esast.extern.Symbol;

import org.fw.std.dvec.DVecBuilderFw;
import org.fw.std.dvec.DVecFw;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.core.util.FwUtils;
import org.fw.esast.extern.BracketsTypes;
import org.fw.esast.extern.Expr;
import org.fw.esast.extern.ExprList;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.core.vit.Vit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fw.core.FW.symbol;

// no I literally just made a telemap XD
public final class ModuleFw {
    public static final Type module = FW.telephonist_native("Module", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ModuleFw.module)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Module module = instance._UNPACK_();
            for (Val declared : module.declareds()) {
                if (DeclaredFw.getKey(declared).equals(arg)) {
                    return DeclaredFw.getValue(declared);
                }
            }
        } else if (arg.equalsSymbol("construct")) {
            return FW.telephonist_native("Module.constructor", (arg1) -> {
                if (!arg1.getType().equals(DVecFw.dVec))
                    return null;

                Val[] values = arg1._UNPACK_(); // Ok I don't even care at this point
                for (Val value : values) {
                    if (!value.getType().equals(DeclaredFw.declared))
                        return null;
                }

                return Val._NEW_INSTANCE_(ModuleFw.module, new Module(values));
            });
        } else if (arg.equalsSymbol("contains-key")) {
            return FW.telephonist_native("Module.contains-key", (arg1) -> {
                if (!arg1.getType().equals(ModuleFw.module)) return null;
                Module mod = arg1._UNPACK_();
                return FW.telephonist_native((key) -> mod.containsKey(key) ? BoolFw._true : BoolFw._false);
            });
        }

        return null;
    }).asType();

    public static final CompEnv module2exprCenv = CompEnv.of(FW.telephonist_native("module2exprCenv",(arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of((Val) arg.get("chain"));
            arg = (Val) (Val) arg.get("passing");

            Type type = arg.getType();
            if (type.equals(module)) {
                return toExpr(arg, compEnv);
            }
            return null;
        }
        return null;
    }));

    public static Val module(Val... values) {
        for (Val value : values) {
            if (!value.getType().equals(DeclaredFw.declared))
                throw new IllegalArgumentException(value.toString());
        }
        return Val._NEW_INSTANCE_(ModuleFw.module, new Module(values));
    }

    public static Val toExpr(Val arg, CompEnv compEnv) {
        return ExprFw.wrap(arg._UNPACK_(ModuleFw.Module.class).toExpr(compEnv));
    }

    public static Val invert(Val module) {
        if (module == null) return null;
        if (module.getType() != ModuleFw.module)
            return null;

        Module m = module._UNPACK_();
        Val[] newd = new Val[m.declareds.length];
        for (int i = 0; i < m.declareds.length; i++) {
            newd[i] = DeclaredFw.declared(
                    DeclaredFw.getValue(m.declareds[i]),
                    DeclaredFw.getKey(m.declareds[i])
            );
        }
        return Val._NEW_INSTANCE_(module.getType(), new Module(newd));
    }

    public static Value merge(Value module, Value... modules) {
        for (Value val : modules) {
            if (val == null) continue;
            module = ChainLinkFw.chain(ExtendedFw.extended, module, val);
        }
        return module;
    }

    public static Val merge(Val module, Val... modules) {
        for (Value val : modules) {
            if (val == null) continue;
            module = (Val) ChainLinkFw.chain(ExtendedFw.extended, module, val);
        }
        return module;
    }

    // todo: replace with map, order shouldn't matter
    public static final class Module {
        private final Val[] declareds;

        private Module(Val[] declareds) {
            this.declareds = declareds;
        }

        public Val[] declareds() {
            return declareds;
        }

        public Expr toExpr(CompEnv compEnv) {
            List<Expr> elements0 = new ArrayList<>();
            elements0.add(ModuleFw.module.asVal().toExpr(compEnv));
            List<Expr> elements = new ArrayList<>();

            for (Val declared : declareds)
                elements.add(declared.toExpr(compEnv));

            elements0.add(ExprList.of(BracketsTypes.square, elements));
            return ExprList.of(BracketsTypes.round, elements0);
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
        public static final Type moduleCompEnv = FW.telephonist_native("ModuleCEnvFn", (arg) -> {
            if (arg.equalsSymbol("construct")) {
                return FW.telephonist_native(ModuleCEnvFw::compEnv);
            }
            if (FwUtils.isTypeApiCall(arg, ModuleCEnvFw.moduleCompEnv)) {
                Val instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);
                Val payload = instance._UNPACK_(Val.class);
                if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
                    Val exprVal = (Val) arg.call(FW.symbol("expr"));
                    Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
                    Expr expr = exprVal._UNPACK_(Expr.class);
                    if (expr instanceof Symbol) {
                        if (payload.getType() == ModuleFw.module) {
                            Val val = module.asVal();
                            Val val1 = ((Val) val.call(symbol("contains-key")));
                            Val val2 = ((Val) val1.call(payload));
                            if ((Val) val2.call(exprVal) == BoolFw._true) {
                                Val value = (Val) payload.call(exprVal);
                                return VitFw.wrap(Vit.val(value));
                            }
                        }
                        Val value = (Val) payload.call(exprVal);
                        if (Unspecified.isUnspecified(value))
                            return null;
                        return VitFw.wrap(Vit.val(value));
                    }
                    return null;
                }
            }
            return null;
        }).asType();

        public static final Type moduleCompEnvToExpr = FW.telephonist_native("ModuleCEnvToExprFn", (arg) -> {
            if (arg.equalsSymbol("construct")) {
                return FW.telephonist_native(ModuleCEnvFw::toExprCompEnv);
            }
            if (FwUtils.isTypeApiCall(arg, ModuleCEnvFw.moduleCompEnvToExpr)) {
                Val instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);
                Val payload = instance._UNPACK_(Val.class);
                if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
                    Val val = (Val) arg.call(FW.symbol("passing"));
                    Val compEnv = (Val) arg.call(FW.symbol("chain"));

                    if (payload.getType() == ModuleFw.module) {
                        Val val3 = module.asVal();
                        Val val2 = (Val) val3.call(FW.symbol("contains-key"));
                        Val val1 = (Val) val2.call((Value) payload);
                        if ((Val) val1.call((Value) val) == BoolFw._true) {
                            return (Val) payload.call((Value) val);
                        }
                    }
                    Val value = (Val) payload.call((Value) val);
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
            return ((Val) val.get("construct")).call(module);
        }
        public static Value toExprCompEnv(Value module) {
            Val val = moduleCompEnvToExpr.asVal();
            return ((Val) val.get("construct")).call(module);
        }
        public static Val compEnv(Val module) {
            return Val._NEW_INSTANCE_(moduleCompEnv, module);
        }
        public static Val toExprCompEnv(Val module) {
            return Val._NEW_INSTANCE_(moduleCompEnvToExpr, module);
        }
    }


    public static final Val directivesCenv = FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call((Value) FW.symbol("expr"));
            Val compEnv = (Val) arg.call((Value) FW.symbol("comp-env"));
            Expr expr = exprVal._UNPACK_(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "module": {
                        Vit builder = Vit.val(DVecBuilderFw.emptyBuilder);
                        for (int i = 1; i < isize; i++) {
                            Expr expr1 = ((Val) exprVal.call((Value) DIntFw.dint(i)))._UNPACK_();
                            Val val = (Val) compEnv.call((Value) CompEnv.syntaxResolve(expr1, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(val.getType()))
                                return val;

                            builder = builder.call(val._UNPACK_(Vit.class));
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
                    DeclaredFw.declared(symbol("ModuleCompEnv"), ModuleFw.ModuleCEnvFw.moduleCompEnv.asVal())
            ),
            CompEnv.compEnv(
                    directivesCenv,
                    module2exprCenv.asValue()
            )
    );
}
