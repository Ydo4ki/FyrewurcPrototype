package org.fw.core.abstrait;

import org.fw.base.*;
import org.fw.core.NativeExecutionException;
import org.fw.core.state.obj.State;

import static org.fw.core.FW.symbol;

// so null just means unknown value
// and in Val's context it turns into unspecified
// make sense
// but we can't do stuff like null.call
// ok we'll get to this later as we see whether we actually need it or not
public interface Value {

    Value call(Value arg);

    boolean impliesEquality(Val val);

    default Val asVal() throws NativeExecutionException {
        Val ret = asVal(null);
        if (ret == null)
            throw new NativeExecutionException("Not a val: " + this);
        return ret;
    }

    default Val asVal(Val orElse) {
        return orElse;
    }

    default Value invoke(State state) {
        return null;
    }

    default Value callLazy(Value arg) {
        return new LazyCallValue(this, arg);
    }

    default Value get(String property) {
        return call(symbol(property));
    }

    default Value getTypeValue() {
        return TypeGetFw.typeGet.call(this);
    }

    default boolean equals(Val val) {
        return impliesEquality(val);
//        return EqFw.eq.call(this).call(val).impliesEquality(BoolFw._true);
    }

    default boolean equalsSymbol(String symbol) {
        if (!this.getTypeValue().impliesEquality(SymbolFw.symbol.asVal()))
            return false;
        return this.equals(symbol(symbol));
    }
}
