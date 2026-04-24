package org.fw.memlib.state;

import org.fw.core.FW;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.memlib.lib.QWordFw;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public record DereferenceQWordOperation(MemorySegment segment, long pointer) implements Operation {
    
    private static final Val max = QWordFw.wrap(-1L);
    
    @Override
    public Val execute(Context context) {
        try {
            long ret = segment.get(ValueLayout.JAVA_LONG_UNALIGNED, pointer);
            return QWordFw.wrap(ret);
        } catch (RuntimeException e) {
            return max;
        }
    }

    @Override
    public Val asVal() {
        return Val.of(operationType, this);
    }

    public static final Type operationType = FW.telephonist("DereferenceQWordOperation", (_, _) -> Val.unspecified).asType();
}
