package org.fw.core.vit;

import org.fw.core.ast.Expr;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class VitCall extends Vit {

    public VitCall(Vit func, Vit arg) {
        this(func, arg, func.isConst() && arg.isConst(), func.isPure() && arg.isPure());
    }

    @Override
    public Val eval(Context context) {
        return func.eval(context).call(arg.eval(context), context);
    }

    @Override
    public String toString() {
        return "(VitCall " + func + " " + arg + ")";
    }

    public Collection<? extends Expr> exprs(Context context) {
        List<Expr> elements = new ArrayList<>();
        if (func() instanceof VitCall) {
            elements.addAll(((VitCall) func()).exprs(context));
        } else {
            elements.add(VitFw.wrap(func()).toExpr(context));
        }
        elements.add(VitFw.wrap(arg()).toExpr(context));
        return elements;
    }

    private final Vit func;
    private final Vit arg;
    private final boolean isConst;
    private final boolean isPure;

    private VitCall(Vit func, Vit arg, boolean isConst, boolean isPure) {
        Objects.requireNonNull(func);
        Objects.requireNonNull(arg);
        this.func = func;
        this.arg = arg;
        this.isConst = isConst;
        this.isPure = isPure;
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
        return this.isConst == that.isConst &&
                this.isPure == that.isPure &&
                Objects.equals(this.func, that.func) &&
                Objects.equals(this.arg, that.arg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(func, arg, isConst, isPure);
    }
}