package org.fw.lib.memlib;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.memlib.obj.AllocatedMemoryObj;
import org.fw.lib.jlib.data.JIntFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

public final class AllocatedMemoryFw {
    public static final Type allocatedMemory = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, AllocatedMemoryFw.allocatedMemory)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

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
                                protected boolean isPure0() {
                                    return false;
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
