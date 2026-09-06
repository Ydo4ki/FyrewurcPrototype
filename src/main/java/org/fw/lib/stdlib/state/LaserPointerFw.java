package org.fw.lib.stdlib.state;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.AtomObj;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

public final class LaserPointerFw {
    // todo: make them predetermined for each scope, otherwise its possible to do a(b) != a(b)
    public static final Type laserPointer = FW.telephonist_native("LaserPointer", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, LaserPointerFw.laserPointer)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Obj obj = instance._UNPACK();

            if (arg.getType() == SymbolFw.symbol) {
                String s = arg._UNPACK().toString();
                switch (s) {
                    case "owner":
                        return obj.partOf().asVal();
                    case "read":
                        return Operation.read((AtomObj) obj).asVal();
                    case "write":
                        return FW.telephonist_native((arg1) -> Operation.write((AtomObj) obj, arg1).asVal());
                }
            }
            return null;
        }
        return null;
    }).asType();
}
