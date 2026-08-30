package org.fw.core.base;

import org.fw.core.Tester;
import org.fw.lib.stdlib.expr.StdLib;
import org.fw.lib.stdlib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class BaseTests {
    @Test
    public void valsTest() throws IOException {
        Tester.testFw(TypeGetFw.class, "vals", CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void boolTest() throws IOException {
        Tester.testFw(BoolFw.class, CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void callTest() throws IOException {
        Tester.testFw(CallFw.class, CompEnv.of(StdLib.lib.exports()));
    }

    @Test
    public void symbolTest() throws IOException {
        Tester.testFw(SymbolFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
