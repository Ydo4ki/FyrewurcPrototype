package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.TelephonistType;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.vit.VitCall;
import org.fw.core.vit.VitInvoke;
import org.fw.core.vit.VitVal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.fw.core.FW.symbol;

public class ToExprFn {
    // wait did I really write all of this instead of using hashmap or some custom overengineered condition table?
    // wow
    public static final Val toExpr = FW.telephonist((arg, context) -> {
        Type type = arg.type();
        if (type.equals(Val.ofTelephonist(0).asType())) {
            TelephonistType.Telephonist tele = arg._unpack();
            Supplier<Expr> r = tele.representation();
            if (r != null)
                return ExprFw.wrap(r.get());
        } else if (type.equals(VitFw.vitVal)) {
            VitVal vitVal = arg._unpack();
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(context), vitVal.val().toExpr(context)));
        } else if (type.equals(VitFw.vitVar)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(context)));
        } else if (type.equals(VitFw.vitCall)) {
            VitCall vitVal = arg._unpack();
            List<Expr> elements = new ArrayList<>();
            elements.add(type.asVal().toExpr(context));
            elements.addAll(vitVal.exprs(context));

            return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
        } else if (type.equals(VitFw.vitInvoke)) {
            VitInvoke vitInvoke = arg._unpack();
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(context), VitFw.wrap(vitInvoke.operation()).toExpr(context)));
        }

        if (type.equals(ExprFw.exprList)) {
            List<Expr> content = new ArrayList<>();
            content.add(type.asVal().toExpr(context));
            ExprList el = arg._unpack();
            for (Expr expr : el) {
                content.add(ExprFw.wrap(expr).toExpr(context));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, content));
        } else if (type.equals(ExprFw.symbol)) {
            String str = arg._unpack().toString();
            str = str.replace("\"", "\\\"");
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, Symbol.of("Symbol"), Symbol.of('"' + str + '"')));
        }

        if (type.equals(DIntFw.dint)) {
            return symbol(arg._unpack().toString());
        }

        if (type.equals(DeclaredFw.declared)) {
            Expr expr = DeclaredFw.toExpr(arg, context);
            return ExprFw.wrap(expr);
        }

        if (type.equals(DeclarationFw.declaration)) {
            Expr expr = DeclarationFw.toExpr(arg, context);
            return ExprFw.wrap(expr);
        }

        if (type.equals(ModuleFw.module)) {
            return ModuleFw.toExpr(arg, context);
        }

        if (type.equals(StrFw.str)) {
            return symbol('"' + arg._unpack(String.class) + '"');
        }

        if (type.equals(OperatorExprFw.exprOperator) || type.equals(SenderExprFw.exprSender)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round,
                    type.asVal().toExpr(context),
                    arg.call(symbol("operator"), context).toExpr(context)
            ));
        }

        if (type.equals(SyntaxResolveFw.syntaxResolve)) {
            return ExprFw.wrap(arg._unpack(SyntaxResolveFw.SyntaxResolve.class).toExpr(context));
        }
        if (type.equals(CompEnv.compEnv)) {
            SyntaxResolveFw.CompEnvRecord env = arg._unpack();
            return ExprFw.wrap(env.toExpr(context));
        }

        if (type.equals(BoolFw.bool)) {
            return symbol(arg._unpack().toString());
        }

        if (type.equals(ExprCallOpFw.exprCallOp)) {
            return ExprCallOpFw.toExpr(arg, context);
        }

        if (type.equals(EnumFw.enumeration)) {
            return EnumFw.toExpr(arg, context);
        } else if (type.asVal().type().equals(EnumFw.enumeration)) {
            Val value = arg._unpack();
            return value; // it supposes to be a symbol
        }

        if (type.equals(AccumulatorsExprFw.exprAccumulator)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round,
                    AccumulatorsExprFw.exprAccumulator.asVal().toExpr(context),
                    arg.call(symbol("operator"), context).toExpr(context)
            ));
        }

        if (type.equals(DVecFw.dVec)) {
            Val[] vec = arg._unpack();
            List<Expr> elements = new ArrayList<>();
//            elements.add(DVecFw.dVec.asVal().toExpr(context));
            for (Val val : vec) {
                elements.add(val.toExpr(context));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.square, elements));
        } else if (type.equals(DVecFw.dVecBuilder)) {
            Val[] vec = arg._unpack();
            List<Expr> elements = new ArrayList<>();
            elements.add(type.asVal().toExpr(context));
            for (Val val : vec) {
                elements.add(val.toExpr(context));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
        }

        if (type.equals(StructFw.struct)) {
            return StructFw.toExpr(arg, context);
        } else if (type.asVal().type().equals(StructFw.struct)) {
            return StructFw.instanceToExpr(arg, context);
        }

        if (type.equals(TraitFw.trait)) {
            return TraitFw.toExpr(arg, context);
        }

        if (type.equals(BoxFw.boxType)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, BoxFw.boxType.asVal().toExpr(context), BoxFw.unbox(arg).toExpr(context)));
        } else if (type.asVal().type().equals(BoxFw.boxType)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(context), BoxFw.unbox(arg).toExpr(context)));
        }

        if (type instanceof TelephonistType) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.braces));
        }
        return ExprFw.wrap(ExprList.of(BracketsTypes.braces,
                arg.type().asVal().toExpr(context)
//                Symbol.of(arg.toString())
        ));
    });
}
