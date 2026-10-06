package org.fw.base;

import org.fw.std.Std;
import org.fw.test.Tester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class TelephonizeTest {
    @Test
    public void telephonizeTest() throws IOException {
        Tester.testDirectFw(TelephonizeFw.class, Std.std);
    }
}
