package org.fw.core.ast;

import java.util.*;
import java.util.stream.Collectors;

public final class LocatedSymbol extends LocatedExpr<Symbol> {
    LocatedSymbol(Symbol expr, Location location) {
        super(expr, location);
    }

    @Override
    public Collection<? extends LocatedExpr<?>> split(String... separateLines) {
        String line = getExpr().getValue();

        int lineLength = line.length();
        int start = 0;
        int current = 0;

        List<String> validSeparators = Arrays.stream(separateLines).filter(sep -> !sep.isEmpty()).collect(Collectors.toList());

        if (validSeparators.isEmpty()) {
            return Collections.emptyList();
        }

        List<LocatedExpr<Symbol>> result = new ArrayList<>();
        while (current <= lineLength) {
            LocatedExpr<Symbol> foundSep = null;
            int maxLen = 0;

            for (String sep : validSeparators) {
                if (line.startsWith(sep, current)) {
                    if (sep.length() > maxLen) {
                        maxLen = sep.length();
                        foundSep = Symbol.of(new Location(
                                getLocation().getStartPos() + start,
                                getLocation().getStartPos() + current,
                                getLocation().getStartLine(),
                                getLocation().getEndLine(),
                                getLocation().getSourceFile()
                        ), sep);
                    }
                }
            }

            if (foundSep != null) {
                if (current > start) {
                    result.add(Symbol.of(new Location(
                                    getLocation().getStartPos() + start,
                                    getLocation().getStartPos() + current,
                                    getLocation().getStartLine(),
                                    getLocation().getEndLine(),
                                    getLocation().getSourceFile()
                            ), line.substring(start, current))
                    );
                }
                result.add(foundSep);
                start = current + foundSep.getExpr().getValue().length();
                current = start;
            } else {
                current++;
            }
        }

        if (start < lineLength) {
            result.add(Symbol.of(new Location(
                    getLocation().getStartPos() + start,
                    getLocation().getStartPos() + (line.length() - start),
                    getLocation().getStartLine(),
                    getLocation().getEndLine(),
                    getLocation().getSourceFile()
            ), line.substring(start)));
        }

        return result;
    }

    public String getValue() {
        return getExpr().getValue();
    }
}
