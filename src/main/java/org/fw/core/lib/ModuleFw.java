package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.lib.expr.SyntaxResolveFw;
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

import static org.fw.core.FW.symbol;

// no I literally just made a telemap XD
public final class ModuleFw {
    public static final Type module = FW.telephonist("Module", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, ModuleFw.module, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);

            Module module = instance._unpack();
            for (Val declared : module.declareds()) {
                if (DeclaredFw.getKey(declared, context).equals(arg)) {
                    return DeclaredFw.getValue(declared, context);
                }
            }
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            final int isize = size._unpack(BigInteger.class).intValue();
//            Val[] entries = new Val[isize];
//            Vit ctor = Vit.val(ModuleFw.module.asVal()).call(symbol("builder"));
            Vit ctor = Vit.val(DVecFw.emptyBuilder);

            for (int i = 0; i < isize; i++) {
                Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(cEnv)), context);
                if (!VitFw.isVit(value.type()))
                    return value; // error idk
                ctor = ctor.call(VitFw.unwrap0(value));
            }
            ctor = Vit.val(DVecFw.dvecbf).call(ctor);
            ctor = Vit.val(ModuleFw.module.asVal()).call(symbol("constructor")).call(ctor); // ok nice
            return VitFw.wrap(ctor);
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist("Module.constructor", (arg1, c) -> {
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
            return FW.telephonist("Module.contains-key", (arg1, context1) -> {
                if (!arg1.type().equals(ModuleFw.module)) return null;
                Module mod = arg1._unpack();
                return FW.telephonist(() -> "(Module.contains-key " + arg1.toExpr(context1) + ")", (key, context2) -> {
                    return mod.containsKey(key, context2) ? BoolFw._true : BoolFw._false;
                });
            });
        }

        return null;
    }).asType();

    public static Val module(Val... values) {
        for (Val value : values) {
            if (!value.type().equals(DeclaredFw.declared))
                throw new IllegalArgumentException(value.toString());
        }
        return Val.of(ModuleFw.module, new Module(values));
    }

    public static Val toExpr(Val arg, Context context) {
        return ExprFw.wrap(arg._unpack(ModuleFw.Module.class).toExpr(context));
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

        public Expr toExpr(Context context) {
            List<Expr> elements = new ArrayList<>();
            elements.add(ModuleFw.module.asVal().toExpr(context));
            for (Val declared : declareds) {
                elements.add(declared.toExpr(context));
            }
            return ExprList.of(BracketsTypes.round, elements);
        }

        public boolean containsKey(Val key, Context context) {
            for (Val declared : declareds) {
                if (DeclaredFw.getKey(declared, context).equals(key)) return true;
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
        public static final Type moduleCompEnv = FW.telephonist("ModuleCEnvFn", (arg, context) -> {
            if (arg.equals(symbol("constructor"))) {
                return FW.telephonist((module, context1) -> compEnv(module));
            }
            if (FwUtils.isTypeApiCall(arg, ModuleCEnvFw.moduleCompEnv, context)) {
                Val instance = Call.getVal(arg, context);
                arg = Call.getArg(arg, context);
                Val payload = instance._unpack(Val.class);
                if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
                    Val exprVal = arg.call(symbol("expr"), context);
                    Val compEnv = arg.call(symbol("comp-env"), context);
                    Expr expr = exprVal._unpack();
                    if (expr instanceof Symbol) {
                        if (module.asVal().call(symbol("contains-key"), context).call(payload, context).call(exprVal, context) == BoolFw._true) {
                            Val value = payload.call(exprVal, context);
                            return VitFw.wrap(Vit.val(value));
                        }
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

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Module"), ModuleFw.module.asVal()),
                    DeclaredFw.declared(symbol("ModuleCompEnv"), ModuleFw.ModuleCEnvFw.moduleCompEnv.asVal())
            ))
    ));
}
