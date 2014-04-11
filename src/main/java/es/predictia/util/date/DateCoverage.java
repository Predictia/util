package es.predictia.util.date;

import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

public class DateCoverage {

	private final TreeSet<Integer> yearsCovered;
	private final TreeSet<Integer> monthsCovered;
	private final ArrayListMultimap<Integer, Integer> monthsOfYear;
	private final ArrayListMultimap<Integer, Integer> daysOfYear;
	private final ArrayListMultimap<Integer, Integer> yearsWithMonth;
	private final Interval interval;
	
	public DateCoverage(Iterable<? extends DateProvider> elems){
		this.yearsCovered = new TreeSet<Integer>();
		this.monthsCovered = new TreeSet<Integer>();
		this.monthsOfYear = ArrayListMultimap.create();
		this.daysOfYear = ArrayListMultimap.create();
		this.yearsWithMonth = ArrayListMultimap.create();
		for(DateProvider dp : elems){
			this.yearsCovered.add(dp.getDate().getYear());
			this.monthsCovered.add(dp.getDate().getMonthOfYear());
			this.monthsOfYear.put(dp.getDate().getYear(), dp.getDate().getMonthOfYear());
			this.daysOfYear.put(dp.getDate().getYear(), dp.getDate().getDayOfYear());
			this.yearsWithMonth.put(dp.getDate().getMonthOfYear(), dp.getDate().getYear());
		}
		Interval interval = null;
		try{
			interval  = getInterval(elems);
		}
		catch (Exception e) {
			logger.debug(e.getMessage());
		}
		this.interval = interval;
	}

	public List<Integer> getYearsCovered() {
		return Lists.newArrayList(yearsCovered);
	}
	public List<Integer> getMonthsCovered() {
		return Lists.newArrayList(monthsCovered);
	}
	public List<Integer> getMonthsOfYearCovered(Integer year) {
		return Lists.newArrayList(new TreeSet<Integer>(monthsOfYear.get(year)));
	}
	public List<Integer> getDaysOfYearCovered(Integer year) {
		return Lists.newArrayList(new TreeSet<Integer>(daysOfYear.get(year)));
	}
	public List<Integer> getYearsWithMonthCovered(Integer month) {
		return Lists.newArrayList(new TreeSet<Integer>(yearsWithMonth.get(month)));
	}
	
	public Integer getNumberOfDaysCovered(){
		return daysOfYear.entries().size();
	}
	public Integer getNumberOfMonthsCovered(){
		return monthsOfYear.entries().size();
	}
	
	/**
	 * @return Interval of dates covered or null if no dates were provided
	 */
	public Interval getInterval() {
		return interval;
	}

	public static DateCoverage getDateCoverage(Iterable<? extends DateTime> dates){
		return new DateCoverage(DateProviders.getDateProviderList(dates));
	}
	
	public static DateCoverage getIntervalCoverage(Iterable<? extends Interval> intervals){
		return new DateCoverage(DateProviders.getDateProviderListFromInterval(intervals));
	}
	
	private static Interval getInterval(Iterable<? extends DateProvider> fechas){
		List<DateProvider> dates = Lists.newArrayList(fechas);
		if(dates.isEmpty()) throw new IllegalArgumentException("No dates were provided");
		DateTime min = dates.get(0).getDate(), max = dates.get(0).getDate();
		for(DateProvider cada : dates){
			if(cada.getDate().isBefore(min)){
				min = cada.getDate();
			}
			else if(cada.getDate().isAfter(max)){
				max = cada.getDate();
			}
		}
		return new Interval(min, max);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DateCoverage.class);
}
