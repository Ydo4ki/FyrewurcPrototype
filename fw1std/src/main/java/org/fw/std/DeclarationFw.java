package org.fw.std;

import org.fw.base.*;
import org.fw.core.FW;

import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.core.util.FwUtils;

import java.util.Objects;

public final class DeclarationFw {

    // I hope it will be possible to make it a struct later
    public static final Type declaration = FW.lambda_native("Declaration", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DeclarationFw.declaration)) {
            Val instance = CallFw.getVal(arg).asVal();
            arg = CallFw.getArg(arg).asVal();

            Declaration decl = instance._UNPACK_();
            if (arg.equalsSymbol("key")) {
                return decl.key();
            } else if (arg.equalsSymbol("constraint")) {
                return decl.constraint();
            }
        } else if (arg.equalsSymbol("builder")) {
            return FW.lambda_native("Declaration.builder", (key) -> {
                return FW.lambda_native("(call Declaration.builder " + key + ")", (constraint) -> {
                            if (!ConstraintFw.isConstraint(constraint))
                                return null;

                            return Val._NEW_INSTANCE_(DeclarationFw.declaration, new Declaration(key, constraint));
                        });
            });
        }
        return null;
    }).asType();

    public static Val getKey(Val declaration) {
        return declaration.get("key").asVal();
    }

    public static Val getConstraint(Val declaration) {
        return declaration.get("constraint").asVal();
    }

    public static Val declaration(Val key, Val constraint) {
        if (!ConstraintFw.isConstraint(constraint))
            throw new IllegalArgumentException();

        return Val._NEW_INSTANCE_(declaration, new Declaration(key, constraint));
    }

    public static final class Declaration {
        private final Val key;
        private final Val constraint;

        private Declaration(Val key, Val constraint) {
            this.key = key;
            this.constraint = constraint;
        }

        public Val key() {
            return key;
        }

        public Val constraint() {
            return constraint;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Declaration that = (Declaration) obj;
            return Objects.equals(this.key, that.key) &&
                    Objects.equals(this.constraint, that.constraint);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, constraint);
        }

        @Override
        public String toString() {
            return "Declaration[" +
                    "key=" + key + ", " +
                    "constraint=" + constraint + ']';
        }
    }

}
