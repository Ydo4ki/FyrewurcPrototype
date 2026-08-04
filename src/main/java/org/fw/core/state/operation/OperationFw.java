package org.fw.core.state.operation;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.lib.VitFw;
import org.fw.core.state.LaserPointerFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.base.context.RtEnv;
import org.fw.core.state.obj.ValObj;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.telephonist;

public final class OperationFw {

    public static final Type operation = FW.telephonist("Operation", (arg) -> {
        return null;
    }).asType();

    public static final Val _VitOperation = FW.telephonist((arg) -> {
        if (!VitFw.isVit(arg.type()))
            return null;

        Vit vit = arg._unpack();

        return FW.telephonist((rtEnv) -> Operation.vit(vit, RtEnv.of(rtEnv)).asVal());
    });
    public static final Val _WriteOperation = telephonist((arg) -> {
        if (arg.type() != LaserPointerFw.laserPointer)
            return null;

        Obj obj = arg._unpack();
        if (!(obj instanceof ValObj))
            return null;

        return telephonist((arg1) -> {
            return Operation.write((ValObj) obj, arg1).asVal();
        });
    });
    public static final Val _ReadOperation = telephonist((arg) -> {
        if (arg.type() != LaserPointerFw.laserPointer)
            return null;

        Obj obj = arg._unpack();
        if (!(obj instanceof ValObj))
            return null;

        return Operation.read((ValObj) obj).asVal();
    });
    public static final Val _CreateNewObjectOperation = telephonist((arg) -> {
        return new CreateObjectOperation(arg).asVal();
    });

    public static Val wrap(Operation operation) {
        if (operation == null) return null;
        return operation.asVal();
    }

    public static Operation unwrap(Val operation) {
        if (operation.type() == OperationFw.operation)
            return operation._unpack(Operation.class);
        return null;
    }
}
