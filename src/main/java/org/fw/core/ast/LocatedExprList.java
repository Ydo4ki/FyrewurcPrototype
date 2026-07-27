package org.fw.core.ast;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LocatedExprList extends LocatedExpr<ExprList> implements Iterable<LocatedExpr<? extends Expr>> {
    private final List<LocatedExpr<? extends Expr>> elements;

    LocatedExprList(ExprList expr, Location location, List<LocatedExpr<? extends Expr>> elements) {
        super(expr, location);
        this.elements = elements;
    }

    @Override
    public Collection<? extends LocatedExpr<?>> split(String... separateLines) {
        return Collections.singleton(splitList(separateLines));
    }

    private LocatedExpr<ExprList> splitList(String... separateLines) {
        return ExprList.of(getLocation(), getBracketsType(),
                getElements().stream()
                        .flatMap(e -> e.split(separateLines).stream())
                        .collect(Collectors.toList()));
    }

    public BracketsType getBracketsType() {
        return getExpr().getBracketsType();
    }

    public List<? extends LocatedExpr<? extends Expr>> getElements() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    public LocatedExpr<? extends Expr> get(int index) {
        return elements.get(index);
    }

    @Override
    public Iterator<LocatedExpr<? extends Expr>> iterator() {
        return elements.iterator();
    }
}
