package org.fw.ast;

import java.util.Collection;
import java.util.function.Function;

/**
 * @author Sulphuris
 * @since 4/11/2025 4:36 PM
 */
public abstract class Expr {

    private final Location location;

    // sealed
    Expr(Location location) {
        this.location = location;
    }

    public final Location getLocation() {
        return location;
    }

    public abstract Collection<? extends Expr> split(String... separateLines);

    public <T> T matched(Function<Symbol, T> ifSymbol, Function<ExprList, T> ifList) {
        return this instanceof Symbol
                ? ifSymbol.apply((Symbol) this)
                : ifList.apply((ExprList) this);
    }

    public Expr replace(Symbol symbol, Expr newValue) {
        return matched(sym -> {
            if (sym.getValue().equals(symbol.getValue())) return newValue;
            else return sym;
        }, list
                -> ExprList.of(list.getLocation(), list.getBracketsType(), list.getElements().stream().map(e -> e.replace(symbol, newValue)).toList()));
    }
}

