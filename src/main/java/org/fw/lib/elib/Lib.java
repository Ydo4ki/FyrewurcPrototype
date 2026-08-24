package org.fw.lib.elib;

import org.fw.core.base.Val;
import org.fw.lib.elib.expr.CompEnv;

import java.util.function.Function;

public final class Lib {

    public static Lib combine(Lib first, Lib... libs) {
        Lib actual = first;
        for (Lib lib : libs) {
            actual = combine(actual, lib);
        }
        return actual;
    }

    public static Lib combine(Lib parent, Lib over) {
        Val module;
        if (over.module == null) {
            module = parent.module;
        } else if (parent.module == null) {
            module = over.module;
        } else {
            module = ChainLinkFw.chain(ExtendedFw.extended, over.module, parent.module);
//            module = FW.telephonist(arg -> {
//                Val ret = over.module.call(arg);
//                if (Unspecified.isUnspecified(ret)) ret = parent.module.call(arg);
//                return ret;
//            });
        }

        Val extraCEnv;
        if (over.extraCEnv == null) {
            extraCEnv = parent.extraCEnv;
        } else if (parent.extraCEnv == null) {
            extraCEnv = over.extraCEnv;
        } else {
            extraCEnv = CompEnv.compEnv(over.extraCEnv, parent.extraCEnv);
        }

        Function<Val, Val> rtEnvAdjuster;
        if (over.rtEnvAdjuster == null) {
            rtEnvAdjuster = parent.rtEnvAdjuster;
        } else if (parent.rtEnvAdjuster == null) {
            rtEnvAdjuster = over.rtEnvAdjuster;
        } else {
            rtEnvAdjuster = over.rtEnvAdjuster.andThen(parent.rtEnvAdjuster);
        }
        return of(
                module,
                extraCEnv,
                rtEnvAdjuster
        );
    }

    public static Lib ofCEnv(Val extraCEnv) {
        return Lib.of(null, extraCEnv);
    }

    public static Lib ofModule(Val module) {
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
