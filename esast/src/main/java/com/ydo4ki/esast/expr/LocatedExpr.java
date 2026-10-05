package com.ydo4ki.esast.expr;

import com.ydo4ki.esast.Location;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An expression with an associated location in the source code.
 *
 * @param <E> the type of the underlying expression
 */
public abstract class LocatedExpr<E extends Expr> {
    private final E expr;
    private final Location location;

    /**
     * Creates a located expression.
     *
     * @param expr the underlying expression
     * @param location the location in the source code
     */
    LocatedExpr(E expr, Location location) {
        this.expr = expr;
        this.location = location;
    }

    /**
     * Returns the underlying expression.
     *
     * @return the expression
     */
    public E getExpr() {
        return expr;
    }

    /**
     * Returns the location in the source code.
     *
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Splits this located expression by the given separator strings.
     *
     * @param separateLines strings to split by
     * @return a collection of located expression parts
     */
    public abstract Collection<? extends LocatedExpr<?>> split(String... separateLines);

    /**
     * Applies one of the given functions depending on the concrete node type.
     *
     * <p>If the current object is a {@link LocatedSymbol}, {@code ifSymbol} is
     * invoked. If the current object is a {@link LocatedExprList},
     * {@code ifList} is invoked.</p>
     *
     * @param <T> the return type
     * @param ifSymbol the function to apply to a {@link LocatedSymbol}
     * @param ifList the function to apply to a {@link LocatedExprList}
     * @see Expr#matched(Function, Function)
     * @return the result of applying the selected function
     */
    public <T> T matched(Function<LocatedSymbol, T> ifSymbol, Function<LocatedExprList, T> ifList) {
        return this instanceof LocatedSymbol
                ? ifSymbol.apply((LocatedSymbol) this)
                : ifList.apply((LocatedExprList) this);
    }

    /**
     * Returns a new located expression in which all occurrences of the specified
     * symbol are replaced with the new expression.
     *
     * <p>The replacement is recursive: for a {@link LocatedExprList}, all child
     * elements are traversed.</p>
     *
     * @param symbol the symbol to search for
     * @param newValue the new expression to replace with
     * @see Expr#replace(Symbol, Expr)
     * @return a new located expression with the replacement performed
     */
    public LocatedExpr<? extends Expr> replace(Symbol symbol, Expr newValue) {
        return (LocatedExpr<? extends Expr>) matched(sym -> {
            if (sym.getValue().equals(symbol.getValue())) return newValue;
            else return sym;
        }, list
                -> ExprList.of(list.getLocation(), list.getBracketsType(), list.getElements().stream()
                .map(e -> e.replace(symbol, newValue)).collect(Collectors.toList())));
    }
}
