package me.redstonepvpcore.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;


public class PagedArrayList<T> extends ArrayList<T> implements Iterable<T> {

	private List<T> completeList = Lists.newArrayList();
	private List<List<T>> pages = Lists.newArrayList();
	private int currentPage;
	private int lastPage;
	private int elementsPerPage;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8942996357846540848L;

	class PagedIterator implements Iterator<T> {

        private int index = 0;

        public boolean hasNext() {
            return index < size();
        }

        public T next() {
            return get(index++);
        }

        public void remove() {
            throw new UnsupportedOperationException("not supported yet");
        }
        
   }
	
	public final static int paginateIndex(final int index, final int entryPerPage, final int page) {	
		return page > 1 ? index + (entryPerPage*(page-1)) : index;
	}

	/**
	 * Constructs an array list with a capacity of 10 for each page
	 */
	public PagedArrayList() {
		elementsPerPage = 10;
		lastPage = 1;
		pages.add(new ArrayList<T>());
	}

	/**
	 * Constructs an array list from the specified list with a capacity of 10 for each page
	 */
	public PagedArrayList(List<T> list) {
		List<T> pageList = new ArrayList<>();
		List<T> virtualList = new ArrayList<>();
		pages = new ArrayList<>();
		completeList = list;
		elementsPerPage = 10;
		lastPage = 1;
		int elementCounter = 0;	
		int size = completeList.size();
		int finalPageElement = elementsPerPage > size-1 ? size : elementsPerPage;
		pages.add(completeList.subList(0, finalPageElement));
		for(int i = 0; i < size; i++) {
			if(elementCounter == elementsPerPage) {
				finalPageElement = i+elementCounter > size-1 ? size : i+elementCounter;
				virtualList = completeList.subList(i, finalPageElement);
				pageList = virtualList;
				pages.add(pageList);
				elementCounter = 0;
				lastPage++;
			}
			elementCounter++;
		}
		currentPage = 1;
	}

	/**
	 * Constructs an array list from the specified collection with a capacity of 10 for each page
	 */
	public PagedArrayList(Collection<T> collection) {
		List<T> pageList = new ArrayList<>();
		List<T> virtualList = new ArrayList<>();
		pages = new ArrayList<>();
		completeList = new ArrayList<>(collection);
		elementsPerPage = 10;
		lastPage = 1;
		int elementCounter = 0;	
		int size = completeList.size();
		int finalPageElement = elementsPerPage > size-1 ? size : elementsPerPage;
		pages.add(completeList.subList(0, finalPageElement));
		for(int i = 0; i < size; i++) {
			if(elementCounter == elementsPerPage) {
				finalPageElement = i+elementCounter > size-1 ? size : i+elementCounter;
				virtualList = completeList.subList(i, finalPageElement);
				pageList = virtualList;
				pages.add(pageList);
				elementCounter = 0;
				lastPage++;
			}
			elementCounter++;
		}
		currentPage = 1;
	}

	/**
	 * Constructs an array list from the specified list with the specified maxElementsPerPage as a capacity for each page
	 */
	public PagedArrayList(int maxElementsPerPage, List<T> list) {
		List<T> pageList = new ArrayList<>();
		List<T> virtualList = new ArrayList<>();
		pages = new ArrayList<>();
		completeList = list;
		elementsPerPage = maxElementsPerPage;
		lastPage = 1;
		int elementCounter = 0;	
		int size = completeList.size();
		int finalPageElement = elementsPerPage > size-1 ? size : elementsPerPage;
		pages.add(completeList.subList(0, finalPageElement));
		for(int i = 0; i < size; i++) {
			if(elementCounter == elementsPerPage) {
				finalPageElement = i+elementCounter > size-1 ? size : i+elementCounter;
				virtualList = completeList.subList(i, finalPageElement);
				pageList = virtualList;
				pages.add(pageList);
				elementCounter = 0;
				lastPage++;
			}
			elementCounter++;
		}
		currentPage = 1;
	}
	
	/**
	 * Constructs an array list from the specified collection with the specified maxElementsPerPage as a capacity for each page
	 */
	public PagedArrayList(int maxElementsPerPage, Collection<T> collection) {
		List<T> pageList = new ArrayList<>();
		List<T> virtualList = new ArrayList<>();
		pages = new ArrayList<>();
		completeList = new ArrayList<T>(collection);
		elementsPerPage = maxElementsPerPage;
		lastPage = 1;
		int elementCounter = 0;	
		int size = completeList.size();
		int finalPageElement = elementsPerPage > size-1 ? size : elementsPerPage;
		pages.add(completeList.subList(0, finalPageElement));
		for(int i = 0; i < size; i++) {
			if(elementCounter == elementsPerPage) {
				finalPageElement = i+elementCounter > size-1 ? size : i+elementCounter;
				virtualList = completeList.subList(i, finalPageElement);
				pageList = virtualList;
				pages.add(pageList);
				elementCounter = 0;
				lastPage++;
			}
			elementCounter++;
		}
		currentPage = 1;
	}

