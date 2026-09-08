package org.fw.lib.stdlib.dvec;

import org.fw.core.Tester;
import org.fw.lib.stdlib.expr.StdLib;
import org.fw.lib.stdlib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DVecTest {
    @Test
    public void dvecTest() throws IOException {
        Tester.testExprFw(DVecFw.class, "dvec.fw", CompEnv.of(StdLib.lib.exports()));
    }
}
