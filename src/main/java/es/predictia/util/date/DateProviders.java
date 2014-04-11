package es.predictia.util.date;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

class DateProviders {

	private DateProviders(){}
	
	public static List<DateProvider> getDateProviderList(Iterable<? extends DateTime> dates){
		List<DateProvider> dcList = new ArrayList<DateProvider>();
		for(final DateTime date : dates){
			if(date != null){
				dcList.add(new DateProvider() {
					public DateTime getDate() {
						return date;
					}
				});
			}
		}
		return dcList;
	}

	public static List<DateProvider> getDateProviderListFromInterval(Iterable<? extends Interval> intervals){
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

	
}
