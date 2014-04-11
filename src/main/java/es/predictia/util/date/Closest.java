package es.predictia.util.date;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import com.google.common.collect.Lists;

public class Closest<T extends DateProvider> {

	private final T closest; 
	
	public Closest(Iterable<T> dates, T date){
		List<T> fechas = Lists.newArrayList(dates);
    	if(fechas.isEmpty()) throw new IllegalArgumentException("Dates is empty");
    	T closest = fechas.get(0);
    	for(T fecha : fechas){
    		Duration oldDistance = getDistance(date, closest);
    		Duration newDistance = getDistance(date, fecha);
    		if(newDistance.isShorterThan(oldDistance)){
    			closest = fecha;
    		}
		}
    	this.closest = closest;
	}
	
	public Closest(Iterable<T> dates, DateTime date){
		List<T> fechas = Lists.newArrayList(dates);
    	if(fechas.isEmpty()) throw new IllegalArgumentException("Dates is empty");
    	T closest = fechas.get(0);
    	for(T fecha : fechas){
    		Duration oldDistance = getDistance(date, closest);
    		Duration newDistance = getDistance(date, fecha);
    		if(newDistance.isShorterThan(oldDistance)){
    			closest = fecha;
    		}
		}
    	this.closest = closest;
	}
	
	public T getClosest() {
		return closest;
	}

	public static DateTime getClosestDate(Iterable<? extends DateTime> dates, final DateTime date){
    	Closest<DateProvider> c = new Closest<DateProvider>(
    		DateProviders.getDateProviderList(dates), 
    		new DateProvider() {
				public DateTime getDate() {
					return date;
				}
    		}
    	);
    	return c.getClosest().getDate();
	}
	
    public static Duration getDistance(DateTime dt1, DateTime dt2){
    	Interval interval = (dt1.isAfter(dt2)) ? new Interval(dt2, dt1) : new Interval(dt1, dt2);
    	return interval.toDuration();
    }
    
    public static <T extends DateProvider> Duration getDistance(T dt1, T dt2){
    	return getDistance(dt1.getDate(), dt2.getDate());
    }
    
    public static <T extends DateProvider> Duration getDistance(T dt1, DateTime dt2){
    	return getDistance(dt1.getDate(), dt2);
    }
    
    public static <T extends DateProvider> Duration getDistance(DateTime dt1, T dt2){
    	return getDistance(dt1, dt2.getDate());
    }
	
}
