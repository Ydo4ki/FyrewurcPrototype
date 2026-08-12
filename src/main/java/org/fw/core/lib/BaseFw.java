package org.fw.core.lib;

import org.fw.core.base.*;
import org.fw.core.lib.expr.CompEnv;

import static org.fw.core.FW.symbol;

public final class BaseFw {

    public static final CompEnv exports = CompEnv.of(CompEnv.compEnv(
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Call"), Call.call_t.asVal()),
                    DeclaredFw.declared(symbol("Telephonist"), Val.ofTelephonist(0)),
                    DeclaredFw.declared(symbol("Symbol"), SymbolFw.symbol.asVal()),
                    DeclaredFw.declared(symbol("is-unspecified"), ValsFw.isUnspecified),
                    DeclaredFw.declared(symbol("eq"), ValsFw.eq),
                    DeclaredFw.declared(symbol("type-get"), ValsFw.typeGet)
            ))
    ));
}
