package org.fw.std.dvec;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.CallFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;

import org.fw.core.util.FwUtils;

import java.math.BigInteger;
import java.util.Arrays;

public final class DVecFw {
    // this already looks oldfashioned wtf
    public static final Type dVec = FW.lambda_native("DVec", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DVecFw.dVec)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);
            Val[] vec = instance._UNPACK_();

            if (cArg.getType().equals(SymbolFw.symbol)) {
                String text = cArg._UNPACK_().toString();
                switch (text) {
                    case "size": // ???
                        return DIntFw.dint(vec.length);
                    case "iter-type":
                        return DVecIterFw.iterType(instance).asVal();
                    case "first":
                        return DVecIterFw.iterator(instance, 0);
                    case "last":
                        return DVecIterFw.iterator(instance, vec.length - 1);
                }
            }
            else
                // deprecated (probably)
                if (cArg.getType().equals(DIntFw.dint)) {
                    BigInteger v = DIntFw.unwrap0(cArg);
                    // perhaps its better to use boxes for results of this
                    // otherwise there's no way to distinguish "out of range" result from a proper one
                    // except for duplicating range checks
                    // nevermind we just moved to iterators, just move this to a separate value later
                    // also I feel like we're still 30 years before finishing this as a usable language
                    if (v.bitLength() > 32)
                        return null; // out of range
                    int i = v.intValue();
                    if (i < 0 || i >= vec.length)
                        return null; // out of range
                    return vec[i];
                }
        } else if (arg.equalsSymbol("builder")) {
            return DVecBuilderFw.emptyBuilder;
        }
        return null;
    }).asType();

    public static <T> T[] arAppended(T[] value, T arg) {
        int i = value.length;
        value = Arrays.copyOf(value, i + 1);
        value[i] = arg;
        return value;
    }

    public static Val vec(Val... value) {
        return Val._NEW_INSTANCE_(dVec, value);
    }
}
