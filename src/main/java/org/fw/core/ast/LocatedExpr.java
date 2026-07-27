package org.fw.core.ast;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LocatedExpr<E extends Expr> {
    private final E expr;
    private final Location location;

    LocatedExpr(E expr, Location location) {
        this.expr = expr;
        this.location = location;
    }

    public E getExpr() {
        return expr;
    }

    public Location getLocation() {
        return location;
    }


    public abstract Collection<? extends LocatedExpr<?>> split(String... separateLines);

    public <T> T matched(Function<LocatedSymbol, T> ifSymbol, Function<LocatedExprList, T> ifList) {
        return this instanceof LocatedSymbol
                ? ifSymbol.apply((LocatedSymbol) this)
                : ifList.apply((LocatedExprList) this);
    }

    public LocatedExpr<? extends Expr> replace(Symbol symbol, Expr newValue) {
        return (LocatedExpr<? extends Expr>) matched(sym -> {
            if (sym.getValue().equals(symbol.getValue())) return newValue;
            else return sym;
        }, list
                -> ExprList.of(list.getLocation(), list.getBracketsType(), list.getElements().stream()
                .map(e -> e.replace(symbol, newValue)).collect(Collectors.toList())));
    }
}
