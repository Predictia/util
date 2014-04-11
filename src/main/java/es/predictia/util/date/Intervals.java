package es.predictia.util.date;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class Intervals {

	private Intervals(){}
	
	public static final Function<Interval, DateTime> INTERVAL_START_FUNCTION = new Function<Interval, DateTime>() {
		@Override
		public DateTime apply(Interval arg0) {
			return arg0.getStart();
		}
	};
	
	public static final Function<Interval, DateTime> INTERVAL_END_FUNCTION = new Function<Interval, DateTime>() {
		@Override
		public DateTime apply(Interval arg0) {
			return arg0.getEnd();
		}
	};
	
	public static Predicate<DateTime> containedInPredicate(final Interval interval){
		return new Predicate<DateTime>() {
			@Override
			public boolean apply(DateTime arg0) {
				return interval.contains(arg0);
			}
		};
	}
	
	public static Collection<Interval> getIntervalCollection(Collection<DateTime> itimes){
		if(itimes.isEmpty()){
			return Collections.emptyList();
		}else if(itimes.size() == 1){
			DateTime time = itimes.iterator().next();
			return Lists.newArrayList(new Interval(time, time));
		}else{
			TreeSet<DateTime> times = new TreeSet<DateTime>(itimes);
			DateCoverage dc = DateCoverage.getDateCoverage(times);
			Iterator<DateTime> timesIt = times.iterator();
			DateTime dt1 = timesIt.next();
			DateTime dt2 = timesIt.next();
			IntervalSplitter is = new IntervalSplitter(dc.getInterval(), (new Interval(dt1, dt2)).toDuration());
			return is.getIntervals();
		}
	}
	
}
