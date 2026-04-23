package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;

public final class DeclaredFw {

    public static final Val colon = FW.telephonist(":", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2)
                return Val.unspecified;

            Val name = arg.call(DIntFw.dint(0), context);
            if (!name.type().equals(ExprFw.symbol))
                return Val.unspecified; // symbol expected

            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(value.type())) return value; // error idk

            return VitFw.wrap(Vit.val(DeclaredFw.declared.asVal()).call(symbol("builder")).call(name).call(VitFw.unwrap(value)));
        }
        return Val.unspecified;
    });

    // I hope it will be possible to make it a struct later
    public static final Type declared = FW.telephonist("Declared", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, DeclaredFw.declared, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);

            Declared decl = instance._unpack();
            if (arg.equals(symbol("key"))) {
                return decl.key();
            } else if (arg.equals(symbol("value"))) {
                return decl.value();
            }
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2)
                return Val.unspecified;

            Val name = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(name.type()))
                return name; // error idk

            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(value.type())) return value; // error idk

            return VitFw.wrap(Vit.val(DeclaredFw.declared.asVal()).call(symbol("builder")).call(VitFw.unwrap(name)).call(VitFw.unwrap(value)));
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Declared.builder", (name, context1) -> {
                return FW.telephonist(() -> "(call Declared.builder " + name.toExpr(context1) + ")", (value, context2) -> {
                    return declared(name, value);
                });
            });
        }
        return Val.unspecified;
    }).asType();

    public static Val getKey(Val declared, Context context) {
        return declared.call(symbol("key"), context);
    }

    public static Val getValue(Val declared, Context context) {
        return declared.call(symbol("value"), context);
    }


    public static Val declared(Val key, Val value) {
        return Val.of(DeclaredFw.declared, new Declared(key, value));
    }

    public static Expr toExpr(Val arg, Context context) {
        return arg._unpack(DeclaredFw.Declared.class).toExpr(context);
    }

    private record Declared(Val key, Val value) {
        public Expr toExpr(Context context) {
            return ExprList.of(BracketsTypes.round, Symbol.of("Declared"), key.toExpr(context), value.toExpr(context));
        }
    }
}
