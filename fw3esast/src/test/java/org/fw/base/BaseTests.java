package org.fw.base;

import org.fw.core.FW;
import org.fw.test.Tester;
import org.fw.test.Tester3;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.Std;
import org.fw.esast.expr.StdLib;
import org.fw.esast.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.fw.core.FW.symbol;

public final class BaseTests {

    static Val dstdt = ModuleFw.module(
            DeclaredFw.declared(symbol("b-and"), FW.lambda("b-and",
                    a -> FW.lambda(b -> {
                        return BoolFw.wrap(a.equals(BoolFw._true) && b.equals(BoolFw._true));
                    }))),
            DeclaredFw.declared(symbol("b-or"), FW.lambda("b-or",
                    a -> FW.lambda(b -> {
                        return BoolFw.wrap(a.equals(BoolFw._true) || b.equals(BoolFw._true));
                    }))),
            DeclaredFw.declared(symbol("b-xor"), FW.lambda("b-xor",
                    a -> FW.lambda(b -> {
                        return BoolFw.wrap(a.equals(BoolFw._true) != b.equals(BoolFw._true));
                    }))),
            DeclaredFw.declared(symbol("b-not"), FW.lambda("b-not",
                    a -> BoolFw.wrap(!a.equals(BoolFw._true))))
    );

    @Test
    public void valsTest() throws IOException {
        Tester3.testExprFw(TypeGetFw.class, "vals.fw", CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void boolTest() throws IOException {
        Tester.testDirectFw(BoolFw.class, ModuleFw.merge(Std.std, dstdt));
        Tester3.testExprFw(BoolFw.class, CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void callTest() throws IOException {
        Tester3.testExprFw(CallFw.class, CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void symbolTest() throws IOException {
        Tester3.testExprFw(SymbolFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
