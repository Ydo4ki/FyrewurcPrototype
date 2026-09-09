package org.fw.std.combine;

import org.fw.std.Std;
import org.fw.test.Tester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class RecurserTest {
    @Test
    public void recurserTest() throws IOException {
        Tester.testDirectFw(RecurserFw.class, Std.std);
    }
}
