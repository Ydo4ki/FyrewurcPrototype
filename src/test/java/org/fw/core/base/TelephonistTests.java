package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.base.contract.CallContract;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TelephonistTests {
    @Test
    public void test() {
        Val v = FW.telephonist(a -> null, CallContract.unknown());
        assertEquals(Val.ofTelephonist(2), v.type().asVal().type().asVal().type().asVal());
        assertEquals("Telephonist14", Val.ofTelephonist(14).toString());
        assertEquals("Telephonist2", v.type().asVal().type().asVal().type().toString());
    }
}
