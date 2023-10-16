package me.redstonepvpcore.utils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class IntParser {

	public static final Set<Character> NUMBER_CHARS = QSets.newHashSet('0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9');
	private static final int INT_MAX_LENGTH = 10;
	private static final int ZERO_CODE = 48;
	private static final int NINE_CODE = 57;
	private static final int RADIX = 10;
	private static final char MINUS = '-';
	private static final char PLUS = '+';

	public static class QSets {

		@SafeVarargs
		public static <T> HashSet<T> newHashSet(T... elements) {
			HashSet<T> set = new HashSet<>();
			for (T t : elements) set.add(t);
			return set;
		}

		@SafeVarargs
		public static <T> LinkedHashSet<T> newLinkedHashSet(T... elements) {
			LinkedHashSet<T> set = new LinkedHashSet<>();
			for (T t : elements) set.add(t);
			return set;
		}

	}

	/**
	 * Checks whether string can be recognized as an int or not
	 * 
	 * @param string to check
	 * @return true if string can be converted to int, false otherwise
	 */
	public static boolean isInt(String string) {
		char[] characters = string.toCharArray();
		int startIndex = 0;
		int curLength = 0;
		char firstChar = characters[0];
		if (firstChar == MINUS || firstChar == PLUS) startIndex = 1;
		for (int i = startIndex; i < characters.length; i++) {
			char current = characters[i];
			if (current < ZERO_CODE || current > NINE_CODE) return false;
			if (current != ZERO_CODE) curLength++;
			if (curLength > INT_MAX_LENGTH)
				return false;
			else if (curLength == INT_MAX_LENGTH && Long.parseLong(string) > Integer.MAX_VALUE) return false;
		}
		return true;
	}

	/**
	 * Converts string to int
	 * 
	 * @param string to parse as integer
	 * @return string as integer if it can be perceived as integer, int of 0 is
	 *         returned otherwise
	 */
	public static int asInt(String string) {
		return asInt(string, 0);
	}

	/**
	 * Converts string to int
	 * 
	 * @param string to parse as integer
	 * @param error  integer to return if string wasn't an integer
	 * @return string as integer if it can be perceived as integer, error is
	 *         returned otherwise
	 */
	public static int asInt(String string, Consumer<String> error) {
		return asInt(string, error, 0);
	}

	/**
	 * Converts string to int
	 * 
	 * @param string      to parse as integer
	 * @param error       action to perform on string if parsing failed
	 * @param errorResult integer to return if string wasn't an integer
	 * @return string as integer if it can be perceived as integer, errorResult is
	 *         returned otherwise
	 */
	public static int asInt(String string, Consumer<String> error, int errorResult) {
		char[] characters = string.toCharArray();
		int startIndex = 0;
		int curLength = 0;
		char firstChar = characters[0];
		int result = 0;
		boolean negative = false;
		if (firstChar == MINUS) {
			startIndex = 1;
			negative = true;
		}
		if (firstChar == PLUS) startIndex = 1;
		for (int i = startIndex; i < characters.length; i++) {
			char current = characters[i];
			if (current < ZERO_CODE || current > NINE_CODE) {
				result = errorResult;
				error.accept(string);
				break;
			}
			if (current != ZERO_CODE) curLength++;
			if (curLength > INT_MAX_LENGTH) {
				result = errorResult;
				error.accept(string);
				break;
			} else if (curLength == INT_MAX_LENGTH && Long.parseLong(string) > Integer.MAX_VALUE) {
				result = errorResult;
				error.accept(string);
				break;
			}
			result *= RADIX;
			result += Character.digit(current, RADIX);
		}
		return negative ? -result : result;
	}

	/**
	 * Converts string to int
	 * 
	 * @param string to parse as integer
	 * @param error  integer to return if string wasn't an integer
	 * @return string as integer if it can be perceived as integer, error is
	 *         returned otherwise
	 */
	public static int asInt(String string, int error) {
		char[] characters = string.toCharArray();
		int startIndex = 0;
		int curLength = 0;
		char firstChar = characters[0];
		int result = 0;
		boolean negative = false;
		if (firstChar == MINUS) {
			startIndex = 1;
			negative = true;
		}
		if (firstChar == PLUS) startIndex = 1;
		for (int i = startIndex; i < characters.length; i++) {
			char current = characters[i];
			if (current < ZERO_CODE || current > NINE_CODE) {
				result = error;
				break;
			}
			if (current != ZERO_CODE) curLength++;
			if (curLength > INT_MAX_LENGTH) {
				result = error;
				break;
			} else if (curLength == INT_MAX_LENGTH && Long.parseLong(string) > Integer.MAX_VALUE) {
				result = error;
				break;
			}
			result *= RADIX;
			result += Character.digit(current, RADIX);
		}
		return negative ? -result : result;
	}

	public static boolean isParsableInt(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static int parseInt(String string, int defaultInt) {
		int i = defaultInt;
		try {
			i = Integer.parseInt(string);
		} catch (NumberFormatException ex) {
			return defaultInt;
		}
		return i;
	}

	/**
	 * Get int in a string
	 * 
	 * @param string to extract an int from
	 * @return extracted int
	 */
	public static int readInt(String string) {
		if (string == null || string.isEmpty()) return 0;
		char[] characters = string.toCharArray();
		String numberizer = "";
		boolean numberAdded = false;
		if (characters[0] == MINUS) numberizer += "-0";
		for (char character : characters) {
			if (NUMBER_CHARS.contains(character)) {
				numberizer += character;
				numberAdded = true;
			}
		}
		return numberAdded ? Integer.parseInt(numberizer) : 0;
	}

	public static long readLong(String string) {
		if (string == null || string.isEmpty()) return 0l;
		char[] characters = string.toCharArray();
		String numberizer = "";
		boolean numberAdded = false;
		if (characters[0] == MINUS) numberizer += "-0";
		for (char character : characters) {
			if (NUMBER_CHARS.contains(character)) {
				numberizer += character;
				numberAdded = true;
			}
		}
		return numberAdded ? Long.parseLong(numberizer) : 0l;
	}

	public static double readDouble(String string) {
		if (string == null || string.isEmpty()) return 0.0d;
		char[] characters = string.toCharArray();
		String numberizer = "";
		if (characters[0] == MINUS) numberizer += "-0";
		boolean decimalIgnore = false;
		boolean numberAdded = false;
		for (int i = 0; i < characters.length; i++) {
			char character = characters[i];
			if (NUMBER_CHARS.contains(character)) {
				numberizer += character;
				numberAdded = true;
			}
			if (!decimalIgnore && character == '.' && numberAdded) {
				numberizer += ".";
				decimalIgnore = true;
			}
		}
		return numberAdded ? Double.parseDouble(numberizer) : 0.0d;
	}

}
