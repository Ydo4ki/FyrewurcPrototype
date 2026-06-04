package org.fw.core.ast;

import java.util.Objects;

/**
 * @author Sulphuris
 * @since 4/6/2025 8:43 PM
 */
public final class BracketsType {

    private final char open;
    private final char close;

    public BracketsType(char open, char close) {
        this.open = open;
        this.close = close;
    }

    public char open() {
        return open;
    }

    public char close() {
        return close;
    }

    @Override
    public String toString() {
        return open + "" + close;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BracketsType)) return false;
        BracketsType that = (BracketsType) o;
        return open == that.open && close == that.close;
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, close);
    }
}