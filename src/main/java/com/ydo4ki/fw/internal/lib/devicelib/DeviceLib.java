package com.ydo4ki.fw.internal.lib.devicelib;

import org.fw.lib.stdlib.DeclaredFw;
import org.fw.lib.stdlib.Lib;
import org.fw.lib.stdlib.ModuleFw;

import static org.fw.core.FW.symbol;

public final class DeviceLib {

    public static final Lib lib = Lib.combine(
            PrimitiveLayoutsFw.lib,
            Lib.ofModule(ModuleFw.module(
                    DeclaredFw.declared(symbol("BlockDevice"), BlockDeviceFw.blockDevice)
            ))
    );
}
