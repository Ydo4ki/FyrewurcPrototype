package com.ydo4ki.fw.lib.memlib;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import com.ydo4ki.fw.lib.memlib.obj.AllocatedMemoryObj;
import com.ydo4ki.fw.lib.jlib.data.JIntFw;
import org.fw.core.contract.InvokeContract;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

public final class AllocatedMemoryFw {
    public static final Type allocatedMemory = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, AllocatedMemoryFw.allocatedMemory)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            AllocatedMemoryObj amo = instance._unpack();

            if (arg.type() == SymbolFw.symbol) {
                String sym = arg._unpack().toString();
                switch (sym) {
                    case "put":
                        return FW.telephonist(arg1 -> {
                            if (arg1.type() != JIntFw.jint)
                                return null;
                            int v = arg1._unpack();
                            return new Operation() {
                                @Override
                                public Val apply(State state) {
                                    amo.buffer().putInt(v);
                                    return Operation.unit;
                                }

                                @Override
                                public InvokeContract contract() {
                                    return InvokeContract.unknown();
                                }
                            }.asVal();
                        });
                }
            }

            // uhhh errrr
            // yeah right
            return null;
        }
        return null;
    }).asType();
}
