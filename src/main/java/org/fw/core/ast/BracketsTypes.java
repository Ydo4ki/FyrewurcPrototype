package org.fw.core.ast;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @since 5/31/2025 3:23 PM
 */
public final class BracketsTypes extends ArrayList<BracketsType> {
	
	public BracketsTypes(BracketsType... types) {
		Collections.addAll(this, types);
	}
	
	public static final BracketsType round = new BracketsType('(',')');
	public static final BracketsType square = new BracketsType('[',']');
	public static final BracketsType braces = new BracketsType('{','}');
	
	public static final BracketsTypes bracketsTypes = new BracketsTypes(round, square, braces);
	
	public BracketsType byOpen(char ch) {
		for (BracketsType value : this) {
			if (ch == value.open()) return value;
		}
		return null;
	}
	
	public BracketsType byClose(char ch) {
		for (BracketsType value : this) {
			if (ch == value.close()) return value;
		}
		return null;
	}

	public BracketsType byChar(char ch) {
		for (BracketsType value : this) {
			if (ch == value.open() || ch == value.close()) return value;
		}
		return null;
	}
	
	public boolean isBracket(char ch) {
		for (BracketsType value : this) {
			if (ch == value.close() || ch == value.open()) return true;
		}
		return false;
	}
}
