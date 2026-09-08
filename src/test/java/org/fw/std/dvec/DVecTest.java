package org.fw.std.dvec;

import org.fw.core.Tester;
import org.fw.esast.expr.StdLib;
import org.fw.esast.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DVecTest {
    @Test
    public void dvecTest() throws IOException {
        Tester.testExprFw(DVecFw.class, "dvec.fw", CompEnv.of(StdLib.lib.exports()));
    }
}
