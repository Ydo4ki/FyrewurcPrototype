package org.fw.memlib.state;

import org.fw.core.FW;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.memlib.lib.ByteFw;
import org.fw.memlib.lib.DWordFw;
import org.fw.memlib.lib.QWordFw;
import org.fw.memlib.lib.WordFw;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public record DereferenceWordOperation(MemorySegment segment, long pointer) implements Operation {

    private static final Val max = WordFw.wrap((short) 0xFFFF);

    @Override
    public Val execute(Context context) {
        try {
            short ret = segment.get(ValueLayout.JAVA_SHORT_UNALIGNED, pointer);
            return WordFw.wrap(ret);
        } catch (RuntimeException e) {
            return max;
        }
    }

    @Override
    public Val asVal() {
        return Val.of(operationType, this);
    }

    public static final Type operationType = FW.telephonist("DereferenceWordOperation", (_, c) -> Val.unspecified).asType();
}

