package org.fw.memlib.state;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.memlib.lib.DWordFw;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public record DereferenceDWordOperation(MemorySegment segment, long pointer) implements Operation {

    private static final Val max = DWordFw.wrap(-1);

    @Override
    public Val execute(Context context) {
        try {
            int ret = segment.get(ValueLayout.JAVA_INT_UNALIGNED, pointer);
            return DWordFw.wrap(ret);
        } catch (RuntimeException e) {
            return max;
        }
    }
}
