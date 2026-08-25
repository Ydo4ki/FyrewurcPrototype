package org.fw.lib.elib;

import org.fw.core.Tester;
import org.fw.lib.elib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class ModuleTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(ModuleFw.class, CompEnv.of(EssentiaLibstd.lib.exports()));
    }
}
