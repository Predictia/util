package es.predictia.util;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Conversions {

	private Conversions(){}
	
	/**
	 * Applies convertion {@link Function} to source elements, catches
	 * exceptions and valid destination elements are returned
	 * 
	 * @param <S>
	 * @param <D>
	 * @param source
	 * @param transformFunction
	 * @return
	 */
	public static <S, D> List<D> tryParseList(List<S> source, final Function<S, D> transformFunction){
		if(source != null){
			return Lists.newArrayList(Iterables.filter(Iterables.transform(source, new Function<S, D>() {
				@Override public D apply(S arg0) {
					try{
						return transformFunction.apply(arg0);
					}catch (Throwable e) {
						return null;
					}
				}
			}), Predicates.notNull()));
		}else{
			return Collections.emptyList();
		}
	}
	
}
