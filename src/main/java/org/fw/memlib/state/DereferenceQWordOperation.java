package org.fw.memlib.state;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.memlib.lib.QWordFw;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public record DereferenceQWordOperation(MemorySegment segment, long pointer) implements Operation {
    
    private static final Val max = QWordFw.wrap(-1);
    
    @Override
    public Val execute(Context context) {
        try {
            long ret = segment.get(ValueLayout.JAVA_LONG_UNALIGNED, pointer);
            return QWordFw.wrap(ret);
        } catch (RuntimeException e) {
            return max;
        }
    }
}
