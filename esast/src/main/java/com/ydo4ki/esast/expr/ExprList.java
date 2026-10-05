package com.ydo4ki.esast.expr;

import com.ydo4ki.esast.Location;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A node of the ESAST representing a list of expressions grouped by brackets.
 *
 * <p>For example, the expression {@code (a b c)} can be represented as an
 * {@code ExprList} with bracket type {@link BracketsTypes#round} and elements
 * {@code a}, {@code b}, {@code c}.</p>
 *
 * @see Expr
 * @see Symbol
 * @see BracketsType
 *
 * @author Sulphuris
 * @since 4/8/2025 8:24 PM
 */
public final class ExprList extends Expr implements Iterable<Expr> {
	
	private final BracketsType bracketsType;
	private final List<? extends Expr> elements;

	/**
	 * Creates an expression list with the given brackets type.
	 *
	 * @param bracketsType the bracket type
	 * @param elements the list elements
	 */
	private ExprList(BracketsType bracketsType, List<? extends Expr> elements) {
		this.bracketsType = bracketsType;
		this.elements = Collections.unmodifiableList(elements);
	}

	/**
	 * Returns the brackets type that groups this list.
	 *
	 * @return the brackets type
	 */
	public BracketsType getBracketsType() {
		return bracketsType;
	}

	/**
	 * Creates a located expression list.
	 *
	 * @param location the position in the source text
	 * @param bracketsType the bracket type
	 * @param elements the list elements with locations
	 * @return a located expression list
	 */
	public static LocatedExprList of(Location location, BracketsType bracketsType, List<LocatedExpr<? extends Expr>> elements) {
		ExprList list = of(bracketsType, elements.stream().map(LocatedExpr::getExpr).collect(Collectors.toList()));
		return new LocatedExprList(list, location, elements);
	}

	/**
	 * Creates an expression list with the given brackets type.
	 *
	 * @param bracketsType the brackets type
	 * @param elements the list elements
	 * @return a new expression list
	 * @throws NullPointerException if {@code bracketsType} or {@code elements} is {@code null}
	 */
	public static ExprList of(BracketsType bracketsType, List<? extends Expr> elements) {
		if (bracketsType == null) throw new NullPointerException("bracketsType is null");
		return new ExprList(bracketsType, elements);
	}

	/**
	 * Creates an expression list with the given brackets type.
	 *
	 * @param bracketsType the brackets type
	 * @param elements the list elements
	 * @return a new expression list
	 */
	public static ExprList of(BracketsType bracketsType, Expr... elements) {
		return of(bracketsType, Arrays.asList(elements));
	}

	/**
	 * Splits all elements of the list by the given separators and merges the
	 * results into a single {@link ExprList}.
	 *
	 * @param separateLines separator strings
	 * @return a collection containing the result of {@link #splitList(String...)} as its only element
	 */
	@Override
	public Collection<? extends Expr> split(String... separateLines) {
		return Collections.singleton(splitList(separateLines));
	}

	/**
	 * Splits each element of the list and merges the resulting parts into a new
	 * {@link ExprList} with the same bracket type.
	 *
	 * @param separateLines separator strings
	 * @return a new expression list obtained after splitting
	 */
	public ExprList splitList(String... separateLines) {
		return new ExprList(bracketsType,
				getElements().stream()
						.flatMap(e -> e.split(separateLines).stream())
						.collect(Collectors.toList()));
	}

	/**
	 * Returns an unmodifiable list of elements that this expression list was created with.
	 *
	 * @return the element list
	 */
	public List<? extends Expr> getElements() {
		return elements;
	}

	/**
	 * Returns the number of elements in the list.
	 *
	 * @return the number of elements
	 */
	public int size() {
		return elements.size();
	}

	/**
	 * Returns the element at the specified index.
	 *
	 * @param index the element index
	 * @return the element at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public Expr get(int index) {
		return elements.get(index);
	}

	/**
	 * Returns a direct string representation of the list.
	 *
	 * <p>Example: {@code (a b c)} for an expression list with three elements and {@link BracketsTypes#round} as its brackets type.</p>
	 *
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return getBracketsType().open() + elements.stream().map(String::valueOf).collect(Collectors.joining(" ")) + getBracketsType().close();
	}

	/**
	 * Compares this list with another object.
	 *
	 * <p>Two lists are equal if their bracket types and elements are equal.</p>
	 *
	 * @param o the object to compare with
	 * @return {@code true} if the objects are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ExprList exprs = (ExprList) o;
		return Objects.equals(bracketsType, exprs.bracketsType) && Objects.equals(elements, exprs.elements);
	}

	/**
	 * Returns the hash code of the list.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
        return 31 * (31 + bracketsType.hashCode()) + elements.hashCode();
	}

	/**
	 * Returns an iterator over the elements of the list.
	 *
	 * @return an iterator
	 */
	@SuppressWarnings("unchecked")
    @Override
	public Iterator<Expr> iterator() {
		return (Iterator<Expr>) elements.iterator();
	}
}
