package org.fw.core.lib.dvec;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.telephonist;

public final class DVecBuilderFw {
    public static final Type dVecBuilder = FW.telephonist("DVecBuilder", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DVecBuilderFw.dVecBuilder)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);
            Val[] value = instance._unpack();

            return Val.of(DVecBuilderFw.dVecBuilder, DVecFw.arAppended(value, cArg));
        }
        return null;
    }).asType();
    public static final Val emptyBuilder = Val.of(dVecBuilder, new Val[0]);
    public static final Val dvecbf = FW.telephonist("dvecbf", (arg) -> {
        if (arg.type() == dVecBuilder) {
            return Val.of(DVecFw.dVec, arg._unpack());
        }
        return null;
    });
}
