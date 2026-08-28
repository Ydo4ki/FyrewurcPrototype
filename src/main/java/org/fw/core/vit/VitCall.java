package org.fw.core.vit;

import org.fw.core.ast.Expr;
import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.base.contract.CallContract;
import org.fw.lib.stdlib.VitFw;
import org.fw.core.state.obj.State;
import org.fw.lib.stdlib.expr.CompEnv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class VitCall extends Vit {

    public VitCall(Vit func, Vit arg) {
        this(func, arg, func.isConst() && arg.isConst(), func.isPure() && arg.isPure());
    }

    @Override
    public Val eval(RtEnv rtEnv, State state) {
        if (isConst != null)
            return isConst;
        return func.eval(rtEnv, state).call(arg.eval(rtEnv, state));
    }

    @Override
    public String toString() {
        return "(VitCall " + func + " " + arg + ")";
    }

    public Collection<? extends Expr> exprs(CompEnv compEnv) {
        List<Expr> elements = new ArrayList<>();
        if (func() instanceof VitCall) {
            elements.addAll(((VitCall) func()).exprs(compEnv));
        } else {
            elements.add(VitFw.wrap(func()).toExpr(compEnv));
        }
        elements.add(VitFw.wrap(arg()).toExpr(compEnv));
        return elements;
    }

    private final Vit func;
    private final Vit arg;
    private final Val isConst;
    private final boolean isPure;
    private CallContract contract;

    private VitCall(Vit func, Vit arg, boolean isConst, boolean isPure) {
        Objects.requireNonNull(func);
        Objects.requireNonNull(arg);
        this.func = func;
        this.arg = arg;
        this.isPure = isPure;
        if (isConst && isPure) {
            this.isConst = func.eval().call(arg.eval());
        } else {
            this.isConst = null;
        }

    }

    public Vit func() {
        return func;
    }

    public Vit arg() {
        return arg;
    }

    public boolean isConst() {
        return isConst != null;
    }

    public boolean isPure() {
        return isPure;
    }

    @Override
    public CallContract evalContract() {
        if (contract == null) {
            if (isConst()) {
                contract = CallContract.constant(this.isConst);
            } else {
                contract = CallContract.unknown();
            }
        }
        return contract;
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