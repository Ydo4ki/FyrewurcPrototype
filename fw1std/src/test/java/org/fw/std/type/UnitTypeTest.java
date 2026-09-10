package org.fw.std.type;

import org.fw.std.Std;
import org.fw.test.Tester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class UnitTypeTest {
    @Test
    public void unitTest() throws IOException {
        Tester.testDirectFw(UnitTypeFw.class, Std.std);
    }
}
