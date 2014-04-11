package es.predictia.util.date;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class IntervalSplitter {

	public enum SplitPeriod implements Function<DateTime, DateTime>{
		second{
			public DateTime apply(DateTime arg0) {
				return arg0.plusSeconds(1);
			}
		}, minute{
			public DateTime apply(DateTime arg0) {
				return arg0.plusMinutes(1);
			}
		}, hour{
			public DateTime apply(DateTime arg0) {
				return arg0.plusHours(1);
			}
		}, day{
			public DateTime apply(DateTime arg0) {
				return arg0.plusDays(1);
			}
		}, month{
			public DateTime apply(DateTime arg0) {
				return arg0.plusMonths(1);
			}
		}, year{
			public DateTime apply(DateTime arg0) {
				return arg0.plusYears(1);
			}
		};
	}
	
	private final Interval interval;
	private final Function<DateTime, DateTime> timeIncrementerFunction;
	
	public IntervalSplitter(Interval interval, Function<DateTime, DateTime> timeIncrementerFunction){
		this.interval = interval;
		this.timeIncrementerFunction = timeIncrementerFunction;
	}
	
	public IntervalSplitter(Interval interval, final Duration duration){
		this.interval = interval;
		this.timeIncrementerFunction = new Function<DateTime, DateTime>() {
			@Override public DateTime apply(DateTime arg0) {
				return addDuration(arg0, duration);
			}
		};
	}
	
	public List<Interval> getIntervals(){
		List<DateTime> serie = getSerie();
		LinkedHashSet<Interval> serieIntervalos = new LinkedHashSet<Interval>();
		Iterator<DateTime> it = serie.iterator();
		if(serie.size() >= 2){
			DateTime fecha1 = it.next();
			DateTime fecha2 = it.next();
			while(true){
				serieIntervalos.add(new Interval(fecha1, fecha2));
				if(!it.hasNext()) break;
				else{
					fecha1 = new DateTime(fecha2);
					fecha2 = it.next();
				}
			}
		}
		return Lists.newArrayList(serieIntervalos);
	}
	
	public List<DateTime> getSerie(){
		TreeSet<DateTime> serie = new TreeSet<DateTime>();
		DateTime fechaInicial = interval.getStart();
		DateTime fechaFinal = interval.getEnd();
		DateTime fechaSerie = fechaInicial;
		serie.add(fechaInicial);
		while(fechaSerie.isBefore(fechaFinal)){
			fechaSerie = timeIncrementerFunction.apply(fechaSerie);
			if(fechaSerie.isBefore(fechaFinal)){
				serie.add(fechaSerie);
			}
		}
		serie.add(fechaFinal);
		return Lists.newArrayList(serie);
	}
	
	private static DateTime addDuration(DateTime dt, Duration increment){
		if(!increment.isShorterThan(Duration.standardDays(365))){
			int days = Days.daysIn(new Interval(dt, dt.plus(increment))).getDays();
			int years = days/365;
			Duration extraduration = increment.minus(Days.days(days).toStandardDuration());
			DateTime result = dt.plusYears(years);
			return addDuration(result, extraduration);
		}
		if(!increment.isShorterThan(Duration.standardDays(30))){
			int days = Days.daysIn(new Interval(dt, dt.plus(increment))).getDays();
			int months = days/30;
			Duration extraduration = increment.minus(Days.days(days).toStandardDuration());
			DateTime result = dt.plusMonths(months);
			return addDuration(result, extraduration);
		}
		return dt.plus(increment);
	}
	
}
