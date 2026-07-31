package org.fw.core.lib;

import org.fw.core.base.Val;
import org.fw.core.base.context.Context;
import org.fw.core.lib.expr.CompEnv;

import java.util.function.Function;

public final class Lib {

    public static Lib of(Val module) {
        return Lib.of(module, (Val) null);
    }

    public static Lib of(Val module, Val extraCEnv) {
        return Lib.of(module, extraCEnv, null);
    }

    public static Lib of(Val module, Function<Val, Val> rtEnvAdjuster) {
        return Lib.of(module, null, rtEnvAdjuster);
    }

    public static Lib of(Val module, Val extraCEnv, Function<Val, Val> rtEnvAdjuster) {
        return new Lib(module, extraCEnv, rtEnvAdjuster);
    }

    private final Val module;
    private final Val extraCEnv;
    private final Function<Val, Val> rtEnvAdjuster;
    private final Val exports;

    private Lib(Val module, Val extraCEnv, Function<Val, Val> rtEnvAdjuster) {
        this.module = module;
        this.extraCEnv = extraCEnv;
        this.rtEnvAdjuster = rtEnvAdjuster;
        if (extraCEnv == null) {
            exports = ModuleFw.ModuleCEnvFw.compEnv(module);
        } else {
            exports = CompEnv.compEnv(Context.outOf,
                    ModuleFw.ModuleCEnvFw.compEnv(module),
                    extraCEnv
            );
        }
    }

    public Val exports() {
        return exports;
    }

    public Val module() {
        return module;
    }

    public Val extraCEnv() {
        return extraCEnv;
    }
}
