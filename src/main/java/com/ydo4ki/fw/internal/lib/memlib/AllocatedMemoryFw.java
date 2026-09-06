package com.ydo4ki.fw.internal.lib.memlib;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import com.ydo4ki.fw.internal.lib.memlib.obj.AllocatedMemoryObj;
import com.ydo4ki.fw.internal.lib.jlib.data.JIntFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

public final class AllocatedMemoryFw {
    public static final Type allocatedMemory = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, AllocatedMemoryFw.allocatedMemory)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            AllocatedMemoryObj amo = instance._unpack();

            if (arg.getType() == SymbolFw.symbol) {
                String sym = arg._unpack().toString();
                switch (sym) {
                    case "put":
                        return FW.telephonist(arg1 -> {
                            if (arg1.getType() != JIntFw.jint)
                                return null;
                            int v = arg1._unpack();
                            return new Operation() {
                                @Override
                                public Val apply(State state) {
                                    amo.buffer().putInt(v);
                                    return Operation.unit;
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
