package com.ydo4ki.fw.internal.lib.memlib;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.base.CallFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.memlib.obj.AllocatedMemoryObj;
import com.ydo4ki.fw.internal.lib.memlib.obj.HeapObj;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

// ahhh local runtimes local runtimes
// remember local runtimes
public final class MemAllocatorFw {

    public static final Type heapAllocator = FW.lambda_native("heapAllocator", arg -> {
        if (FwUtils.isTypeApiCall(arg, MemAllocatorFw.heapAllocator)) {
            Val instance = (Val) CallFw.getVal(arg);
            HeapObj heap = instance._UNPACK_();
            arg = (Val) CallFw.getArg(arg);


            if (arg.getType() == SymbolFw.symbol) {
                String sym = arg._UNPACK_().toString();
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
                public Value apply(State state) {
                    if (state != heap.state())
                        return Operation.unit;
                    return new AllocatedMemoryObj(heap, size).asVal();
                }
            }.asVal();
        }
        return null;
    }).asType();
}