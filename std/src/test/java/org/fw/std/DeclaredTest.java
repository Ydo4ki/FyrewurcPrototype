package org.fw.std;

import org.fw.core.Tester;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.StdLib;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DeclaredTest {
    @Test
    public void test() throws IOException {
        Tester.testExprFw(DeclaredFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
