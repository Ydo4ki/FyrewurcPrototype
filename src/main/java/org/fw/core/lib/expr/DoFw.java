package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.DVecFw;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;

@Deprecated
final class DoFw {
    public static final Val _do = FW.telephonist("do", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize == 0) {
                return Val.unspecified;
            }

            Vit ctor = Vit.val(DVecFw.emptyBuilder);

            for (int i = 0; i < isize; i++) {
                Val argNVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(cEnv)), context);
                if (!VitFw.isVit(argNVit.type()))
                    return argNVit; // compile error idk

//                ctor = ctor.call(OperationFw.wrap(Operation.vit(VitFw.unwrap(argNVit))));
                ctor = ctor.call(VitFw.unwrap0(argNVit));
            }

            ctor = Vit.val(DVecFw.dvecbf).call(ctor);
            // ctor = Vit.val(DoFw._do).call(symbol("perform")).call(ctor).call(Vit.var);
            // ctor = Vit.invoke(ctor);
            ctor = ctor.call(DIntFw.dint(isize-1));
            return VitFw.wrap(ctor);
        } /*else if (arg.equals(symbol("perform"))) {
            return FW.telephonist("do.perform", (instructionsVal, context1) -> {
                return FW.telephonist("(do.perform " + instructionsVal.toExpr(context1) + ")", (rtEnv, context2) -> {

                    if (!instructionsVal.type().equals(DVecFw.dVec))
                        return Val.unspecified;
                    Val[] instructions = instructionsVal._unpack();
                    Val ret = Val.unspecified;
                    for (Val instructionVal : instructions) {
                        if (!OperationFw.isOperation(instructionVal.type()))
                            return Val.unspecified;
                        Operation instruction = OperationFw.unwrap(instructionVal);
                        ret = instruction.execute(context1);
                    }
                    return ret;
                });
            });
        }*/
        return Val.unspecified;
    });
}
