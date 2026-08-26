package org.fw.core.state;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.ValObj;
import org.fw.core.state.operation.CreateObjectOperation;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.telephonist;

public final class LaserPointerFw {
    public static final Type laserPointer = FW.telephonist("LaserPointer", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, LaserPointerFw.laserPointer)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            Obj obj = instance._unpack();

            if (arg.type() == SymbolFw.symbol) {
                String s = arg._unpack().toString();
                switch (s) {
                    case "read":
                        return Operation.read((ValObj) obj).asVal();
                    case "write":
                        return FW.telephonist((arg1) -> Operation.write((ValObj) obj, arg1).asVal());
                }
            }
            return null;
        } else {
            if (arg.type() == SymbolFw.symbol) {
                String s = arg._unpack().toString();
                if (s.equals("new")) {
                    return LaserPointerFw._CreateNewObjectOperation;
                }
            }
        }
        return null;
    }).asType();
    public static final Val _ReadOperation = FW.telephonist((arg) -> {
        if (arg.type() != laserPointer)
            return null;

        Obj obj = arg._unpack();
        if (!(obj instanceof ValObj))
            return null;

        return Operation.read((ValObj) obj).asVal();
    });
    public static final Val _WriteOperation = FW.telephonist((arg) -> {
        if (arg.type() != laserPointer)
            return null;

        Obj obj = arg._unpack();
        if (!(obj instanceof ValObj))
            return null;

        return FW.telephonist((arg1) -> {
            return Operation.write((ValObj) obj, arg1).asVal();
        });
    });
    public static final Val _CreateNewObjectOperation = FW.telephonist((arg) -> {
        return new CreateObjectOperation(arg).asVal();
    });
}
