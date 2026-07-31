package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.*;

import java.util.function.Supplier;

import static org.fw.core.FW.symbol;

public class ToExprFn {


    public static final Type exprififier = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("constructor"))
            .call(Unspecified.isNot)
            .asType();


    // ok this ones breaking everything anyway
//    @Deprecated
//    public static final Val unknownToExpr = FW.telephonist((arg, context) -> {
//        Type type = arg.type();
//        return ExprFw.wrap(ExprList.of(BracketsTypes.braces,
//                arg.type().asVal().toExpr(context) // !!!
//        ));
//    });


    // wait did I really write all of this instead of using hashmap or some custom overengineered condition table?
    // wow
    public static final Val toExprRest = FW.telephonist((arg) -> {
        Type type = arg.type();
        if (type.equals(Val.ofTelephonist(0).asType())) {
            TelephonistType.Telephonist tele = arg._unpack();
            Supplier<Expr> r = tele.representation();
            if (r != null)
                return ExprFw.wrap(r.get());
        }

        if (type.equals(OperatorExprFw.exprOperator) || type.equals(SenderExprFw.exprSender)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round,
                    type.asVal().toExpr(RtEnv.unspecified),
                    arg.call(symbol("operator")).toExpr(RtEnv.unspecified)
            ));
        }

        if (type.equals(AccumulatorsExprFw.exprAccumulator)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round,
                    AccumulatorsExprFw.exprAccumulator.asVal().toExpr(RtEnv.unspecified),
                    arg.call(symbol("operator")).toExpr(RtEnv.unspecified)
            ));
        }

        if (type.equals(EnumFw.enumeration)) {
            return EnumFw.toExpr(arg, RtEnv.unspecified);
        } else if (type.asVal().type().equals(EnumFw.enumeration)) {
            return arg._unpack(); // it was supposed to be a symbol
        }

        if (type instanceof TelephonistType) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.braces));
        }
        return null;
    });

    public static final Val toExpr = ChainLinkFw.chain(exprififier,
            VitFw.vitToExpr,
            ExprFw.esastToExpr,
            ModuleFw.moduleToExpr,
            DIntFw.dintToExpr,
            DeclaredFw.declaredToExpr,
            DeclarationFw.declarationToExpr,
            StrFw.strToExpr,
            SyntaxResolveFw.syntaxResolveToExpr,
            ChainLinkFw.chainLinkToExpr,
            BoolLib.boolToExpr,
            DVecFw.dvecToExpr,
            StructFw.structToExpr,
            TraitFw.traitToExpr,
            BoxFw.boxToExpr,
//            unknownToExpr,
            toExprRest
    );
}
