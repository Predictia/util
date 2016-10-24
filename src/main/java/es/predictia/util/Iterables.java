package es.predictia.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class Iterables {

	private Iterables(){}
	
	/** Divides an iterable in a continuous fashion: the last element of a slice is also included in the next slice
	 * @param <T>
	 * @param iterable
	 * @param size
	 * @return
	 */
	public static <T> Iterable<List<T>> partitionContinous(Iterable<T> iterable, int size){
		List<T> oldList = Lists.newArrayList(iterable);
		List<T> newList = new ArrayList<T>();
		for(int i=0; i<oldList.size(); i++){
			newList.add(oldList.get(i));
			if((newList.size() % size) == 0) newList.add(oldList.get(i));
		}
		return com.google.common.collect.Iterables.filter(
			com.google.common.collect.Iterables.partition(newList, size), 
			new Predicate<List<T>>() {
				public boolean apply(List<T> arg0) {
					return arg0.size() != 1;
				}
			}
		);
	}
	
	/** Null-safe creation of a list from an iterable
	 * @param <T>
	 * @param inCollection
	 * @return
	 */
	public static <T> List<T> newList(Iterable<T> inCollection){
		if(inCollection != null){
			return new ArrayList<T>(Lists.newArrayList(inCollection));
		}else{
			return new ArrayList<T>();
		}
	}
	
	@SafeVarargs
	public static <T> List<T> newList(Class<T> clazz, T... els){
		if(els != null){
			return Lists.newArrayList(els);
		}else{
			return new ArrayList<T>();
		}
	}
	
	/** Null-safe isEmpty checker
	 * @param it
	 * @return
	 */
	public static boolean isEmpty(Iterable<?> it){
    	if(it == null) return true;
    	return !it.iterator().hasNext();
    }
	
	/**
	 * @param <T>
	 * @param it
	 * @return Last element of iterable of null if no elements present 
	 */
	public static <T> T lastOf(Iterable<T> it){
		T last = null;
		Iterator<T> iterator = it.iterator();
		while(iterator.hasNext()){
			last = iterator.next();
		}
		return last;
	}
	
}
