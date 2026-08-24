package org.fw.core.base;

import org.fw.core.Tester;
import org.fw.lib.elib.EssentiaLibstd;
import org.fw.lib.elib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class BaseTests {
    @Test
    public void valsTest() throws IOException {
        Tester.testFw(ValsFw.class, CompEnv.of(EssentiaLibstd.lib.exports()));
    }

    @Test
    public void boolTest() throws IOException {
        Tester.testFw(BoolFw.class, CompEnv.of(EssentiaLibstd.lib.exports()));
    }

    @Test
    public void callTest() throws IOException {
        Tester.testFw(Call.class, CompEnv.of(EssentiaLibstd.lib.exports()));
    }

    @Test
    public void symbolTest() throws IOException {
        Tester.testFw(SymbolFw.class, CompEnv.of(EssentiaLibstd.lib.exports()));
    }
}
