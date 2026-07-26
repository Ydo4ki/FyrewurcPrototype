package org.fw.core.base;

public abstract class Type {

    public static Type of(Val.Box val) {
        return new ValType(val);
    }

    public abstract Val callInstance(Val instance, Val arg, Context context) throws Exception;

    public abstract Val asVal();

    public static final class ValType extends Type {

        private final Val.Box asVal;

        public ValType(Val.Box asVal) {
            this.asVal = asVal;
        }

        @Override
        public Val callInstance(Val instance, Val arg, Context context) {
            Val ret = asVal.call(Call.fwCall(instance, arg), context);

            // if (ret == Val.unspecified)
            //     return UnspecifiedCallFw.unspecifiedCall(instance, arg);

            return ret;
        }

        @Override
        public Val asVal() {
            return asVal;
        }

        @Override
        public String toString() {
            return asVal.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ValType)) return false;
            ValType that = (ValType) o;
            return java.util.Objects.equals(asVal, that.asVal);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(asVal);
        }
    }
}