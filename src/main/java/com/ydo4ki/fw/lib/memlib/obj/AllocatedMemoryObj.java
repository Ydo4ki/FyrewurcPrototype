package com.ydo4ki.fw.lib.memlib.obj;

import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;

import java.nio.ByteBuffer;

// those are kinda emulated but there isn't much you can do about it in java 8
public final class AllocatedMemoryObj implements Obj {

    private final HeapObj heap;

    // mom can I have sun.misc.Unsafe
    // we have sun.misc.Unsafe at home
    // sun.misc.Unsafe at home:
    private final ByteBuffer directMemoryBuffer;

    public AllocatedMemoryObj(HeapObj heap, long size) {
        this.heap = heap;
        if (size > Integer.MAX_VALUE)
            throw new UnsupportedOperationException("I'm sorry but this amount of memory is to big, say thanks to java 8. " +
                    "Yes I could just emulate bigger memory segments but that doesn't seem reasonable for this implementation");
        directMemoryBuffer = ByteBuffer.allocateDirect((int)size);
    }

    @Override
    public State state() {
        return heap.state();
    }

    @Override
    public Obj partOf() {
        return heap;
    }

    public ByteBuffer buffer() {
        return directMemoryBuffer;
    }

    @Override
    public void shmert() {
        // hell I can't even free them manually
        // I heard that bytebuffers suck but didn't expect them to be that bad
//        directMemoryBuffer.close
    }
}
