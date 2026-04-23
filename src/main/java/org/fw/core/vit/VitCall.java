package org.fw.core.vit;

import org.fw.core.ast.Expr;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public record VitCall(Vit func, Vit arg, boolean isConst, boolean isPure) implements Vit {

    public VitCall {
        Objects.requireNonNull(func);
        Objects.requireNonNull(arg);
    }

    public VitCall(Vit func, Vit arg) {
        this(func, arg, func.isConst() && arg.isConst(), func.isPure() && arg.isPure());
    }

    @Override
    public Val eval(Context context) {
        return func.eval(context).call(arg.eval(context), context);
    }

    @Override
    public boolean isConst() {
        return isConst;
    }

    @Override
    public boolean isPure() {
        return isPure;
    }

    @Override
    public boolean isLocal(Context context) {
        return func().isLocal(context) && arg.isLocal(context);
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
}
