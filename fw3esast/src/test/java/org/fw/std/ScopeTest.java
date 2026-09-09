package org.fw.std;

import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.StdLib;
import org.fw.core.state.obj.ScopeFw;
import org.fw.test.Tester3;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ScopeTest {
    @Test
    public void test() throws IOException {
        Tester3.testExprFw(ScopeFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
