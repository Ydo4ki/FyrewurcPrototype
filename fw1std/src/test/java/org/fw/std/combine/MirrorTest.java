package org.fw.std.combine;

import org.fw.std.Std;
import org.fw.test.Tester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class MirrorTest {
    @Test
    public void mirrorTest() throws IOException {
        Tester.testDirectFw(MirrorFw.class, Std.std);
    }
}

