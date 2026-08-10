package org.fw.core.state;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.ValObj;
import org.fw.core.state.operation.CreateObjectOperation;
import org.fw.core.state.operation.Operation;

import static org.fw.core.FW.telephonist;

public final class LaserPointerFw {
    public static final Type laserPointer = FW.telephonist("LaserPointer", (arg) -> null).asType();
    public static final Val _ReadOperation = telephonist((arg) -> {
        if (arg.type() != laserPointer)
            return null;

        Obj obj = arg._unpack();
        if (!(obj instanceof ValObj))
            return null;

        return Operation.read((ValObj) obj).asVal();
    });
    public static final Val _WriteOperation = telephonist((arg) -> {
        if (arg.type() != laserPointer)
            return null;

        Obj obj = arg._unpack();
        if (!(obj instanceof ValObj))
            return null;

        return telephonist((arg1) -> {
            return Operation.write((ValObj) obj, arg1).asVal();
        });
    });
    public static final Val _CreateNewObjectOperation = telephonist((arg) -> {
        return new CreateObjectOperation(arg).asVal();
    });
}
