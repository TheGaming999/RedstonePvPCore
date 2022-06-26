package me.redstonepvpcore.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CollectionUtils {

	private static boolean isNearPointer(final int number, final int divideBy) {
		double converted = ((double) number / (double) divideBy);
		String stringDecimal = String.valueOf(converted);
		int pointIndex = stringDecimal.indexOf('.');
		int startIndex = ++pointIndex;
		String decimalValue = stringDecimal.substring(startIndex, stringDecimal.length());
		int decimalFirst = Integer.parseInt(String.valueOf(decimalValue.charAt(0)));
		switch (decimalFirst) {
			case 0:
				return decimalValue.length() > 1 ? false : true;
			default:
				return true;
		}
	}

	/**
	 * 
	 * @param a collection size
	 * @param b elements per page
	 * @return %100 accurate final page number
	 */
	private static int fixPages(final int a, final int b) {
		int mathConverted = (int) Math.ceil((double) a / (double) b);
		return isNearPointer(a, b) ? mathConverted : mathConverted - 1;
	}

	/**
	 * 
	 * @param text      text to be inspected
	 * @param searchFor which text should be found
	 * @return true if (searchFor) is found within (text) or if (searchFor) is
	 *         empty, false otherwise.
	 */
	private static boolean containsIgnoreCase(String text, String searchFor) {
		final int length = searchFor.length();
		if (length == 0) return true;

		final char firstLo = Character.toLowerCase(searchFor.charAt(0));
		final char firstUp = Character.toUpperCase(searchFor.charAt(0));

		for (int i = text.length() - length; i >= 0; i--) {
			final char ch = text.charAt(i);
			if (ch != firstLo && ch != firstUp) continue;

			if (text.regionMatches(true, i, searchFor, 0, length)) return true;
		}

		return false;
	}

	private static String replaceIgnoreCase(String source, String target, String replacement) {
		StringBuilder sbSource = new StringBuilder(source);
		StringBuilder sbSourceLower = new StringBuilder(source.toLowerCase());
		String searchString = target.toLowerCase();

		int idx = 0;
		while ((idx = sbSourceLower.indexOf(searchString, idx)) != -1) {
			sbSource.replace(idx, idx + searchString.length(), replacement);
			sbSourceLower.replace(idx, idx + searchString.length(), replacement);
			idx += replacement.length();
		}
		sbSourceLower.setLength(0);
		sbSourceLower.trimToSize();
		sbSourceLower = null;

		return sbSource.toString();
	}

	public static int getAccurateFinalPage(final int elementsCount, final int elementsPerPage) {
		return fixPages(elementsCount, elementsPerPage);
	}

	/**
	 * <i>
	 * 
	 * @param index        the index from the loop that starts with 0 and ends with
	 *                     the size.
	 * @param entryPerPage How many elements in a page.
	 * @param page         The page it will be placed on.
	 * @return Correct position of the meant index in a paginated list.
	 */
	public static int paginateIndex(final int index, final int entryPerPage, final int page) {
		return page > 1 ? index + (entryPerPage * (page - 1)) : index;
	}

	public static class PaginatedList {

		private List<String> list;
		private List<String> entireList;
		private PaginatedList pl;
		private int currentPage;
		private int finalPage;
		private int elementsPerPage;

		public PaginatedList(List<String> list, int currentPage, int finalPage, List<String> entireList,
				int elementsPerPage) {
			this.list = list;
			this.currentPage = currentPage;
			this.finalPage = finalPage;
			this.entireList = entireList;
			this.elementsPerPage = elementsPerPage;
			this.pl = this;
		}

		/**
		 * <i>
		 * 
		 * @return the current page you are viewing
		 */
		public int getCurrentPage() {
			return pl.currentPage;
		}

		/**
		 * <i>
		 * 
		 * @return the final page which has at least one element
		 */
		public int getFinalPage() {
			return pl.finalPage;
		}

		/**
		 * <p>
		 * <i>the result is the same as when you initiate a new paginated list
		 * <p>
		 * the only benefit is flexibilty
		 * 
		 * @return move to the next page
		 */
		public PaginatedList next() {
			return pl = CollectionUtils.paginateListCollectable(pl.entireList, pl.elementsPerPage,
					pl.getCurrentPage() + 1);
		}

		/**
		 * <p>
		 * <i>the result is the same as when you initiate a new paginated list
		 * <p>
		 * the only benefit is flexibilty
		 * 
		 * @return go to the previous page
		 */
		public PaginatedList back() {
			return pl = CollectionUtils.paginateListCollectable(pl.entireList, pl.elementsPerPage,
					pl.getCurrentPage() - 1);
		}

		/**
		 * <p>
		 * <i>the result is the same as when you initiate a new paginated list
		 * <p>
		 * the only benefit is flexibilty
		 * 
		 * @param page the page that you will be moved to
		 * @return navigate to a specific page
		 */
		public PaginatedList navigate(int page) {
			return pl = CollectionUtils.paginateListCollectable(pl.entireList, pl.elementsPerPage, page);
		}

		/**
		 * 
		 * @return a linked list of current page elements | will return an empty list
		 *         when
		 *         <p>
		 *         there are not any elements on the current page | which means it will
		 *         never return null
		 */
		@Nonnull
		public List<String> collect() {
			return pl.list;
		}

		/**
		 * @deprecated
		 * @return all elements (no pagination)
		 */
		@Deprecated
		public List<String> collectAll() {
			return pl.entireList;
		}

		/**
		 * 
		 * @return how many elements will be shown on one page.
		 */
		public int getElementsPerPage() {
			return pl.elementsPerPage;
		}

		@Deprecated
		public boolean addElement(String element) {
			return pl.entireList.add(element);
		}

		@Deprecated
		public PaginatedList update() {
			return this.pl = CollectionUtils.paginateListCollectable(pl.entireList, pl.elementsPerPage,
					getCurrentPage());
		}

		public PaginatedList getPaginatedList() {
			return pl;
		}

	}

	public static class PaginatedCollection {

		private Collection<String> collection;
		private PaginatedCollection pc;
		private int currentPage;
		private int finalPage;

		public PaginatedCollection(Collection<String> collection, int currentPage, int finalPage) {
			this.collection = collection;
			this.currentPage = currentPage;
			this.finalPage = finalPage;
			this.pc = this;
		}

		/**
		 * 
		 * @return the current page you are viewing
		 */
		public int getCurrentPage() {
			return currentPage;
		}

		/**
		 * 
		 * @return the final page which has elements
		 */
		public int getFinalPage() {
			return finalPage;
		}

		/**
		 * 
		 * @return a collection of current page elements | will return an empty
		 *         collection when
		 *         <p>
		 *         there are not any elements on the current page | which means it will
		 *         never return null
		 */
		@Nonnull
		public Collection<String> collect() {
			return collection;
		}

		public PaginatedCollection getPaginatedCollection() {
			return pc;
		}

	}

	public static class ReplaceableList {

		private List<String> list;
		private ReplaceableList rl;

		public ReplaceableList(List<String> list) {
			this.list = list;
			this.rl = this;
		}

		@Deprecated
		public ReplaceableList replaceCollectable(List<String> stringList, String from, String to) {
			int i = 0;
			for (String stringLine : stringList) {
				i++;
				if (stringLine.equals(from)) {
					stringList.set(i, to);
				}
			}
			return rl;
		}

		public ReplaceableList replace(String from, String to) {
			int i = 0;
			for (String stringLine : list) {
				i++;
				if (stringLine.equals(from)) {
					list.set(i, to);
				}
			}
			return rl;
		}

		public Collection<String> collect() {
			return list;
		}

		public List<String> collectAsList() {
			return list;
		}

	}

	/**
	 * 
	 * @param stringCollection collection to columnize its elements
	 * @param columnsPerLine   how many string elements on one line
	 * @param seperator        what is between the string elements
	 * @return a new columnized linked list string collection
	 */
	public static Collection<String> columnizeCollection(final Collection<String> stringCollection,
			final int columnsPerLine, final String seperator) {
		int i = 0;
		List<String> newList = new LinkedList<>();
		StringBuilder builder = new StringBuilder("");
		String finishChar = ".";
		for (String stringLine : stringCollection) {
			i++;
			if (i == columnsPerLine + 1) {
				newList.add(builder.toString());
				builder = new StringBuilder("");
				i = 0;
			} else {
				if (i == stringCollection.size()) {
					break;
				}
				builder.append(stringLine).append(seperator);
			}
		}
		int size = newList.size();
		String lastLine = newList.get(size - 1);
		String lastLineReplaced = lastLine.substring(0, (lastLine.length()) - (seperator.length()));
		newList.set(size - 1, lastLineReplaced + finishChar);
		return newList;
	}

	/**
	 * 
	 * @param stringCollection collection to columnize its elements
	 * @param columnsPerLine   how many string elements on one line
	 * @param seperator        what is between the string elements
	 * @param finalChar        the last character on the last line, '.' (dot) by
	 *                         default
	 * @return a new columnized linked list string collection
	 */
	public static Collection<String> columnizeCollection(final Collection<String> stringCollection,
			final int columnsPerLine, final String seperator, final String finalChar) {
		int i = 0;
		List<String> newList = new LinkedList<>();
		StringBuilder builder = new StringBuilder("");
		String finishChar = finalChar == null ? "." : finalChar;
		for (String stringLine : stringCollection) {
			i++;
			if (i == columnsPerLine + 1) {
				newList.add(builder.toString());
				builder = new StringBuilder("");
				i = 0;
			} else {
				builder.append(stringLine).append(seperator);
			}
		}
		int size = newList.size();
		String lastLine = newList.get(size - 1);
		String lastLineReplaced = lastLine.substring(0, (lastLine.length()) - (seperator.length()));
		newList.set(size - 1, lastLineReplaced + finishChar);
		return newList;
	}

	/**
	 * 
	 * @param stringList     List to columnize its elements
	 * @param columnsPerLine how many string elements on one line
	 * @param seperator      what is between the string elements
	 * @param finalChar      the last character on the last line, '.' (dot) by
	 *                       default
	 * @return a new columnized linked list string collection
	 */
	public static List<String> columnizeList(final List<String> stringList, final int columnsPerLine,
			final String seperator, final String finalChar) {
		int i = 0;
		List<String> newList = new LinkedList<>();
		StringBuilder builder = new StringBuilder("");
		String finishChar = finalChar == null ? "." : finalChar;
		for (String stringLine : stringList) {
			i++;
			if (i == columnsPerLine + 1) {
				newList.add(builder.toString());
				builder = new StringBuilder("");
				i = 0;
			} else {
				if (i == stringList.size() + 1) {
					break;
				}
				builder.append(stringLine).append(seperator);
			}
		}
		int size = newList.size();
		String lastLine = newList.get(size - 1);
		String lastLineReplaced = lastLine.substring(0, (lastLine.length()) - (seperator.length()));
		newList.set(size - 1, lastLineReplaced + finishChar);
		return newList;
	}

	/**
	 * 
	 * @param stringCollection collection to be converted to string
	 * @param seperator        what's between the string elements
	 * @return Single Line String
	 */
	public static String collectionToString(final Collection<String> stringCollection, final String seperator) {
		String converted = "";
		String finishChar = ".";
		for (String element : stringCollection) {
			converted += element + seperator;
		}
		converted = converted.substring(converted.length() - seperator.length(), converted.length()) + finishChar;
		return converted;
	}

	public static String collectionToString(final Collection<String> stringCollection, final String seperator,
			final String finalChar) {
		String converted = "";
		String finishChar = finalChar;
		for (String element : stringCollection) {
			converted += element + seperator;
		}
		converted = converted.substring(converted.length() - seperator.length(), converted.length()) + finishChar;
		return converted;
	}

	/**
	 * Example
	 * <p>
	 * {@code List<String> stringList = Arrays.asList("IgNoRe CaSe", "second element");}
	 * <p>
	 * {@code hasIgnoreCase(stringList, "ignore Case"); // returns true}
	 * <p>
	 * {@code hasIgnoreCase(stringList, "case"); // returns false}
	 * 
	 * @param stringCollection collection of strings to search in
	 * @param searchFor        string to search for with case insensitivity
	 * @return true if {@code searchFor} was found, false otherwise.
	 */
	public static boolean hasIgnoreCase(Collection<String> stringCollection, String searchFor) {
		boolean found = false;
		for (String stringLine : stringCollection)
			if (stringLine.equalsIgnoreCase(searchFor)) found = true;
		return found;
	}

	/**
	 * Example
	 * <p>
	 * {@code List<String> stringList = Arrays.asList("IgNoRe CaSe", "second element");}
	 * <p>
	 * {@code String found = getContainsIgnoreCase(stringList, "ignore case");}
	 * <p>
	 * {@code // found = "IgNoRe CaSe"}
	 * 
	 * @param stringCollection collection of strings to search in
	 * @param searchFor        string to search for with case insensitivity
	 * @return string with the original case if found, or null if not.
	 */
	public static String getIgnoreCase(Collection<String> stringCollection, String searchFor) {
		String found = null;
		for (String stringLine : stringCollection)
			if (stringLine.equalsIgnoreCase(searchFor)) found = stringLine;
		return found;
	}

	/**
	 * Example
	 * <p>
	 * {@code List<String> stringList = Arrays.asList("IgNoRe CaSe", "second element");}
	 * <p>
	 * {@code containsIgnoreCase(stringList, "case"); // returns true}
	 * <p>
	 * {@code containsIgnoreCase(stringList, "ElEmen"); // returns true}
	 * <p>
	 * {@code containsIgnoreCase(stringList, "something"); // returns false}
	 * 
	 * @param stringCollection collection of strings to search in between its
	 *                         strings
	 * @param searchFor        string to search for with case insensitivity
	 * @return true if {@code searchFor} was found in between the strings in the
	 *         collection, false otherwise.
	 */
	public static boolean containsIgnoreCase(Collection<String> stringCollection, String searchFor) {
		boolean found = false;
		for (String stringLine : stringCollection)
			if (containsIgnoreCase(stringLine, searchFor)) found = true;
		return found;
	}

	/**
	 * Example
	 * <p>
	 * {@code List<String> stringList = Arrays.asList("IgNoRe CaSe", "second element");}
	 * <p>
	 * {@code String found = getContainsIgnoreCase(stringList, "case");}
	 * <p>
	 * {@code // found = "IgNoRe CaSe"}
	 * 
	 * @param stringCollection collection of strings to search in between its
	 *                         strings
	 * @param searchFor        string to search for with case insensitivity
	 * @return string with the original case if found, or null if not.
	 */
	public static String getContainsIgnoreCase(Collection<String> stringCollection, String searchFor) {
		String found = null;
		for (String stringLine : stringCollection)
			if (containsIgnoreCase(stringLine, searchFor)) found = stringLine;
		return found;
	}

	/**
	 * Example
	 * <p>
	 * {@code List<String> stringList = Arrays.asList("IgNoRe CaSe", "AnoTher IgnorE case", "Not related");}
	 * <p>
	 * {@code getContainsIgnoreCaseAll(stringList, "cASE"); // returns ["IgNoRe CaSe", "AnoTher IgnorE case"]}
	 * 
	 * @param stringCollection collection of strings to search in between its
	 *                         strings
	 * @param searchFor        string to search for with case insensitivity
	 * @return List of strings with their original case if any were found, or empty
	 *         list if none were found.
	 */
	public static List<String> getContainsIgnoreCaseAll(Collection<String> stringCollection, String searchFor) {
		List<String> found = Lists.newArrayList();
		for (String stringLine : stringCollection)
			if (containsIgnoreCase(stringLine, searchFor)) found.add(stringLine);
		return found;
	}

	/**
	 * 
	 * @param stringCollection to be inspected
	 * @param pattern          to be used
	 * @return elements that are matchable with the pattern matcher, false
	 *         otherwise.
	 */
	public static List<String> getPatternMatchables(Collection<String> stringCollection, Pattern pattern) {
		List<String> found = Lists.newArrayList();
		for (String stringLine : stringCollection) {
			Matcher matcher = pattern.matcher(stringLine);
			while (matcher.find())
				found.add(stringLine);
		}
		return found;
	}

	public static List<String> getPatternMatchablesAndContainsIgnoreCase(Collection<String> stringCollection,
			Pattern pattern, String searchFor) {
		List<String> found = Lists.newArrayList();
		if (!containsIgnoreCase(stringCollection, searchFor)) return found;
		for (String stringLine : stringCollection) {
			Matcher matcher = pattern.matcher(stringLine);
			while (matcher.find())
				found.add(stringLine);
		}
		return found;
	}

	public static boolean containsFromList(String string, Collection<String> searchFor) {
		boolean found = false;
		for (String stringLine : searchFor)
			if (string.contains(stringLine)) found = true;
		return found;
	}

	public static boolean containsIgnoreCaseFromList(String string, Collection<String> searchFor) {
		boolean found = false;
		for (String stringLine : searchFor)
			if (containsIgnoreCase(string, stringLine)) found = true;
		return found;
	}

	public static boolean containsIgnoreCaseCollection(Collection<String> stringCollection,
			Collection<String> searchFor) {
		Set<String> contained = new HashSet<>();
		for (String stringLine : searchFor)
			if (containsIgnoreCase(stringCollection, stringLine)) contained.add(stringLine);
		return stringCollection.containsAll(contained);
	}

	public static Set<String> getContainsIgnoreCaseCollection(Collection<String> stringCollection,
			Collection<String> searchFor) {
		Set<String> contained = new LinkedHashSet<>();
		for (String stringLine : searchFor)
			if (containsIgnoreCase(stringCollection, stringLine)) contained.add(stringLine);
		return contained;
	}

	public static List<String> getContainsIgnoreCaseList(Collection<String> stringCollection,
			Collection<String> searchFor) {
		List<String> contained = Lists.newArrayList();
		for (String stringLine : searchFor)
			if (containsIgnoreCase(stringCollection, stringLine)) contained.add(stringLine);
		return contained;
	}

	public static boolean hasIgnoreCaseCollection(Collection<String> stringCollection, Collection<String> searchFor) {
		Set<String> contained = new HashSet<>();
		for (String stringLine : searchFor)
			if (hasIgnoreCase(stringCollection, stringLine)) contained.add(stringLine);
		return stringCollection.containsAll(contained);
	}

	public static Set<String> getIgnoreCaseCollection(Collection<String> stringCollection,
			Collection<String> searchFor) {
		Set<String> contained = new LinkedHashSet<>();
		for (String stringLine : searchFor)
			if (hasIgnoreCase(stringCollection, stringLine)) contained.add(stringLine);
		return contained;
	}

	public static List<String> getIgnoreCaseList(Collection<String> stringCollection, Collection<String> searchFor) {
		List<String> contained = Lists.newArrayList();
		for (String stringLine : searchFor)
			if (hasIgnoreCase(stringCollection, stringLine)) contained.add(stringLine);
		return contained;
	}

	public static ReplaceableList replaceCollectable(List<String> stringList, String from, String to) {
		int i = 0;
		for (String stringLine : stringList) {
			if (stringLine.equals(from)) stringList.set(i, to);
			i++;
		}
		ReplaceableList replaceableList = new CollectionUtils.ReplaceableList(stringList);
		return replaceableList;
	}

	public static List<String> replace(List<String> stringList, String from, String to) {
		int i = 0;
		for (String stringLine : stringList) {
			if (stringLine.equals(from)) stringList.set(i, to);
			i++;
		}
		return stringList;
	}

	public static List<String> replaceWithin(List<String> stringList, String from, String to) {
		int i = 0;
		for (String stringLine : stringList) {
			if (stringLine.equals(from)) stringList.set(i, stringLine.replace(from, to));
			i++;
		}
		return stringList;
	}

	public static List<String> replaceIgnoreCase(List<String> stringList, String from, String to) {
		int i = 0;
		for (String stringLine : stringList) {
			if (stringLine.equalsIgnoreCase(from)) stringList.set(i, to);
			i++;
		}
		return stringList;
	}

	/**
	 * Example
	 * <p>
	 * {@code List<String> stringList = Arrays.asList("first line", "second line");}
	 * <p>
	 * {@code List<String> updated = replaceContainsIgnoreCase(stringList, "second", "Something new");}
	 * <p>
	 * {@code // Updated list elements: ("first line", "Something new")}
	 * 
	 * @param stringList list to search for string
	 * @param from       string to detect with case ignoring inside a string in a
	 *                   list
	 * @param to         new string to apply on the line that has the found string
	 * @return updated list
	 */
	public static List<String> replaceContainsIgnoreCase(List<String> stringList, String from, String to) {
		int i = 0;
		for (String stringLine : stringList) {
			if (containsIgnoreCase(stringLine, from)) stringList.set(i, to);
			i++;
		}
		return stringList;
	}

	public static List<String> replaceContainsIgnoreCaseWithin(List<String> stringList, String from, String to) {
		int i = 0;
		for (String stringLine : stringList) {
			if (containsIgnoreCase(stringLine, from)) stringList.set(i, replaceIgnoreCase(stringLine, from, to));
			i++;
		}
		return stringList;
	}

	public static List<String> replaceElementWithList(List<String> stringList, String target,
			List<String> replacement) {
		int originalIndex = stringList.indexOf(target);
		if (originalIndex != -1) {
			stringList.remove(target);
			stringList.addAll(originalIndex, replacement);
		}
		return stringList;
	}

	public static List<String> replaceElementContainsWithList(List<String> stringList, String searchFor,
			List<String> replacement) {
		String target = getContainsIgnoreCase(stringList, searchFor);
		if (target != null) {
			int originalIndex = stringList.indexOf(target);
			stringList.remove(target);
			stringList.addAll(originalIndex, replacement);
		}
		return stringList;
	}

	public static List<String> replaceElementIgnoreCaseWithList(List<String> stringList, String searchFor,
			List<String> replacement) {
		String target = getIgnoreCase(stringList, searchFor);
		if (target != null) {
			int originalIndex = stringList.indexOf(target);
			stringList.remove(target);
			stringList.addAll(originalIndex, replacement);
		}
		return stringList;
	}

	/**
	 * 
	 * @param collection  collection of string to paginate
	 * @param maxElements elements per page
	 * @param page        current page
	 * @return paginated string list
	 */
	public static List<String> paginateCollection(Collection<String> collection, final int maxElements,
			final int page) {
		int counter = 0;
		List<String> oldCollection = Lists.newArrayList(collection);
		List<String> newCollection = Lists.newArrayList();
		int size = oldCollection.size();
		for (int i = 0; i < size; i++) {
			counter++;
			if (counter >= maxElements) {
				break;
			}
			if (i + page < 0 || i + page >= size) {
				break;
			}
			newCollection.add(oldCollection.get(i + page));
		}
		return newCollection;
	}

	/**
	 * 
	 * @param stringList  list of strings
	 * @param maxElements elements per page
	 * @param page        current page
	 * @return paginated string list
	 */
	public static List<String> paginateList(List<String> stringList, final int maxElements, final int page) {
		int counter = 0;
		List<String> oldCollection = stringList;
		List<String> newCollection = Lists.newArrayList();
		int size = oldCollection.size();
		for (int i = 0; i < size; i++) {
			counter++;
			if (counter >= maxElements) {
				break;
			}
			int elementIndex = paginateIndex(counter, maxElements, page);
			if (elementIndex < 0 || elementIndex >= size) {
				break;
			}
			newCollection.add(oldCollection.get(elementIndex));
		}
		return newCollection;
	}

	/**
	 * 
	 * @param stringList  that you want to paginate
	 * @param maxElements elements per page
	 * @param page        page number
	 * @return A PaginatedList with the same insertion order that has the following
	 *         methods:
	 *         <i>
	 *         <p>
	 *         collect(), getCurrentPage(), getFinalPage(), and this method
	 *         parameters.
	 */
	public static PaginatedList paginateListCollectable(List<String> stringList, final int maxElements,
			final int page) {
		int counter = 0;
		List<String> oldCollection = stringList;
		List<String> newCollection = Lists.newArrayList();
		int size = oldCollection.size();
		for (int i = 0; i < size; i++) {
			if (counter >= maxElements) {
				break;
			}
			int elementIndex = paginateIndex(counter, maxElements, page);
			if (elementIndex < 0 || elementIndex >= size) {
				break;
			}
			newCollection.add(oldCollection.get(elementIndex));
			counter++;
		}
		return new PaginatedList(newCollection, page, fixPages(size, maxElements), oldCollection, maxElements);
	}

	/**
	 * 
	 * @param stringList  that you want to paginate
	 * @param maxElements elements per page
	 * @param page        page number
	 * @return A PaginatedCollection that has the following methods:
	 *         <i>
	 *         <p>
	 *         collect(), getCurrentPage(), getFinalPage() and this method
	 *         parameters.
	 *         <p>
	 *         difference between this and PaginatedList is the collection can't
	 *         have an
	 *         <p>
	 *         element twice. also it doesn't keep track of the insertion order
	 */
	public static PaginatedCollection paginateCollectionCollectable(Collection<String> stringList,
			final int maxElements, final int page) {
		int counter = 0;
		String[] oldCollection = stringList.toArray(new String[0]);
		Set<String> newCollection = Sets.newHashSet();
		int size = oldCollection.length;
		for (int i = 0; i < size; i++) {
			if (counter >= maxElements) {
				break;
			}
			int elementIndex = paginateIndex(counter, maxElements, page);
			if (elementIndex < 0 || elementIndex >= size) {
				break;
			}
			newCollection.add(oldCollection[elementIndex]);
			counter++;
		}
		return new PaginatedCollection(newCollection, page, fixPages(size, maxElements));
	}

	/**
	 * 
	 * @param stringList  that you want to paginate
	 * @param maxElements elements per page
	 * @param page        page number
	 * @return A PaginatedCollection with the same insertion order that has the
	 *         following methods:
	 *         <i>
	 *         <p>
	 *         collect(), getCurrentPage(), getFinalPage() and this method
	 *         parameters.
	 *         <p>
	 *         difference between this and PaginatedList is the collection can't
	 *         have an
	 *         <p>
	 *         element twice
	 */
	public static PaginatedCollection paginateLinkedCollectionCollectable(Collection<String> stringList,
			final int maxElements, final int page) {
		int counter = 0;
		String[] oldCollection = stringList.toArray(new String[0]);
		Set<String> newCollection = Sets.newLinkedHashSet();
		int size = oldCollection.length;
		for (int i = 0; i < size; i++) {
			if (counter >= maxElements) {
				break;
			}
			int elementIndex = paginateIndex(counter, maxElements, page);
			if (elementIndex < 0 || elementIndex >= size) {
				break;
			}
			newCollection.add(oldCollection[elementIndex]);
			counter++;
		}
		return new PaginatedCollection(newCollection, page, fixPages(size, maxElements));
	}

	public static List<String> stringToList(String string, String seperator) {
		return Lists.newArrayList(string.split(seperator));
	}

	public static Collection<String> stringToCollection(String string, String seperator) {
		List<String> newList = Lists.newArrayList(string.split(seperator));
		return Collections.unmodifiableList(newList);
	}

	public static List<String> separateIntoChars(List<String> stringList, int separateFactor) {
		List<String> newList = Lists.newArrayList();
		stringList.forEach(stringLine -> {
			int counter = -1;
			for (char character : stringLine.toCharArray()) {
				counter++;
				if (counter == separateFactor)
					counter = -1;
				else
					newList.add(String.valueOf(character));
			}
		});
		return newList;
	}

}
