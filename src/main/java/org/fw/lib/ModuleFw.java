package org.fw.lib;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.lib.expr.ExprFw;
import org.fw.vit.Vit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.FW.symbol;

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
                ctor = ctor.call(VitFw.unwrap(value));
            }
            ctor = Vit.val(DVecFw.dvecbf).call(ctor);
            ctor = Vit.val(ModuleFw.module.asVal()).call(symbol("constructor")).call(ctor); // ok nice
            return VitFw.wrap(ctor);
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist("Module.constructor", (arg1, _) -> {
                if (!arg1.type().equals(DVecFw.dVec))
                    return Val.unspecified;

                Val[] values = arg1._unpack(); // Ok I don't even care at this point
                for (Val value : values) {
                    if (!value.type().equals(DeclaredFw.declared))
                        return Val.unspecified;
                }

                return Val.of(ModuleFw.module, new Module(values));
            });
        } else if (arg.equals(symbol("contains-key"))) {
            return FW.telephonist("Module.contains-key", (arg1, context1) -> {
                if (!arg1.type().equals(ModuleFw.module)) return Val.unspecified;
                Module mod = arg1._unpack();
                return FW.telephonist(() -> "(Module.contains-key " + arg1.toExpr(context1) + ")", (key, context2) -> {
                    return mod.containsKey(key, context2) ? BoolFw._true : BoolFw._false;
                });
            });
        }

        return Val.unspecified;
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

    // todo: replace this with map idk
    private record Module(Val[] declareds) {
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
            return Objects.deepEquals(declareds, module.declareds);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(declareds);
        }
    }
}
