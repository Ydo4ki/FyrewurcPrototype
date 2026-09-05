package com.ydo4ki.fw.internal.lib.memlib;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.base.contract.InvokeContract;
import org.fw.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.memlib.obj.AllocatedMemoryObj;
import com.ydo4ki.fw.internal.lib.memlib.obj.HeapObj;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

// ahhh local runtimes local runtimes
// remember local runtimes
public final class MemAllocatorFw {

    public static final Type heapAllocator = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, MemAllocatorFw.heapAllocator)) {
            Val instance = CallFw.getVal(arg);
            HeapObj heap = instance._unpack();
            arg = CallFw.getArg(arg);


            if (arg.getType() == SymbolFw.symbol) {
                String sym = arg._unpack().toString();
                switch (sym) {
                    case "heap":
                        return heap.asVal();
                }
            }
            if (!arg.getType().equals(DIntFw.dint))
                return null;
            long size = DIntFw.unwrap0(arg).longValueExact();
            return new Operation() {
                @Override
                public Val apply(State state) {
                    if (state != heap.state())
                        return Operation.unit;
                    return new AllocatedMemoryObj(heap, size).asVal();
                }

                @Override
                public InvokeContract contract() {
                    return InvokeContract.unknown();
                }
            }.asVal();
        }
        return null;
    }).asType();
}