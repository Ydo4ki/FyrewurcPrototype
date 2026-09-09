package org.fw.std.type;

import org.fw.std.Std;
import org.fw.test.Tester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class ProductTypeTest {
    @Test
    public void productTest() throws IOException {
        Tester.testDirectFw(ProductTypeFw.class, Std.std);
    }
}
