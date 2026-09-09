package org.fw.std;

import org.fw.esast.expr.StdLib;
import org.fw.esast.expr.CompEnv;
import org.fw.std.dvec.DVecFw;
import org.fw.test.Tester3;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DVecTest {
    @Test
    public void dvecTest() throws IOException {
        Tester3.testExprFw(DVecFw.class, "dvec.fw", CompEnv.of(StdLib.lib.exports()));
    }
}
