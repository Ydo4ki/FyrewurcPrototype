package org.fw.std;

import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.StdLib;
import org.fw.test.Tester3;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class ModuleTest {
    @Test
    public void test() throws IOException {
        Tester3.testExprFw(ModuleFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
