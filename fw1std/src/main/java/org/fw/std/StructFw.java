package org.fw.std;

import org.fw.base.BoolFw;
import org.fw.base.CallFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;

import org.fw.std.dvec.DVecFw;
import org.fw.core.util.FwUtils;

import java.util.Arrays;
import java.util.Objects;

import static org.fw.core.FW.symbol;

// so should the order of fields matter or not?
public final class StructFw {
    public static final Type struct = FW.lambda_native("Struct", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StructFw.struct)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            Struct struct = instance._UNPACK_();
            if (FwUtils.isTypeApiCall(arg, instance.asType())) {
                Val strInstance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);
                Val[] values = strInstance._UNPACK_();
                int index = struct.indexOf(arg);
                if (index == -1) return null;
                return values[index];
            } else if (arg.equalsSymbol("builder")) {
                return structBuilder(struct, instance);
            } else if (arg.equalsSymbol("fields")) {
                return DVecFw.vec(struct.fields);
            }
        } else if (arg.equalsSymbol("construct")) {
            return FW.lambda_native("Struct.construct", (payload) -> {
                if (!payload.getType().equals(DVecFw.dVec))
                    return null;
                Val[] fields = payload._UNPACK_();
                for (Val field : fields) {
                    if (!field.getType().equals(DeclarationFw.declaration))
                        return null; // some day I'll add proper errors
                }
                return Val._NEW_INSTANCE_(StructFw.struct, new Struct(fields));
            });
        }
        return null;
    }).asType();

    public static Type struct(Val... fields) {
        for (Val value : fields) {
            if (!value.getType().equals(DeclarationFw.declaration))
                throw new IllegalArgumentException(value.toString());
        }
        return Val._NEW_INSTANCE_(StructFw.struct, new Struct(fields)).asType();
    }

    public static Val instance(Type struct, Val... values) {
        return Val._NEW_INSTANCE_(struct, values);
    }

    public static final class Struct {
        public final Val[] fields;

        private Struct(Val[] fields) {
            this.fields = fields;
        }

        public int indexOf(Val key) {
            for (int i = 0; i < fields.length; i++) {
                Val field = fields[i];
                if (DeclarationFw.getKey(field).equals(key))
                    return i;
            }
            return -1;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Struct struct = (Struct) o;
            return Objects.deepEquals(fields, struct.fields);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(fields);
        }
    }

    private static final class StructBuilder {
        private final Struct struct;
        private final Val sameStructButItsAVal;
        private final Val[] progress;

        private StructBuilder(Struct struct, Val sameStructButItsAVal, Val[] progress) {
            this.struct = struct;
            this.sameStructButItsAVal = sameStructButItsAVal;
            this.progress = progress;
        }
    }

    private static Val structBuilder(Struct struct, Val sameStructButItsAVal) {
        if (struct.fields.length == 0) return Val._NEW_INSTANCE_(sameStructButItsAVal.asType(), new Val[0]);
        return Val._NEW_INSTANCE_(structBuilder, new StructBuilder(struct, sameStructButItsAVal, new Val[0]));
    }

    private static final Type structBuilder = FW.lambda_native("StructBuilder", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StructFw.structBuilder)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            StructBuilder payload = instance._UNPACK_();

            Val constraint = DeclarationFw.getConstraint(payload.struct.fields[payload.progress.length]);
            Val val = ((Val) constraint.call(symbol("check")));
            if (val.call(arg) != BoolFw._true) {
                return null;
            }

            Val[] values = DVecFw.arAppended(payload.progress, arg);
            if (values.length == payload.struct.fields.length) {
                return Val._NEW_INSTANCE_(payload.sameStructButItsAVal.asType(), values);
            }
            return Val._NEW_INSTANCE_(StructFw.structBuilder, new StructBuilder(payload.struct, payload.sameStructButItsAVal, values));
        }
        return null;
    }).asType();


    public static final Val module = ModuleFw.module(
            DeclaredFw.declared(symbol("Struct"), struct)
    );
}