package org.fw.lib.devicelib;

import org.fw.lib.elib.DeclaredFw;
import org.fw.lib.elib.Lib;
import org.fw.lib.elib.ModuleFw;

import static org.fw.core.FW.symbol;

public final class DeviceLib {

    public static final Lib lib = Lib.combine(
            PrimitiveLayoutsFw.lib,
            Lib.ofModule(ModuleFw.module(
                    DeclaredFw.declared(symbol("BlockDevice"), BlockDeviceFw.blockDevice)
            ))
    );
}
