package es.predictia.util;

public class Nulls {

	private Nulls(){}
	
	public static <T> T defaultIfNull(T element, T defaultValue){
		return (element != null) ? element : defaultValue;
	}
	
	public static boolean nullSafeEquals(Object el1, Object el2){
		if(el1 != null) return el1.equals(el2);
		else return el2 == null;
	}
	
}
