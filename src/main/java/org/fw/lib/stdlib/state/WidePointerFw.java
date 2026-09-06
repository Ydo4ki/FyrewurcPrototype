package org.fw.lib.stdlib.state;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.state.array.CreateArrayOperation;
import org.fw.lib.stdlib.state.array.ValArrayObj;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.telephonist;

public final class WidePointerFw {
    public static final Type widePointer = FW.telephonist("WidePointer", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, WidePointerFw.widePointer)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            ValArrayObj vao = instance._unpack();
            if (arg.getType() == SymbolFw.symbol) {
                String v = arg._unpack().toString();
                if (v.equals("size")) {
                    // this one's pure since array size cannot change after its creation
                    // wait I just realized
                    // maybe we don't need a separate class of objects for this
                    return DIntFw.dint(vao.size());
                }
            }
            return null;
        }
        return null;
    }).asType();

    public static final Val _CreateNewArrayOperation = FW.telephonist(size -> FW.telephonist(init -> {
        return new CreateArrayOperation(
                DIntFw.unwrap(size).intValueExact(),
                i -> {
                    return OperationFw.unwrap(init.call(DIntFw.dint(i)));
                }).asVal();
    }));
}
