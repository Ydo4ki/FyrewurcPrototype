package org.fw.lib.stdlib;

import org.fw.core.Tester;
import org.fw.core.util.FwUtils;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.StdLib;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class DeclarationTest {
    @Test
    public void test() throws IOException {
        Tester.testFw(DeclarationFw.class, CompEnv.of(StdLib.lib.exports()));
    }
}
