package com.ydo4ki.esast.expr;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base class for the expressly-symbolic abstract syntax tree (ESAST).<br>
 * The tree structure is immutable and requires creation of a new instance if need be changed.
 *
 * <p>The tree is built from two types of nodes:</p>
 * <ul>
 *     <li>{@link Symbol} — an atomic token that is not a bracket;</li>
 *     <li>{@link ExprList} — a list of expressions grouped by brackets.</li>
 * </ul>
 *
 * <p>The constructor is package-private access, restricting the hierarchy
 * to {@link Symbol} and {@link ExprList} within the package.</p>
 *
 * @author Sulphuris
 * @see Symbol
 * @see ExprList
 * @since 4/11/2025 4:36 PM
 */
public abstract class Expr {

    // sealed
    Expr() {
        if (!(this instanceof Symbol) && !(this instanceof ExprList))
            throw new IllegalStateException("Sealed abstract class");
    }

    /**
     * Splits this expression by the given separator strings.
     *
     * @param separateLines strings to split by
     * @return a collection of expression parts
     */
    public abstract Collection<? extends Expr> split(String... separateLines);

    /**
     * Applies one of the given functions depending on the concrete node type.
     *
     * <p>If the current object is a {@link Symbol}, {@code ifSymbol} is invoked.
     * If the current object is an {@link ExprList}, {@code ifList} is invoked.</p>
     *
     * @param <T>      the return type
     * @param ifSymbol the function to apply to a {@link Symbol}
     * @param ifList   the function to apply to an {@link ExprList}
     * @return the result of applying the selected function
     */
    public <T> T matched(Function<Symbol, T> ifSymbol, Function<ExprList, T> ifList) {
        return this instanceof Symbol
                ? ifSymbol.apply((Symbol) this)
                : ifList.apply((ExprList) this);
    }

    /**
     * Returns a new expression in which all occurrences of the specified symbol
     * are replaced with the new expression.
     *
     * <p>The replacement is recursive: for an {@link ExprList}, all child
     * elements are traversed.</p>
     *
     * @param symbol   the symbol to search for
     * @param newValue the new expression to replace with
     * @return a new expression with the replacement performed
     */
    public Expr replace(Symbol symbol, Expr newValue) {
        return matched(sym -> {
            if (sym.getValue().equals(symbol.getValue())) return newValue;
            else return sym;
        }, list
                -> ExprList.of(list.getBracketsType(), list.getElements().stream()
                .map(e -> e.replace(symbol, newValue)).collect(Collectors.toList())));
    }
}

