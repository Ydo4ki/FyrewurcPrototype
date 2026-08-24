package org.fw.lib.elib.dvec;

import org.fw.core.Tester;
import org.fw.lib.elib.EssentiaLibstd;
import org.fw.lib.elib.expr.CompEnv;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DVecTest {
    @Test
    public void dvecTest() throws IOException {
        Tester.testFw(DVecFw.class, "dvec", CompEnv.of(EssentiaLibstd.lib.exports()));
    }
}
