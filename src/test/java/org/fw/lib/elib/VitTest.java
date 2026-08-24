package org.fw.lib.elib;

import org.fw.core.Tester;
import org.fw.lib.elib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class VitTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(VitFw.class, CompEnv.of(EssentiaLibstd.lib.exports()));
    }
}
