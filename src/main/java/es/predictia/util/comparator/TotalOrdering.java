/**
 * 
 */
package es.predictia.util.comparator;

import com.google.common.collect.Ordering;

/** Ordering que situa el valor total al principio
 * @author Max
 *
 */
public class TotalOrdering extends Ordering<Object>{
	
	private final Object totalCode;
	
	public TotalOrdering(Object totalCode){
		this.totalCode = totalCode;
	}
	
	@Override
	public int compare(Object arg0, Object arg1) {
		if(totalCode.equals(arg0)){
			return (totalCode.equals(arg1)) ? 0 : 1;
		}else if(totalCode.equals(arg1)){
			return (totalCode.equals(arg0)) ? 0 : -1;
		}else{
			return 0;
		}
	}
	
}