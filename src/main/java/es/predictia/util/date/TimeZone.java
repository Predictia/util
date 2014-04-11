package es.predictia.util.date;

import org.joda.time.DateTimeZone;

public class TimeZone {

	private TimeZone(){}
	
	public static DateTimeZone getTimeZone(String location) {
		String[] tzIds = java.util.TimeZone.getAvailableIDs();
		for (String tzId : tzIds) {
			if (tzId.contains(location)) {
				return DateTimeZone.forID(tzId);
			}
		}
		throw new IllegalArgumentException("Could not found zone: " + location);
	}
	
}
