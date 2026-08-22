package org.fw.core.lib;

import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;

import java.util.function.Function;

public final class Lib {

    public static Lib combine(Lib... libs) {
        Lib actual = libs[0];
        for (int i = 1; i < libs.length; i++) {
            actual = combine(actual, libs[i]);
        }
        return actual;
    }

    public static Lib combine(Lib parent, Lib over) {
        return of(
                ChainLinkFw.chain(ExtendedFw.extended, over.module, parent.module),
                CompEnv.compEnv(over.extraCEnv, parent.extraCEnv),
                over.rtEnvAdjuster.andThen(parent.rtEnvAdjuster)
        );
    }

    public static Lib of(CompEnv extraCEnv) {
        return Lib.of(null, extraCEnv.asVal());
    }

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
            if (module == null) {
                exports = extraCEnv;
            } else {
                exports = CompEnv.compEnv(
                        ModuleFw.ModuleCEnvFw.compEnv(module),
                        extraCEnv
                );
            }
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

    public Function<Val, Val> getRtEnvAdjuster() {
        return rtEnvAdjuster;
    }
}
