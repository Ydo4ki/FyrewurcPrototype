package org.fw.ast;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sulphuris
 * @since 4/8/2025 8:24 PM
 */
public final class ExprList extends Expr implements Iterable<Expr> {
	
	private final BracketsType bracketsType;
	private final List<? extends Expr> elements;
	
	ExprList(Location location, BracketsType bracketsType, List<? extends Expr> elements) {
		super(location);
		this.bracketsType = bracketsType;
		this.elements = Collections.unmodifiableList(elements);
	}
	
	public BracketsType getBracketsType() {
		return bracketsType;
	}
	
	public static ExprList of(Location location, BracketsType bracketsType, List<? extends Expr> elements) {
		if (bracketsType == null) throw new NullPointerException("bracketsType is null");
		return new ExprList(location, bracketsType, elements);
	}
	
	public static ExprList of(BracketsType bracketsType, List<? extends Expr> elements) {
		return of(Location.unknown(null, ""), bracketsType, elements);
	}
	public static ExprList of(BracketsType bracketsType, Expr... elements) {
		return of(bracketsType, List.of(elements));
	}
	
	@Override
	public Collection<? extends Expr> split(String... separateLines) {
		return Collections.singleton(splitList(separateLines));
	}
	
	public ExprList splitList(String... separateLines) {
		return new ExprList(getLocation(), bracketsType,
				getElements().stream()
						.flatMap(e -> e.split(separateLines).stream())
						.collect(Collectors.toList()));
	}
	
	public List<? extends Expr> getElements() {
		return elements;
	}
	
	public int size() {
		return elements.size();
	}
	
	public Expr get(int index) {
		return elements.get(index);
	}
	
	@Override
	public String toString() {
		return getBracketsType().open() + elements.stream().map(Expr::toString).collect(Collectors.joining(" ")) + getBracketsType().close();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ExprList exprList = (ExprList) o;
		return Objects.equals(elements, exprList.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getBracketsType(), elements);
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public Iterator<Expr> iterator() {
		return (Iterator<Expr>) elements.iterator();
	}
	
	public Expr[] cdr() {
		return getElements().stream().skip(1).toArray(Expr[]::new);
	}
}
