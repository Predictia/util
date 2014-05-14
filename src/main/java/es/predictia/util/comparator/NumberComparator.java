package es.predictia.util.comparator;

import java.util.Comparator;

/**
 * Comparator for Number type. This is a singleton class. Call getInstance() to
 * get the comparator.
 */
public class NumberComparator implements Comparator<Number> {

    private static NumberComparator singleton = null;

    /**
     * Has protected access to prevent other clients creating instances of the class ... it is stateless so we need only
     * one instance.
     */
    protected NumberComparator() {
    }

    /**
     * Returns <tt>NumberComparator</tt> singleton.
     *
     * @return an instance of NumberComparator.
     */
    public static NumberComparator getInstance() {
        if (singleton == null)
            singleton = new NumberComparator();
        return singleton;
    }

    /**
     * Compares two <tt>Number</tt>s.
     *
     * @param o1 the first object to be compared
     * @param o2 the second object to be compared
     * @return 0 if a and b are equal, -1 if a is less than b, 1 if a is more than b.
     */
    public int compare(Number o1, Number o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }else if (o1 == null) {
            return -1;
        }else if (o2 == null) {
            return 1;
        }else{
        	double d1 = o1.doubleValue();
            double d2 = o2.doubleValue();
            if(Double.isNaN(d1) && Double.isNaN(d2)){
            	return 0;
            }else if(Double.isNaN(d1)){
            	return -1;
            }else if(Double.isNaN(d2)){
            	return 1;
            }else if (d1 < d2){
            	return -1;
            }else if (d1 > d2){
            	return 1;
            }else{
            	return 0;
            }
        }
    }

}
