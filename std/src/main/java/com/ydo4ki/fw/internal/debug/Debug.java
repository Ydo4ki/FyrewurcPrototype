package com.ydo4ki.fw.internal.debug;

import org.fw.core.FW;
import org.fw.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.std.DeclaredFw;
import org.fw.esast.expr.Lib;
import org.fw.std.ModuleFw;

import static org.fw.core.FW.symbol;

public final class Debug {
    public static final Val debug = ModuleFw.module(
//            DeclaredFw.declared(symbol("val2oop"), FW.telephonist((arg) -> {
//                return Val.of(JOopFw.jOop, arg);
//            })),
            DeclaredFw.declared(symbol("Print"), FW.telephonist_native((arg) -> {
                System.out.println("# " + arg);
                return Operation.unit;
            }))
    );

    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("_Debug"), debug)
    ));
}
