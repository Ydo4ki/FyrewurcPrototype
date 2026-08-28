package org.fw.lib.stdlib;

import org.fw.core.Tester;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.ExprFw;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class ExprTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(ExprFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
