package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.Tester;
import org.fw.lib.stdlib.DeclaredFw;
import org.fw.lib.stdlib.ModuleFw;
import org.fw.lib.stdlib.Std;
import org.fw.lib.stdlib.expr.StdLib;
import org.fw.lib.stdlib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.fw.core.FW.symbol;

public final class BaseTests {

    static Val dstdt = ModuleFw.module(
            DeclaredFw.declared(symbol("b-and"), FW.telephonist("b-and",
                    a -> FW.telephonist(b -> {
                        return BoolFw.wrap(a.equals(BoolFw._true) && b.equals(BoolFw._true));
                    }))),
            DeclaredFw.declared(symbol("b-or"), FW.telephonist("b-or",
                    a -> FW.telephonist(b -> {
                        return BoolFw.wrap(a.equals(BoolFw._true) || b.equals(BoolFw._true));
                    }))),
            DeclaredFw.declared(symbol("b-xor"), FW.telephonist("b-xor",
                    a -> FW.telephonist(b -> {
                        return BoolFw.wrap(a.equals(BoolFw._true) != b.equals(BoolFw._true));
                    }))),
            DeclaredFw.declared(symbol("b-not"), FW.telephonist("b-not",
                    a -> BoolFw.wrap(!a.equals(BoolFw._true))))
    );

    @Test
    public void valsTest() throws IOException {
        Tester.testExprFw(TypeGetFw.class, "vals.fw", CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void boolTest() throws IOException {
        Tester.testDirectFw(BoolFw.class, ModuleFw.merge(Std.std, dstdt));
        Tester.testExprFw(BoolFw.class, CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void callTest() throws IOException {
        Tester.testExprFw(CallFw.class, CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void symbolTest() throws IOException {
        Tester.testExprFw(SymbolFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
