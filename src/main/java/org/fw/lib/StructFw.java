package org.fw.lib;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.annotation.Insightful;
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
import java.util.List;

import static org.fw.FW.symbol;

public final class StructFw {
    @Insightful
    public static final Type struct = FW.telephonist("Struct", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, StructFw.struct, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            Struct struct = instance._unpack();
            if (FwUtils.isTypeApiCall(arg, instance.asType(), context)) {
                Val strInstance = Call.getVal(arg, context);
                arg = Call.getArg(arg, context);
                Val[] values = strInstance._unpack();
                int index = struct.indexOf(arg, context);
                if (index == -1) return Val.unspecified;
                return values[index];
            } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
                Val size = arg.call(symbol("size"), context);
                Val cEnv = arg.call(symbol("comp-env"), context);
                int isize = size._unpack(BigInteger.class).intValue();
                if (isize != struct.fields.length) {
                    return Val.unspecified;
                }

                Vit ctor = Vit.val(instance).call(symbol("builder"));
                for (int i = 0; i < isize; i++) {
                    Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(cEnv)), context);
                    if (!VitFw.isVit(retVit.type()))
                        return retVit; // compile error idk

                    ctor = ctor.call(VitFw.unwrap(retVit));
                }

                return VitFw.wrap(ctor);
            } else if (arg.equals(symbol("builder"))) {
                return structBuilder(struct, instance);
            } else if (arg.type().equals(ExprFw.toExpr)) {
                Val strInstance = BoxFw.unbox(arg);
                if (!strInstance.type().equals(instance.asType()))
                    return Val.unspecified;

                Val[] value = strInstance._unpack();
                List<Expr> elements = new ArrayList<>();
                elements.add(instance.toExpr(context));
                for (Val val : value) {
                    elements.add(val.toExpr(context));
                }
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
            }
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) {
                return Val.unspecified;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            return VitFw.wrap(Vit.val(StructFw.struct.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist("Struct.constructor", (payload, context1) -> {
                if (!payload.type().equals(DVecFw.dVec))
                    return Val.unspecified;
                Val[] fields = payload._unpack();
                for (Val field : fields) {
                    if (!field.type().equals(DeclarationFw.declaration))
                        return Val.unspecified; // some day I'll add proper errors
                }
                return Val.of(StructFw.struct, new Struct(fields));
            });
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(StructFw.struct))
                return Val.unspecified;

            Struct value = instance._unpack();
            List<Expr> finElements = new ArrayList<>();
            finElements.add(StructFw.struct.asVal().toExpr(context));
            List<Expr> elements = new ArrayList<>();
            for (Val val : value.fields) {
                elements.add(val.toExpr(context));
            }
            finElements.add(ExprList.of(BracketsTypes.square, elements));
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, finElements));
        }
        return Val.unspecified;
    }).asType();

    private record Struct(Val[] fields) {
        public int indexOf(Val key, Context context) {
            for (int i = 0; i < fields.length; i++) {
                Val field = fields[i];
                if (DeclarationFw.getKey(field, context).equals(key))
                    return i;
            }
            return -1;
        }
    }
    record StructBuilder(Struct struct, Val sameStructButItsAVal, Val[] progress) {}

    private static Val structBuilder(Struct struct, Val sameStructButItsAVal) {
        if (struct.fields.length == 0) return Val.of(sameStructButItsAVal.asType(), new Val[0]);
        return Val.of(structBuilder, new StructBuilder(struct, sameStructButItsAVal, new Val[0]));
    }

    private static final Type structBuilder = FW.telephonist("StructBuilder", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, StructFw.structBuilder, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            StructBuilder payload = instance._unpack();
            Val[] values = DVecFw.appended(payload.progress, arg);
            if (values.length == payload.struct.fields.length) {
                return Val.of(payload.sameStructButItsAVal.asType(), values);
            }
            return Val.of(StructFw.structBuilder, new StructBuilder(payload.struct, payload.sameStructButItsAVal, values));
        }
        return Val.unspecified;
    }).asType();
}
// wow it actually worked from the first try
// but constraints are meaningless for now