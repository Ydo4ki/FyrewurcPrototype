package org.fw.std;

import org.fw.base.CallFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.commons.ValAdapter;

import org.fw.core.util.FwUtils;

import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class DeclaredFw {

//    public static final Val colon = FW.telephonist(":", (arg, context) -> {
//        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
//            Val size = arg.call(symbol("size"), context);
//            Val cEnv = arg.call(symbol("comp-env"), context);
//
//            int isize = size._unpack(BigInteger.class).intValue();
//            if (isize != 2)
//                return Val.unspecified;
//
//            Val name = arg.call(DIntFw.dint(0), context);
//            if (!name.type().equals(ExprFw.symbol))
//                return Val.unspecified; // symbol expected
//
//            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
//            if (!VitFw.isVit(value.type())) return value; // error idk
//
//            try {
//                return VitFw.wrap(Vit.val(DeclaredFw.declared.asVal()).call(symbol("builder")).call(name).call(VitFw.unwrap(value)));
//            } catch (VitCompilationException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return Val.unspecified;
//    });

    // I hope it will be possible to make it a struct later
    public static final Type declared = FW.lambda_native("Declared", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DeclaredFw.declared)) {
            Val instance = CallFw.getVal(arg).asVal();
            arg = CallFw.getArg(arg).asVal();

            Declared decl = instance._UNPACK_();
            if (arg.equalsSymbol("key")) {
                return decl.key();
            } else if (arg.equalsSymbol("value")) {
                return decl.value();
            }
        } else if (arg.equalsSymbol("builder")) {
            return FW.lambda_native("Declared.builder",
                    (name) -> FW.lambda_native(
                            (value) -> declared(name, value)));
        }
        return null;
    }).asType();

    public static Val getKey(Val declared) {
        return declared.call(symbol("key")).asVal();
    }

    public static Val getValue(Val declared) {
        return declared.call(symbol("value")).asVal();
    }


    public static Val declared(ValAdapter key, ValAdapter value) {
        return Val._NEW_INSTANCE_(DeclaredFw.declared, new Declared(key.asVal(), value.asVal()));
    }

    public static final class Declared {
        private final Val key;
        private final Val value;

        private Declared(Val key, Val value) {
            this.key = key;
            this.value = value;
        }

        public Val key() {
            return key;
        }

        public Val value() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Declared that = (Declared) obj;
            return Objects.equals(this.key, that.key) &&
                    Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "Declared[" +
                    "key=" + key + ", " +
                    "value=" + value + ']';
        }
    }

}
