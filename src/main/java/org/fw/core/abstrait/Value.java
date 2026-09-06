package org.fw.core.abstrait;

import org.fw.core.ast.Expr;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.TypeGetFw;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.expr.CompEnv;

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

    default Value getTypeValue() {
        return TypeGetFw.typeGet.call(this);
    }

    default boolean equalsSymbol(String symbol) {
        if (!this.getTypeValue().impliesEquality(SymbolFw.symbol.asVal()))
            return false;
        return this.impliesEquality(symbol(symbol));
    }

    boolean impliesEquality(Val val);

    default Expr toExpr(CompEnv compEnv) {
        return compEnv.toExpr(this);
    }
}
