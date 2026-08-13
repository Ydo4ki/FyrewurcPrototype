package org.fw.core.cases;

import org.fw.core.base.BoolFw;
import org.fw.core.lib.constraint._Constraint;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

public final class ConstraintTest {
    public static void main(String[] args) throws VitCompilationException {
        _Constraint cnstr = _Constraint.of(Vit.val(BoolFw._true), Vit.val(BoolFw._true));
//                Vit.call(ValsFw.eq, Vit.var).call(DVecFw.vec(DIntFw.dint(1), DIntFw.dint(2)))
//                        .call(symbol("or"))
//                        .call(Vit.call(ValsFw.eq, Vit.var).call(DVecFw.vec(DIntFw.dint(2), DIntFw.dint(1)))));

//        Constraint cnstr = Constraint.of(VitFw.unwrap(ConstraintFw.toConstraint(BitFw.bit).call(symbol("vit"))));
//        System.out.println(cnstr.toExpr(Main.rtEnv.get(symbol("to-expr"))));
//        System.out.println(cnstr);
    }
}

