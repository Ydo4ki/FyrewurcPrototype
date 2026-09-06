package org.fw.core.abstrait;

import org.fw.core.base.SymbolFw;
import org.fw.core.base.TypeGetFw;
import org.fw.core.base.Val;

import static org.fw.core.FW.symbol;

// so null just means unknown value
// and in Val's context it turns into unspecified
// make sense
// but we can't do stuff like null.call
// ok we'll get to this later as we see wether we actually need it or not
public interface Value {
    Value call(Value value);

    default Value get(String val) {
        return call(symbol(val));
    }

    default Value getType0() {
        return TypeGetFw.typeGet.call(this);
    }

    default boolean equalsSymbol(String symbol) {
        if (!this.getType0().impliesEquality(SymbolFw.symbol.asVal()))
            return false;
        return this.impliesEquality(symbol(symbol));
    }

    boolean impliesEquality(Val val);
}
