package org.fw.std.state;

import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.std.VitFw;

public final class OperationLibFw {
    public static final Val _VitOperation = FW.lambda_native((arg) -> {
        if (!VitFw.isVit(arg.getType()))
            return null;

        Vit vit = arg._UNPACK_();

        return FW.lambda_native((rtEnv) -> Operation.vit(vit, rtEnv).asVal());
    });
}
