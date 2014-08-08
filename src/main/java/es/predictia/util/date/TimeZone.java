package es.predictia.util.date;

import org.joda.time.DateTimeZone;

public class TimeZone {

	private TimeZone(){}
	
	/**
	 * Searches for a {@link DateTimeZone} using {@link String#contains(CharSequence)}
	 * method for input parameter instead of {@link String#equals(Object)}
	 * 
	 * @param location
	 * @return first DateTimeZone that contains location string 
	 * @throws IllegalArgumentException if no suitable {@link DateTimeZone} was found
	 */
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
