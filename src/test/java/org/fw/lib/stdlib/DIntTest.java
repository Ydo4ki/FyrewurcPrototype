package org.fw.lib.stdlib;

import org.fw.core.Tester;
import org.fw.lib.stdlib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DIntTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(DIntFw.class, "dint", CompEnv.of(StdLib.lib.exports()));
    }
}
