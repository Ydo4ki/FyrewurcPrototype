package org.fw.core.ast;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.core.commons.ValAdapter;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 4/7/2025 10:33 PM
 * @author Sulphuris
 */
public final class Symbol extends Expr implements ValAdapter, Constable {
	private final String value;
	
	public Symbol(String value) {
		this.value = value;
	}
	
	public static Symbol of(String value) {
		return new Symbol(value);
	}
	public static LocatedSymbol of(Location location, String value) {
		return Symbol.of(value).located(location);
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public Collection<Symbol> split(String... separateLines) {
		String line = value;
		
		int lineLength = line.length();
		int start = 0;
		int current = 0;
		
		List<String> validSeparators = Arrays.stream(separateLines).filter(sep -> !sep.isEmpty()).collect(Collectors.toList());
		
		if (validSeparators.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<Symbol> result = new ArrayList<>();
		while (current <= lineLength) {
			Symbol foundSep = null;
			int maxLen = 0;
			
			for (String sep : validSeparators) {
				if (line.startsWith(sep, current)) {
					if (sep.length() > maxLen) {
						maxLen = sep.length();
						foundSep = Symbol.of(sep);
					}
				}
			}
			
			if (foundSep != null) {
				if (current > start) {
					result.add(Symbol.of(line.substring(start, current)));
				}
				result.add(foundSep);
				start = current + foundSep.value.length();
				current = start;
			} else {
				current++;
			}
		}
		
		if (start < lineLength) {
			result.add(Symbol.of(line.substring(start)));
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol) o;
		return Objects.equals(value, symbol.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	public LocatedSymbol located(Location location) {
		return new LocatedSymbol(this, location);
	}

	@Override
	public Val asVal() {
		return FW.symbol(value);
	}

	@Override
	public Optional<? extends ConstantDesc> describeConstable() {
		return Optional.of(value);
	}
}
