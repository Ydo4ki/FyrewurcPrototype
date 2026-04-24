package org.fw.memlib.state;

import org.fw.core.FW;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.memlib.lib.DWordFw;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public record DereferenceDWordOperation(MemorySegment segment, long pointer) implements Operation {

    private static final Val max = DWordFw.wrap(-0xFFFFFFFF);

    @Override
    public Val execute(Context context) {
        try {
            int ret = segment.get(ValueLayout.JAVA_INT_UNALIGNED, pointer);
            return DWordFw.wrap(ret);
        } catch (RuntimeException e) {
            return max;
        }
    }

    @Override
    public Val asVal() {
        return Val.of(operationType, this);
    }

    public static final Type operationType = FW.telephonist("DereferenceDWordOperation", (_, _) -> Val.unspecified).asType();
}
