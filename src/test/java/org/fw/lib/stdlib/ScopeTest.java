package org.fw.lib.stdlib;

import org.fw.core.Tester;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.StdLib;
import org.fw.lib.stdlib.state.ScopeFw;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ScopeTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(ScopeFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
