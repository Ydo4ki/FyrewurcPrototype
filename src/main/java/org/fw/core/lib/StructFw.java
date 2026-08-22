package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.lib.dvec.DVecFw;
import org.fw.core.lib.expr.ToExprFn;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

// so should the order of fields matter or not?
public final class StructFw {
    public static final Type struct = FW.telephonist("Struct", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StructFw.struct)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
            Struct struct = instance._unpack();
            if (FwUtils.isTypeApiCall(arg, instance.asType())) {
                Val strInstance = Call.getVal(arg);
                arg = Call.getArg(arg);
                Val[] values = strInstance._unpack();
                int index = struct.indexOf(arg);
                if (index == -1) return null;
                return values[index];
            } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
                Val size = arg.call(symbol("size"));
                Val cEnv = arg.call(symbol("comp-env"));
                int isize = size._unpack(BigInteger.class).intValue();
                if (isize != struct.fields.length) {
                    return null;
                }

                Vit ctor = Vit.val(instance).call(symbol("builder"));
                for (int i = 0; i < isize; i++) {
                    Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i))._unpack(), CompEnv.of(cEnv)));
                    if (!VitFw.isVit(retVit.type()))
                        return retVit; // compile error idk

                    try {
                        ctor = ctor.call(VitFw.unwrap(retVit));
                    } catch (VitCompilationException e) {
                        throw new RuntimeException(e);
                    }
                }

                return VitFw.wrap(ctor);
            } else if (arg.equals(symbol("builder"))) {
                return structBuilder(struct, instance);
            }
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) {
                return null;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            try {
                return VitFw.wrap(Vit.val(StructFw.struct.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist("Struct.constructor", (payload) -> {
                if (!payload.type().equals(DVecFw.dVec))
                    return null;
                Val[] fields = payload._unpack();
                for (Val field : fields) {
                    if (!field.type().equals(DeclarationFw.declaration))
                        return null; // some day I'll add proper errors
                }
                return Val.of(StructFw.struct, new Struct(fields));
            });
        }
        return null;
    }).asType();
    public static final Val structToExpr = FW.telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
        if (type.equals(struct)) {
            return toExpr(arg, toExpr);
        } else if (type.asVal().type().equals(struct)) {
            return instanceToExpr(arg, toExpr);
        }
        return null;
    });

    public static Val toExpr(Val arg, Val toExpr) {
        StructFw.Struct value = arg._unpack();
        List<Expr> finElements = new ArrayList<>();
        finElements.add(StructFw.struct.asVal().toExpr(toExpr));
        List<Expr> elements = new ArrayList<>();
        for (Val val : value.fields) {
            elements.add(val.toExpr(toExpr));
        }
        finElements.add(ExprList.of(BracketsTypes.square, elements));
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, finElements));
    }

    public static Val instanceToExpr(Val arg, Val toExpr) {
        Val[] value = arg._unpack();
        List<Expr> elements = new ArrayList<>();
        elements.add(arg.type().asVal().toExpr(toExpr));
        for (Val val : value) {
            elements.add(val.toExpr(toExpr));
        }
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
    }

    public static Type struct(Val... fields) {
        for (Val value : fields) {
            if (!value.type().equals(DeclarationFw.declaration))
                throw new IllegalArgumentException(value.toString());
        }
        return Val.of(StructFw.struct, new Struct(fields)).asType();
    }

    public static Val instance(Type struct, Val... values) {
        return Val.of(struct, values);
    }

    private static final class Struct {
        private final Val[] fields;

        private Struct(Val[] fields) {
            this.fields = fields;
        }

        public int indexOf(Val key) {
            for (int i = 0; i < fields.length; i++) {
                Val field = fields[i];
                if (DeclarationFw.getKey(field).equals(key))
                    return i;
            }
            return -1;
        }
    }

    private static final class StructBuilder {
        private final Struct struct;
        private final Val sameStructButItsAVal;
        private final Val[] progress;

        private StructBuilder(Struct struct, Val sameStructButItsAVal, Val[] progress) {
            this.struct = struct;
            this.sameStructButItsAVal = sameStructButItsAVal;
            this.progress = progress;
        }
    }

    private static Val structBuilder(Struct struct, Val sameStructButItsAVal) {
        if (struct.fields.length == 0) return Val.of(sameStructButItsAVal.asType(), new Val[0]);
        return Val.of(structBuilder, new StructBuilder(struct, sameStructButItsAVal, new Val[0]));
    }

    private static final Type structBuilder = FW.telephonist("StructBuilder", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StructFw.structBuilder)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
            StructBuilder payload = instance._unpack();

            Val constraint = DeclarationFw.getConstraint(payload.struct.fields[payload.progress.length]);
            if (constraint.call(symbol("check")).call(arg) != BoolFw._true) {
                return null;
            }

            Val[] values = DVecFw.arAppended(payload.progress, arg);
            if (values.length == payload.struct.fields.length) {
                return Val.of(payload.sameStructButItsAVal.asType(), values);
            }
            return Val.of(StructFw.structBuilder, new StructBuilder(payload.struct, payload.sameStructButItsAVal, values));
        }
        return null;
    }).asType();
}
// wow it actually worked from the first try
// but constraints are meaningless for now