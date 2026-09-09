package org.fw.std.combine;

import org.fw.std.Std;
import org.fw.test.Tester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class ConstTest {
    @Test
    public void constTest() throws IOException {
        Tester.testDirectFw(ConstFw.class, Std.std);
    }
}
