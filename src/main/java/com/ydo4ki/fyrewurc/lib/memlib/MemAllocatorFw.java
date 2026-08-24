package com.ydo4ki.fyrewurc.lib.memlib;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.DIntFw;
import com.ydo4ki.fyrewurc.lib.memlib.obj.AllocatedMemoryObj;
import com.ydo4ki.fyrewurc.lib.memlib.obj.HeapObj;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

// ahhh local runtimes local runtimes
// remember local runtimes
public final class MemAllocatorFw {

    public static final Type heapAllocator = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, MemAllocatorFw.heapAllocator)) {
            Val instance = Call.getVal(arg);
            HeapObj heap = instance._unpack();
            arg = Call.getArg(arg);


            if (arg.type() == SymbolFw.symbol) {
                String sym = arg._unpack().toString();
                switch (sym) {
                    case "heap":
                        return Val.of(HeapFw.heap, heap);
                }
            }
            if (!arg.type().equals(DIntFw.dint))
                return null;
            long size = DIntFw.unwrap0(arg).longValueExact();
            return new Operation() {
                @Override
                public Val apply(State state) {
                    if (state != heap.state())
                        return Operation.unit;
                    return Val.of(AllocatedMemoryFw.allocatedMemory, new AllocatedMemoryObj(heap, size));
                }

                @Override
                protected boolean isPure0() {
                    return false;
                }
            }.asVal();
        }
        return null;
    }).asType();
}