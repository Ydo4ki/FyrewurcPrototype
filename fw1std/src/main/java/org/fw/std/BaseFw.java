package org.fw.std;

import org.fw.base.*;

import static org.fw.core.FW.symbol;

public final class BaseFw {

    public static final Val module = ModuleFw.module(
            DeclaredFw.declared(symbol("Call"), CallFw.call_t.asVal()),
            DeclaredFw.declared(symbol("Telephonist"), Val.ofTelephonist(0)),
            DeclaredFw.declared(symbol("Symbol"), SymbolFw.symbol.asVal()),
            DeclaredFw.declared(symbol("is-unspecified"), Unspecified.isUnspecified),
            DeclaredFw.declared(symbol("eq"), EqFw.eq),
            DeclaredFw.declared(symbol("type-get"), TypeGetFw.typeGet),
            DeclaredFw.declared(symbol("Bool"), BoolFw.bool),
            DeclaredFw.declared(symbol("true"), BoolFw._true),
            DeclaredFw.declared(symbol("false"), BoolFw._false)
    );

}
