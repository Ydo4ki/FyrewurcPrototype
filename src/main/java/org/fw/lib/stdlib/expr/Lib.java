package org.fw.lib.stdlib.expr;

import org.fw.core.abstrait.Value;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.ChainLinkFw;
import com.ydo4ki.fw.internal.lib.stdlib.ExtendedFw;
import org.fw.lib.stdlib.ModuleFw;

public final class Lib {

    public static Lib combine(Lib first, Lib... libs) {
        Lib actual = first;
        for (Lib lib : libs) {
            actual = combine(actual, lib);
        }
        return actual;
    }

    public static Lib combine(Lib parent, Lib over) {
        Value module;
        if (over.module == null) {
            module = parent.module;
        } else if (parent.module == null) {
            module = over.module;
        } else {
            module = ChainLinkFw.chain(ExtendedFw.extended, parent.module, over.module);
        }

        Value moduleInverted;
        if (over.moduleInverted == null) {
            moduleInverted = parent.moduleInverted;
        } else if (parent.moduleInverted == null) {
            moduleInverted = over.moduleInverted;
        } else {
            moduleInverted = ChainLinkFw.chain(ExtendedFw.extended, parent.moduleInverted, over.moduleInverted);
        }

        Value extraCEnv;
        if (over.extraCEnv == null) {
            extraCEnv = parent.extraCEnv;
        } else if (parent.extraCEnv == null) {
            extraCEnv = over.extraCEnv;
        } else {
            extraCEnv = CompEnv.compEnv(parent.extraCEnv, over.extraCEnv);
        }

        return new Lib(
                module,
                moduleInverted,
                extraCEnv
                , CompEnv.compEnv(parent.exports(), over.exports())
        );
    }

    public static Lib ofCEnv(Value extraCEnv) {
        return Lib.of(null, extraCEnv);
    }

    public static Lib ofModule(Value module) {
        return Lib.of(module, null);
    }

    public static Lib of(Value module, Value extraCEnv) {
        return of(module, ModuleFw.invert((Val)module), extraCEnv);
    }

    public static Lib of(Value module, Value moduleInverted, Value extraCEnv) {
        return new Lib(module, moduleInverted, extraCEnv);
    }

    private final Value module;

    private final Value moduleInverted;
    private final Value extraCEnv;
    private final Value m_exports;

    private Lib(Value module, Value moduleInverted, Value extraCEnv) {
        this.module = module;
        this.moduleInverted = moduleInverted;
        this.extraCEnv = extraCEnv;
        m_exports = CompEnv.compEnv(
                extraCEnv,
                moduleInverted == null ? null : ModuleFw.ModuleCEnvFw.toExprCompEnv((Val)moduleInverted),
                module == null ? null : ModuleFw.ModuleCEnvFw.compEnv((Val)module)
        );
    }

    private Lib(Value module, Value moduleInverted, Value extraCEnv, Value mExports) {
        this.module = module;
        this.moduleInverted = moduleInverted;
        this.extraCEnv = extraCEnv;
        m_exports = mExports;
    }

    public Value exports() {
        return m_exports;
    }

    public Value module() {
        return module;
    }

    public Value extraCEnv() {
        return extraCEnv;
    }
}
