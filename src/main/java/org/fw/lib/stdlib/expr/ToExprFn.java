package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.base.*;
import org.fw.lib.stdlib.*;
import org.fw.lib.stdlib.ConstraintFw;
import com.ydo4ki.fw.internal.lib.memlib.ints.IntTypeFw;

import static org.fw.core.FW.symbol;

@Deprecated
public class ToExprFn {

    @Deprecated
    public static final Type exprififier = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("construct"))
            .call(ConstraintFw.isSpecified)
            .asType();

    @Deprecated
    public static final Type toExprResolve = ChainResolveFw.chainResolveType(ConstraintFw.isSpecified);

    @Deprecated
    public static Val toExprResolve(Val val, Val toExpr) {
        return Val.of(ToExprFn.toExprResolve, new ChainResolveFw.ChainResolve(val, toExpr));
    }

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
    @Deprecated
    public static final Val toExprRest = FW.telephonist((arg) -> {
        if (arg.getType() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.getType();

        if (type.equals(EnumFw.enumeration)) {
            return EnumFw.toExpr(arg, toExpr);
        } else if (type.asVal().getType().equals(EnumFw.enumeration)) {
            return arg._unpack(); // it was supposed to be a symbol
        }

        if (type instanceof Type.TelephonistType) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.braces));
        }
        return null;
    });

    @Deprecated
    public static final Val toExpr = ChainLinkFw.chain(exprififier,
            DeclaredFw.declaredToExpr,
            StrFw.strToExpr,
            SyntaxResolveFw.syntaxResolveToExpr,
            ChainLinkFw.chainLinkToExpr,
//            TraitFw.traitToExpr,
            IntTypeFw.intToExpr,
            toExprRest
    );
}
