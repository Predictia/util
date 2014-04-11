package es.predictia.util.comparator;

import java.text.Collator;
import java.util.Locale;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class Orderings {

	private Orderings(){}
	
	public static Ordering<String> getNaturalOrdering(Locale locale){
		return getNaturalOrdering(locale, STRING_BYPASS_FUNCTION);
	}
	
	public static <T> Ordering<T> getNaturalOrdering(Locale locale, final Function<T, String> captionFunction){
		Collator c = Collator.getInstance(locale);
		c.setStrength(Collator.TERTIARY);
		return getNaturalOrdering(new AlphanumComparator(c), captionFunction);
	}
	
	public static Ordering<String> getNaturalOrdering(){
		return getNaturalOrdering(ALPHANUM_COMPARATOR, STRING_BYPASS_FUNCTION);
	}
	
	public static <T> Ordering<T> getNaturalOrdering(final Function<T, String> captionFunction){
		return getNaturalOrdering(ALPHANUM_COMPARATOR, captionFunction);
	}
	
	private static <T> Ordering<T> getNaturalOrdering(final AlphanumComparator alphanumComparator, final Function<T, String> captionFunction){
		return new Ordering<T>() {
			@Override
			public int compare(T arg0, T arg1) {
				String s0 = captionFunction.apply(arg0);
				String s1 = captionFunction.apply(arg1);
				return alphanumComparator.compare(
					(s0 != null) ? s0 : "", 
					(s1 != null) ? s1 : ""
				);
			}
		};
	}
	
	public static final Ordering<Object> NUMBERS_ORDERING = new Ordering<Object>() {
		@Override
		public int compare(Object arg0, Object arg1) {
			if((arg0 instanceof Number) && (arg1 instanceof Number)){
				return NUMBER_COMPARATOR.compare((Number)arg0, (Number)arg1);
			}else{
				return 0;
			}
		}
	};
	
	public static final Ordering<Object> NULLS_FIRST_ORDERING = new Ordering<Object>() {
		@Override
		public int compare(Object arg0, Object arg1) {
			if(arg0 == null){
				return (arg1 == null) ? 0 : -1;
			}else if(arg1 == null){
				return (arg0 == null) ? 0 : +1;
			}else{
				return 0;
			}
		}
	};
	
	private static final Function<String, String> STRING_BYPASS_FUNCTION = new Function<String, String>() {
		@Override
		public String apply(String arg0) {
			return arg0;
		}
	};
	
	private static final AlphanumComparator ALPHANUM_COMPARATOR = new AlphanumComparator();
	
	private static final NumberComparator NUMBER_COMPARATOR = NumberComparator.getInstance();
	
}
