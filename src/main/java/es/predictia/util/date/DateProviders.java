package es.predictia.util.date;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class DateProviders {

	private DateProviders(){}
	
	static List<DateProvider> dateProvidersFromDateTime(DateTime... dates){
		return dateProvidersFromDateTime(Lists.newArrayList(dates));
	}
	
	static List<DateProvider> dateProvidersFromDateTime(Iterable<? extends DateTime> dates){
		List<DateProvider> dcList = new ArrayList<DateProvider>();
		for(final DateTime date : dates){
			if(date != null){
				dcList.add(dateProvider(date));
			}
		}
		return dcList;
	}

	static List<DateProvider> dateProvidersFromIntervals(Interval... intervals){
		return dateProvidersFromIntervals(Lists.newArrayList(intervals));
	}
	
	static List<DateProvider> dateProvidersFromIntervals(Iterable<? extends Interval> intervals){
		List<DateProvider> dcList = new ArrayList<DateProvider>();
		for(final Interval interval : intervals){
			if(interval != null){
				dcList.add(new DateProvider() {
					public DateTime getDate() {
						return interval.getStart();
					}
				});
				dcList.add(new DateProvider() {
					public DateTime getDate() {
						return interval.getEnd();
					}
				});
			}
		}
		return dcList;
	}

	/**
	 * @return true if distance between elements is constant and equal to the supplied period 
	 */
	public static boolean isPeriodEqual(Iterable<? extends DateProvider> elements, Duration period){
		DateTime lastTime = null;
		for(DateProvider dp : ORDERING.sortedCopy(elements)){
			if(lastTime != null){
				if(!dp.getDate().equals(lastTime.plus(period))){
					return false;
				}
			}
			lastTime = dp.getDate();
		}
		return true;
	}

	static DateProvider dateProvider(final DateTime dateTime){
		return new DateProvider() {
			@Override
			public DateTime getDate() {
				return dateTime;
			}
		};
	}
	
	public static final Function<DateProvider, DateTime> TO_DATE_TIME_FUNCTION = new Function<DateProvider, DateTime>() {
		@Override
		public DateTime apply(DateProvider arg0) {
			return arg0.getDate();
		}
	};
	
	/**
	 * {@link Ordering} using the {@link DateTime} associated to the elements 
	 */
	public static final Ordering<DateProvider> ORDERING = Ordering.natural().nullsLast().onResultOf(TO_DATE_TIME_FUNCTION);
	
}
