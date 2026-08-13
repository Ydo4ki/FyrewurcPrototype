package org.fw.core.cases;

import org.fw.core.lib.VitFw;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.memlib.words.BitFw;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;

public final class ConstraintTest {
    public static void main(String[] args) throws VitCompilationException {
//        Constraint cnstr = Constraint.of(
//                Vit.call(ValsFw.eq, Vit.var).call(DVecFw.vec(DIntFw.dint(1), DIntFw.dint(2)))
//                        .call(symbol("or"))
//                        .call(Vit.call(ValsFw.eq, Vit.var).call(DVecFw.vec(DIntFw.dint(2), DIntFw.dint(1)))));

//        Constraint cnstr = Constraint.of(VitFw.unwrap(ConstraintFw.toConstraint(BitFw.bit).call(symbol("vit"))));
//        System.out.println(cnstr.toExpr(Main.rtEnv.get(symbol("to-expr"))));
//        System.out.println(cnstr);
    }
}
