package com.ydo4ki.fyrewurc.debug;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.DeclaredFw;
import org.fw.lib.stdlib.Lib;
import org.fw.lib.stdlib.ModuleFw;
import com.ydo4ki.fyrewurc.lib.jlib.data.JOopFw;

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
