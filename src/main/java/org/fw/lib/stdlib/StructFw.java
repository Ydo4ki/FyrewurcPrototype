package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.lib.stdlib.dvec.DVecBuilderFw;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.core.FW.symbol;

// so should the order of fields matter or not?
public final class StructFw {
    public static final Type struct = FW.telephonist("Struct", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StructFw.struct)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            Struct struct = instance._unpack();
            if (FwUtils.isTypeApiCall(arg, instance.asType())) {
                Val strInstance = CallFw.getVal(arg);
                arg = CallFw.getArg(arg);
                Val[] values = strInstance._unpack();
                int index = struct.indexOf(arg);
                if (index == -1) return null;
                return values[index];
            } else if (arg.equals(symbol("builder"))) {
                return structBuilder(struct, instance);
            } else if (arg.equals(symbol("fields"))) {
                return DVecFw.vec(struct.fields);
            }
        } else if (arg.equals(symbol("construct"))) {
            return FW.telephonist("Struct.construct", (payload) -> {
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
//            return toExpr(arg, toExpr);
        } else if (type.asVal().type().equals(struct)) {
            return instanceToExpr(arg, toExpr);
        }
        return null;
    });

    public static Val toExpr(Val arg, CompEnv toExpr) {
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Struct struct = (Struct) o;
            return Objects.deepEquals(fields, struct.fields);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(fields);
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
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
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


    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = arg.get("passing");

            Type type = arg.type();
            if (type == struct) {
                return toExpr(arg, compEnv);
            }
            return null;
        } else if (arg.type().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");
            if (val == struct.asVal()) {
                return FW.telephonist(c -> {
                    if (c.type() != DVecFw.dVec)
                        return null;
                    Val[] args = c._unpack();
                    if (args.length > 1)
                        return null;
                    Val b = val.get("construct");
                    for (Val arg1 : args) {
                        b = b.call(arg1);
                    }
                    return Operation.pure(b).asVal();
                });
            } else if (val.type() == struct) {
                int len = val._unpack(Struct.class).fields.length;
                return FW.telephonist(c -> {
                    if (c.type() != DVecFw.dVec)
                        return null;
                    Val[] args = c._unpack();
                    if (args.length > len)
                        return null;
                    Val b = val.get("builder");
                    for (Val arg1 : args) {
                        b = b.call(arg1);
                    }
                    return Operation.pure(b).asVal();
                });
            }
        } else if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "struct": {
                        Vit builder = Vit.val(DVecBuilderFw.emptyBuilder);
                        for (int i = 1; i < isize; i++) {
                            Expr expr1 = exprVal.call(DIntFw.dint(i))._unpack();
                            Val val = compEnv.call(CompEnv.syntaxResolve(expr1, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(val.type()))
                                return val;

                            builder = builder.call(val._unpack(Vit.class));
                        }
                        builder = Vit.call(DVecBuilderFw.dvecbf, builder);

                        return VitFw.wrap(Vit.val(struct.asVal()).call(symbol("construct")).call(builder));
                    }
                }
            }
        }
        return null;
    }));

    public static final Val module = ModuleFw.module(
            DeclaredFw.declared(symbol("Struct"), struct)
    );

    public static final Lib lib = Lib.of(
            module,
            directivesCenv.asVal()
    );
}
// wow it actually worked from the first try
// but constraints are meaningless for now