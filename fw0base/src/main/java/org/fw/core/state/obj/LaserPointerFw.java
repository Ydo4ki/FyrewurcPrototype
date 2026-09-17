package org.fw.core.state.obj;

import org.fw.core.FW;
import org.fw.base.CallFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.util.Objects;

public final class LaserPointerFw {
    // todo: make them predetermined for each scope, otherwise its possible to do a(b) != a(b)
    public static final Type laserPointer = FW.lambda_native("LaserPointer", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, LaserPointerFw.laserPointer)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            ValObj obj = instance._UNPACK_();

            if (arg.getType() == SymbolFw.symbol) {
                String s = arg._UNPACK_().toString();
                switch (s) {
                    case "owner":
                        return obj.parent().asValHandle();
                    case "read":
                        return Operation.read(obj).asVal();
                    case "write":
                        return FW.lambda_native((arg1) -> Operation.write(obj, arg1).asVal());
                }
            }
            return null;
        }
        return null;
    }).asType();

    public static final class ValObj {
        private final Scope owner;
        private final Val key;

        ValObj(Scope owner, Val key) {
            Objects.requireNonNull(owner);
            this.owner = owner;
            this.key = key;
        }

        public Obj parent() {
            return owner;
        }

        public Val read(State state) {
            if (this.isInside(state))
                return Operation.unit; // c'mon at least use exceptions you're getting too far with this
            return owner.get(key);
        }

        public void write(State state, Val x) {
            if (this.isInside(state))
                return;
            owner.set(key, x);
        }

        private final Val asVal = Val._NEW_INSTANCE_(laserPointer, this);

        public Val asValHandle() {
            return asVal;
        }

        boolean isInside(State state) {
            State p = this.parent();
            while (p != null) {
                if (p == state)
                    return true;

                p = p.parent();
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ValObj valObj = (ValObj) o;
            return Objects.equals(owner, valObj.owner) && Objects.equals(key, valObj.key) && Objects.equals(asVal, valObj.asVal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, key, asVal);
        }
    }
}
