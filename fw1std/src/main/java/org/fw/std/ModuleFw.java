package org.fw.std;

import com.ydo4ki.fw.internal.lib.stdlib.ExtendedFw;
import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;

import org.fw.std.dvec.DVecFw;
import org.fw.core.util.FwUtils;

import java.util.Arrays;

// no I literally just made a telemap XD
public final class ModuleFw {
    public static final Type module = FW.lambda_native("Module", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ModuleFw.module)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Module module = instance._UNPACK_();
            for (Val declared : module.declareds()) {
                if (DeclaredFw.getKey(declared).equals(arg)) {
                    return DeclaredFw.getValue(declared);
                }
            }
        } else if (arg.equalsSymbol("construct")) {
            return FW.lambda_native("Module.constructor", (arg1) -> {
                if (!arg1.getType().equals(DVecFw.dVec))
                    return null;

                Val[] values = arg1._UNPACK_(); // Ok I don't even care at this point
                for (Val value : values) {
                    if (!value.getType().equals(DeclaredFw.declared))
                        return null;
                }

                return Val._NEW_INSTANCE_(ModuleFw.module, new Module(values));
            });
        } else if (arg.equalsSymbol("contains-key")) {
            return FW.lambda_native("Module.contains-key", (arg1) -> {
                if (!arg1.getType().equals(ModuleFw.module)) return null;
                Module mod = arg1._UNPACK_();
                return FW.lambda_native((key) -> mod.containsKey(key) ? BoolFw._true : BoolFw._false);
            });
        }

        return null;
    }).asType();

    public static Val invert(Val module) {
        if (module == null) return null;
        if (module.getType() != ModuleFw.module)
            return null;

        Module m = module._UNPACK_();
        Val[] newd = new Val[m.declareds.length];
        for (int i = 0; i < m.declareds.length; i++) {
            newd[i] = DeclaredFw.declared(
                    DeclaredFw.getValue(m.declareds[i]),
                    DeclaredFw.getKey(m.declareds[i])
            );
        }
        return Val._NEW_INSTANCE_(module.getType(), new Module(newd));
    }

    public static Value merge(Value module, Value... modules) {
        for (Value val : modules) {
            if (val == null) continue;
            module = ChainLinkFw.chain(ExtendedFw.extended, module, val);
        }
        return module;
    }

    public static Val merge(Val module, Val... modules) {
        for (Value val : modules) {
            if (val == null) continue;
            module = (Val) ChainLinkFw.chain(ExtendedFw.extended, module, val);
        }
        return module;
    }

    public static Val module(Val... values) {
        for (Val value : values) {
            if (!value.getType().equals(DeclaredFw.declared))
                throw new IllegalArgumentException(value.toString());
        }
        return Val._NEW_INSTANCE_(module, new Module(values));
    }

    // todo: replace with map, order shouldn't matter
    public static final class Module {
        private final Val[] declareds;

        public Module(Val[] declareds) {
            this.declareds = declareds;
        }

        public Val[] declareds() {
            return declareds;
        }

        public boolean containsKey(Val key) {
            for (Val declared : declareds) {
                if (DeclaredFw.getKey(declared).equals(key)) return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Module module = (Module) o;
            if (declareds.length != module.declareds.length) return false;
            for (int i = 0; i < declareds.length; i++) {
                if (!declareds[i].equals(module.declareds[i])) return false;
            }
            return true;
//            return Objects.deepEquals(declareds, module.declareds);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(declareds);
        }
    }


}
