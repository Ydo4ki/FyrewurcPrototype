package org.fw.ast;

/**
 * @author Sulphuris
 * @since 4/6/2025 8:43 PM
 */
public record BracketsType(char open, char close) {
    @Override
    public String toString() {
        return open + "" + close;
    }
}
