package org.fw.lib.stdlib;

import org.fw.core.base.Val;
import org.fw.lib.stdlib.expr.CompEnv;

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
        }

        Val moduleInverted;
        if (over.moduleInverted == null) {
            moduleInverted = parent.moduleInverted;
        } else if (parent.moduleInverted == null) {
            moduleInverted = over.moduleInverted;
        } else {
            moduleInverted = ChainLinkFw.chain(ExtendedFw.extended, over.moduleInverted, parent.moduleInverted);
        }

        Val extraCEnv;
        if (over.extraCEnv == null) {
            extraCEnv = parent.extraCEnv;
        } else if (parent.extraCEnv == null) {
            extraCEnv = over.extraCEnv;
        } else {
            extraCEnv = CompEnv.compEnv(over.extraCEnv, parent.extraCEnv);
        }

        return of(
                module,
                moduleInverted,
                extraCEnv
        );
    }

    public static Lib ofCEnv(Val extraCEnv) {
        return Lib.of(null, extraCEnv);
    }

    public static Lib ofModule(Val module) {
        return Lib.of(module, (Val) null);
    }

    public static Lib of(Val module, Val extraCEnv) {
        return new Lib(module, ModuleFw.invert(module), extraCEnv);
    }

    public static Lib of(Val module, Function<Val, Val> rtEnvAdjuster) {
        return Lib.of(module, (Val)null);
    }

    public static Lib of(Val module, Val extraCEnv, Function<Val, Val> rtEnvAdjuster) {
        return of(module, ModuleFw.invert(module), extraCEnv);
    }

    public static Lib of(Val module, Val moduleInverted, Val extraCEnv) {
        return new Lib(module, moduleInverted, extraCEnv);
    }

    private final Val module;
    private final Val moduleInverted;
    private final Val extraCEnv;

    private final Val m_exports;

    private Lib(Val module, Val moduleInverted, Val extraCEnv) {
        this.module = module;
        this.moduleInverted = moduleInverted;
        this.extraCEnv = extraCEnv;
        m_exports = CompEnv.compEnv(
                ModuleFw.ModuleCEnvFw.compEnv(module),
                ModuleFw.ModuleCEnvFw.toExprCompEnv(moduleInverted),
                extraCEnv
        );
    }

    public Val exports() {
        return m_exports;
    }

    public Val module() {
        return module;
    }

    public Val extraCEnv() {
        return extraCEnv;
    }
}
