package es.predictia.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public class Functions {

	private Functions(){}
	
	public static final Function<Object, String> toStringConverter = new Function<Object, String>() {
		public String apply(Object value) {
			return value != null ? value.toString() : "";
		}
	};
	
	public static final Function<String, Boolean> stringToBooleanConverter = new Function<String, Boolean>() {
		public Boolean apply(String value) {
			try{
				Integer iv = Integer.valueOf(value);
				return ! (iv.equals(Integer.valueOf(0)));
			}catch (NumberFormatException e) {
				return Boolean.valueOf(value);
			}
		}
	};
	
	public static final Function<Boolean, String> booleanToStringConverter = new Function<Boolean, String>() {
		public String apply(Boolean value) {
			return value != null ? value.toString() : Boolean.FALSE.toString();
		}
	};
	
	public static final Function<String, Integer> stringToIntegerConverter = new Function<String, Integer>(){
		public Integer apply(String arg0) {
			return Ints.tryParse(arg0);
		}
	};
	
	public static final Function<String, Long> stringToLongConverter = new Function<String, Long>(){
		public Long apply(String arg0) {
			return Longs.tryParse(arg0);
		}
	};
	
	public static final Function<String, Float> stringToFloatConverter = new Function<String, Float>() {
		public Float apply(String arg0) {
			return Floats.tryParse(arg0);
		}
	};
	
	public static final Function<String, Double> stringToDoubleConverter = new Function<String, Double>() {
		public Double apply(String arg0) {
			return Double.valueOf(arg0);
		}
	};
	
	public static final Function<String, DateTime> stringToDateTimeConverter(final String pattern){
		return new Function<String, DateTime>() {
			public DateTime apply(String arg0) {
				return DateTimeFormat.forPattern(pattern).withZone(DateTimeZone.UTC).parseDateTime(arg0);
			}
		};
	}
	
	public static final Function<Iterable<Object>, Double> sum = new Function<Iterable<Object>, Double>(){
		@Override
		public Double apply(Iterable<Object> arg0) {
			Double sum = Double.NaN;	
			Iterable<Object> numbers = Iterables.filter(arg0, VALID_NUMBER_PREDICATE);
			for(Object o : numbers){
				if(sum.isNaN()){
					sum = 0d;
				}
				sum = sum + ((Number)o).doubleValue();
			}
			return sum;
		}
	};
	
	public static final Function<Iterable<Object>, Integer> count = new Function<Iterable<Object>, Integer>(){
		@Override
		public Integer apply(Iterable<Object> arg0) {
			return Lists.newArrayList(Iterables.filter(arg0, VALID_NUMBER_PREDICATE)).size();
		}
	};
	
	public static final Function<Iterable<Object>, Double> avg = new Function<Iterable<Object>, Double>(){
		@Override
		public Double apply(Iterable<Object> arg0) {
			Integer c  = count.apply(arg0);
			if(c > 0){
				Double s  = sum.apply(arg0);
				return s/c;
			}else{
				return Double.NaN;
			}

		}
	};
	
	private static final Predicate<Object> VALID_NUMBER_PREDICATE = new Predicate<Object>() {
		@Override
		public boolean apply(Object arg0) {
			if(arg0 instanceof Number){
				Double d = ((Number) arg0).doubleValue();
				return !d.isNaN();
			}
			return false;
		}
	};
	
}