	@SafeVarargs
	public PagedArrayList(T... elements) {
		clone(10, elements);
	}

	@SafeVarargs
	public PagedArrayList(int maxElementsPerPage, T... elements) {
		List<T> pageList = new ArrayList<>();
		List<T> virtualList = new ArrayList<>();
		pages = new ArrayList<>();
		completeList = Lists.newArrayList(elements);
		elementsPerPage = maxElementsPerPage;
		lastPage = 1;
		int elementCounter = 0;	
		int size = completeList.size();
		int finalPageElement = elementsPerPage > size-1 ? size : elementsPerPage;
		pages.add(completeList.subList(0, finalPageElement));
		for(int i = 0; i < size; i++) {
			if(elementCounter == elementsPerPage) {
				finalPageElement = i+elementCounter > size-1 ? size : i+elementCounter;
				virtualList = completeList.subList(i, finalPageElement);
				pageList = virtualList;
				pages.add(pageList);
				elementCounter = 0;
				lastPage++;
			}
			elementCounter++;
		}
		currentPage = 1;
	}

	@SuppressWarnings("unchecked")
	private PagedArrayList<T> clone(int maxElementsPerPage, T... elements) {
		return new PagedArrayList<T>(maxElementsPerPage, elements);
	}

	public PagedArrayList<T> clone(int maxElementsPerPage) {
		return new PagedArrayList<T>(maxElementsPerPage, this.completeList);
	}

	/**
	 * 
	 * @return an array list that contains all elements that were added to pages
	 */
	public List<T> getArrayList() {
		return completeList;
	}

	/**
	 * 
	 * @param page switches current page to the specified page
	 * @return this PagedArrayList
	 */
	public PagedArrayList<T> navigate(int page) {
		if(page <= lastPage && page > 0) currentPage = page;
		return this;
	}

	/**
	 * 
	 * @param switches current page to the next page
	 * @return this PagedArrayList
	 */
	public PagedArrayList<T> next() {
		if(currentPage <= lastPage) currentPage += 1;
		return this;
	}

	/**
	 * 
	 * @param switches current page to the previous page
	 * @return this PagedArrayList
	 */
	public PagedArrayList<T> back() {
		if(currentPage > 1) currentPage -= 1;
		return this;
	}

	/**
	 * 
	 * @return all pages with their elements
	 */
	public List<List<T>> getPages() {
		return pages;
	}

	/**
	 * @param page the page to retrieve elements from <i>(Starting from 1 to lastPage)</i>
	 * @return all elements in a page
	 */
	public List<T> getElements(int page) {
		return pages.get(page-1);
	}

	/**
	 * 
	 * @return elements of the current page
	 */
	public List<T> getElements() {
		return pages.get(currentPage-1);
	}

	/**
	 * Gets the element with the specified index from current page
	 * @return the element at the specified position in the current page after navigation
	 */
	@Override
	public T get(int index) {
		return pages.get(currentPage-1).get(index);
	}

	/**
	 * Sets the element to the specified index in the current page
	 * @return the element at the specified position in the current page after navigation
	 */
	public T set(int index, T element) {
		return pages.get(currentPage-1).set(index, element);
	}

	public T remove(int index) {
		return pages.get(currentPage-1).remove(index);
	}

	public boolean contains(Object element) {
		return pages.get(currentPage-1).indexOf(element) >= 0;
	}

	public int indexOf(Object element) {
		return pages.get(currentPage-1).indexOf(element);
	}

	public List<T> findFirst(T element) {
		for(List<T> page : pages) 
			if(page.contains(element)) return page;
		return null;
	}

	public List<T> findLast(T element) {
		List<T> found = null;
		for(List<T> page : pages) 
			if(page.contains(element)) found = page;
		return found;
	}

	/**
	 * Appends the specified element to the end of the latest page with an unoccupied place.
	 */
	public synchronized boolean add(T element) {
		int elementsCount = pages.get(lastPage-1).size();
		if(elementsCount > elementsPerPage-1) {
			pages.add(new ArrayList<>());
			lastPage += 1;
		}
		completeList.add(element);
		return pages.get(lastPage-1).add(element);
	}

	/**
	 * Appends the specified elements to the end of the latest page with an unoccupied place.
	 */
	public synchronized void add(@SuppressWarnings("unchecked") T... element) {
		for(T elem : element) 
			add(elem);
	}

	/**
	 * Appends the specified element to the end of the last page with an unoccupied place.
	 * @param element element to be appended to this list
	 * @param navigate whether to navigate to the next page when necessary
	 */
	public boolean add(T element, boolean navigate) {
		if(!navigate) return add(element);
		int elementsCount = pages.get(lastPage-1).size();
		if(elementsCount > elementsPerPage) {
			pages.add(new ArrayList<>());
			lastPage++;
		}
		navigate(lastPage-1);
		completeList.add(element);
		return pages.get(lastPage-1).add(element);
	}

	public void forEach(Consumer<? super T> consumer) {
		for(T t : pages.get(currentPage-1)) 
			consumer.accept(t);
	}

	public int getMaxElementsPerPage() {
		return elementsPerPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}


}
