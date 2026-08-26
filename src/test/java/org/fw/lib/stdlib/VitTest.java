package org.fw.lib.stdlib;

import org.fw.core.Tester;
import org.fw.lib.stdlib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class VitTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(VitFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
