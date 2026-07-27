package org.fw.core.lib.state;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.lib.VitFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.operation.*;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.telephonist;

public final class OperationFw {

    public static final Type operation = FW.telephonist("Operation", (arg, context) -> {
        return Unspecified.unspecified;
    }).asType();

    public static final Val _VitOperation = FW.telephonist((arg, context1) -> {
        if (!VitFw.isVit(arg.type()))
            return Unspecified.unspecified;

        Vit vit = arg._unpack();

        return FW.telephonist((rtEnv, context) -> Operation.vit(vit, RtEnv.of(rtEnv)).asVal());
    });
    public static final Val _WriteOperation = telephonist((arg, context1) -> {
        if (arg.type() != LaserPointerFw.laserPointer)
            return Unspecified.unspecified;

        Obj obj = arg._unpack();
        if (!(obj instanceof Obj.ValObj))
            return Unspecified.unspecified;

        return telephonist((arg1, context2) -> {
            return Operation.write((Obj.ValObj) obj, arg1).asVal();
        });
    });
    public static final Val _ReadOperation = telephonist((arg, context1) -> {
        if (arg.type() != LaserPointerFw.laserPointer)
            return Unspecified.unspecified;

        Obj obj = arg._unpack();
        if (!(obj instanceof Obj.ValObj))
            return Unspecified.unspecified;

        return Operation.read((Obj.ValObj) obj).asVal();
    });
    public static final Val _CreateNewObjectOperation = telephonist((arg, context1) -> {
        return new CreateObjectOperation(arg).asVal();
    });

    public static Val wrap(Operation operation) {
        if (operation == null) return Unspecified.unspecified;
        return operation.asVal();
    }

    public static Operation unwrap(Val operation) {
        if (operation.type() == OperationFw.operation)
            return operation._unpack(Operation.class);
        return null;
    }
}
