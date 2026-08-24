package org.fw.core.debug;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.lib.elib.DeclaredFw;
import org.fw.lib.elib.Lib;
import org.fw.lib.elib.ModuleFw;
import org.fw.lib.jlib.data.JOopFw;

import static org.fw.core.FW.symbol;

public final class Debug {
    public static final Val debug = ModuleFw.module(
            DeclaredFw.declared(symbol("val2oop"), FW.telephonist((arg) -> {
                return Val.of(JOopFw.jOop, arg);
            }))
    );

    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("_Debug"), debug)
    ));
}
