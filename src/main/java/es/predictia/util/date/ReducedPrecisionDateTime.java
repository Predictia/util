package es.predictia.util.date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ReducedPrecisionDateTime implements DateProvider, Comparable<ReducedPrecisionDateTime>{

	private final DateTimeFormatter fmt;
	private final DateTime dt;	
	
	public ReducedPrecisionDateTime(DateTimeFormatter fmt, DateTime dt) {
		this.fmt = fmt;
		this.dt = this.fmt.parseDateTime(this.fmt.print(dt));
	}
	
	public ReducedPrecisionDateTime(Pattern pattern, DateTime dt) {
		this.fmt = pattern.getDateTimeFormatter();
		this.dt = this.fmt.parseDateTime(this.fmt.print(dt));
	}
	
	public ReducedPrecisionDateTime(String pattern, DateTime dt){
		this.fmt = DateTimeFormat.forPattern(pattern).withZone(DateTimeZone.UTC);
		this.dt = this.fmt.parseDateTime(this.fmt.print(dt));
	}
		
	public DateTime getDate() {
		return dt;
	}
		
	public String toString(){
		return fmt.print(dt);
	}
		
	public int hashCode(){
		return toString().hashCode();
	}
		
	public boolean equals(Object o){
		if(o instanceof ReducedPrecisionDateTime){
			return ((ReducedPrecisionDateTime) o).toString().equals(this.toString());
		}
		return false;
	}

	public int compareTo(ReducedPrecisionDateTime o) {
		return getDate().compareTo(o.getDate());
	}
	
}