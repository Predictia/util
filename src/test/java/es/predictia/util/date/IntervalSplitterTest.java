package es.predictia.util.date;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;

import es.predictia.util.date.IntervalSplitter;
import es.predictia.util.date.Pattern;


public class IntervalSplitterTest {

		@Test
		public void testLongInterval() throws Exception{
			
			DateTime dt0 = new DateTime(DateTimeZone.UTC);
			Duration d = new Duration(Days.days(4*365).toStandardDuration());
			
			Interval i0 = new Interval(dt0, dt0.plusYears(40));
			
			IntervalSplitter is = new IntervalSplitter(i0, d);
			List<Interval> intervals = is.getIntervals();
			for(Interval i : intervals){
				System.out.println(Pattern.yyyy.getDateTimeFormatter().print(i.getStart()) + " - " + Pattern.yyyy.getDateTimeFormatter().print(i.getEnd()));
			}
			Assert.assertEquals(10, intervals.size());
			
			Interval i1 = new Interval(dt0, dt0.plusYears(41));
			IntervalSplitter is1 = new IntervalSplitter(i1, d);
			intervals = is1.getIntervals();
			for(Interval i : intervals){
				System.out.println(Pattern.yyyy.getDateTimeFormatter().print(i.getStart()) + " - " + Pattern.yyyy.getDateTimeFormatter().print(i.getEnd()));
			}
			Assert.assertEquals(11, intervals.size());
			
			Interval i2 = new Interval(dt0, dt0.plusYears(39));
			IntervalSplitter is2 = new IntervalSplitter(i2, d);
			intervals = is2.getIntervals();
			for(Interval i : intervals){
				System.out.println(Pattern.yyyy.getDateTimeFormatter().print(i.getStart()) + " - " + Pattern.yyyy.getDateTimeFormatter().print(i.getEnd()));
			}
			Assert.assertEquals(10, intervals.size());
			
		}
		
	}
