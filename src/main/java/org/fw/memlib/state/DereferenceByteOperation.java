package org.fw.memlib.state;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.memlib.lib.ByteFw;
import org.fw.core.state.operation.Operation;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public record DereferenceByteOperation(MemorySegment segment, long pointer) implements Operation {
    @Override
    public Val execute(Context context) {
        try {
            byte ret = segment.get(ValueLayout.JAVA_BYTE, pointer);
            return ByteFw.wrap(ret);
        } catch (RuntimeException e) {
            return ByteFw.wrap((byte) 0xFF);
        }
    }
}

