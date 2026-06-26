package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.ExprFw;

import java.math.BigInteger;
import java.util.Arrays;

import static org.fw.core.FW.*;

public final class DVecFw {
    // this already looks oldfashioned wtf
    public static final Type dVec = FW.telephonist("DVec", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, DVecFw.dVec, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);
            Val[] vec = instance._unpack();

            if (cArg.type().equals(ExprFw.symbol)) {
                String text = cArg._unpack().toString();
                switch (text) {
                    case "size":
                        return DIntFw.dint(vec.length);
                        // errrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr
                    // that's probably it
                }
            } else if (cArg.type().equals(DIntFw.dint)) {
                BigInteger v = DIntFw.unwrap0(cArg);
                // perhaps its better to use boxes for results of this
                // otherwise there's no way to distinguish "out of range" result from a proper one
                // except for duplicating range checks
                // todo
                if (v.bitLength() > 32)
                    return Val.unspecified; // out of range
                int i = v.intValue();
                if (i < 0 || i >= vec.length)
                    return Val.unspecified; // out of range
                return vec[i];
            }
        } else if (arg.equals(symbol("builder"))) {
            return DVecFw.emptyBuilder;
        }
        return Val.unspecified;
    }).asType();

    public static final Type dVecBuilder = telephonist("DVecBuilder", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, DVecFw.dVecBuilder, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);
            Val[] value = instance._unpack();

            return Val.of(DVecFw.dVecBuilder, arAppended(value, cArg));
        }
        return Val.unspecified;
    }).asType();

    public static final Val dvecbf = telephonist("dvecbf", (arg, context) -> {
        if (arg.type() == dVecBuilder) {
            return Val.of(dVec, arg._unpack());
        }
        return Val.unspecified;
    });

    public static final Val emptyBuilder = Val.of(DVecFw.dVecBuilder, new Val[0]);

    static Val[] arAppended(Val[] value, Val arg) {
        int i = value.length;
        value = Arrays.copyOf(value, i + 1);
        value[i] = arg;
        return value;
    }

    public static Val vec(Val... value) {
        return Val.of(dVec, value);
    }

    // bruh
    @Deprecated
    public static Val newvec(Val[] vals, Context context) {
        Val b = DVecFw.emptyBuilder;
        for (Val val : vals) {
            b = b.call(val, context);
        }
        return DVecFw.dvecbf.call(b, context);
    }
}
