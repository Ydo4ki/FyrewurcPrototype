package org.fw.core.base;

import org.fw.core.FW;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TelephonistTests {
    @Test
    public void test() {
        Val v = FW.telephonist_native(a -> null);
        assertEquals(Val.ofTelephonist(2), v.getType().asVal().getType().asVal().getType().asVal());
        assertEquals("Telephonist14", Val.ofTelephonist(14).toString());
        assertEquals("Telephonist2", v.getType().asVal().getType().asVal().getType().toString());
    }
}
