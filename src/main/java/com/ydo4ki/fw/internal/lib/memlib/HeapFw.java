package com.ydo4ki.fw.internal.lib.memlib;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import com.ydo4ki.fw.internal.lib.memlib.obj.HeapObj;
import org.fw.core.util.FwUtils;

// ok maybe local runtimes isn't the best name ever
// it's more like emulated states
// but emulated sounds heavy
// while the whole point of this is to provide a native way to localize some parts of the state without straight-up running a code in another emulation
// that would be a triple nested vm
public final class HeapFw {

    public static final Type heap = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, HeapFw.heap)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            if (arg.getType() == SymbolFw.symbol) {
                String sym = arg._unpack().toString();
                switch (sym) {
                    case "allocator":
                        return Val.of(MemAllocatorFw.heapAllocator, instance._unpack(HeapObj.class));
                }
            }
        }
        return null;
    }).asType();

    public static final Val systemHeap = Val.of(heap, HeapObj.systemHeap);
}
