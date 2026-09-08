package org.fw.std;

import org.fw.core.Tester;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.StdLib;
import org.fw.std.state.ScopeFw;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ScopeTest {
    @Test
    public void test() throws IOException {
        Tester.testExprFw(ScopeFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
