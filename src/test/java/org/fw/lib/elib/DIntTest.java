package org.fw.lib.elib;

import org.fw.core.Tester;
import org.fw.lib.elib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DIntTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(DIntFw.class, "dint", CompEnv.of(EssentiaLibstd.lib.exports()));
    }
}
