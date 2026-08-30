package org.fw.lib.stdlib;

import org.fw.core.Tester;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.StdLib;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class BoxTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(BoxFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
