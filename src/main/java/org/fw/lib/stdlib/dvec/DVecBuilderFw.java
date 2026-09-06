package org.fw.lib.stdlib.dvec;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.telephonist_native;

public final class DVecBuilderFw {
    public static final Type dVecBuilder = FW.telephonist_native("DVecBuilder", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DVecBuilderFw.dVecBuilder)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);
            Val[] value = instance._UNPACK();

            return Val.of(DVecBuilderFw.dVecBuilder, DVecFw.arAppended(value, cArg));
        }
        return null;
    }).asType();

    public static final Val emptyBuilder = Val.of(dVecBuilder, new Val[0]);

    public static final Val dvecbf = FW.telephonist_native("dvecbf", (arg) -> {
        if (arg.getType() == dVecBuilder) {
            return Val.of(DVecFw.dVec, arg._UNPACK());
        }
        return null;
    });
}
