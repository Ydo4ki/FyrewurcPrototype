package org.fw.std.type;

import org.fw.std.Std;
import org.fw.test.Tester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class SumTypeTest {
    @Test
    public void sumTest() throws IOException {
        Tester.testDirectFw(SumTypeFw.class, Std.std);
    }
}
