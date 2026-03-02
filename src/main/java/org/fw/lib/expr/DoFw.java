package org.fw.lib.expr;

import org.fw.FW;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.DVecFw;
import org.fw.lib.VitFw;
import org.fw.lib.state.OperationFw;
import org.fw.state.operation.Operation;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class DoFw {
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
                ctor = ctor.call(VitFw.unwrap(argNVit));
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
