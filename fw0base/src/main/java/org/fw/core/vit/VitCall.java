package org.fw.core.vit;

import org.fw.core.abstrait.Value;
import org.fw.base.Val;
import org.fw.core.state.obj.State;

import java.util.Objects;

public final class VitCall extends Vit {

    public VitCall(Vit func, Vit arg) {
        this(func, arg, func.isConst() && arg.isConst(), func.isPure() && arg.isPure());
    }

    @Override
    public Value eval(Value rtEnv, State state) {
        if (isPreDetermied != null)
            return isPreDetermied;
        return func.eval(rtEnv, state).call(arg.eval(rtEnv, state));
    }

    @Override
    public String toString() {
        return "(VitCall " + func + " " + arg + ")";
    }

    private final Vit func;
    private final Vit arg;
    private final boolean isConst;
    private final boolean isPure;
    private final Value isPreDetermied;

    private VitCall(Vit func, Vit arg, boolean isConst, boolean isPure) {
        Objects.requireNonNull(func);
        Objects.requireNonNull(arg);
        this.func = func;
        this.arg = arg;
        this.isPure = isPure;
        this.isConst = isConst;
        if (isConst && isPure) {
            Val val = func.eval();
            this.isPreDetermied = (Val) val.call(arg.eval());
        } else {
            this.isPreDetermied = null;
        }

    }

    public Vit func() {
        return func;
    }

    public Vit arg() {
        return arg;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isPure() {
        return isPure;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        VitCall that = (VitCall) obj;
        return  Objects.equals(this.func, that.func) &&
                Objects.equals(this.arg, that.arg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(func, arg);
    }
}