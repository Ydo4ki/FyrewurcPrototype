package org.fw.lib.expr;

import org.fw.FW;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.VitFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class ExprInvokeFw {
    public static final Val invoke = FW.telephonist("invoke!", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) {
                return Val.unspecified;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            //noinspection DataFlowIssue
            Vit vit = Vit.simplify(VitFw.unwrap(retVit), context);
//            System.out.println("# " + VitFw.wrap(vit).toExpr(context));
            /*if (vit instanceof VitVal) {*/
//                Val opVal = VitFw.unwrap(retVit).eval(context);
//                if (!OperationFw.isOperation(opVal.type()))
//                    return Val.unspecified;
//
//                Operation operation = opVal._unpack();
//
//                return VitFw.wrap(Vit.invoke(Vit.val(OperationFw.wrap(operation))));
            /*}*/

            return VitFw.wrap(Vit.invoke(vit));

            /*Vit resultingCode = Vit.invoke(Operation.vit(Vit.val(VitFw.eval).call(vit).call(Vit.var)));
            Vit resultingCode = Vit.val(VitFw.eval).call(vit).call(Vit.var);
            System.out.println("## " + resultingCode.eval(context).toExpr(context));
            return VitFw.wrap(resultingCode);*/
        }
        return Val.unspecified;
    });
}
