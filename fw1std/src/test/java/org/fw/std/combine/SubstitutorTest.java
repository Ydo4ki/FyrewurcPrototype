package org.fw.std.combine;

import org.fw.std.Std;
import org.fw.test.Tester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class SubstitutorTest {
    @Test
    public void substitutorTest() throws IOException {
        Tester.testDirectFw(SubstitutorFw.class, Std.std);
    }
}
