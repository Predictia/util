package es.predictia.util.date;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.joda.time.DateTimeZone;

import com.google.common.base.Function;

import es.predictia.util.Matchers;
import es.predictia.util.URLs;

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
	
	/**
	 * @param lon
	 * @param lat
	 * @return Returns {@link DateTimeZone} from latitude and longitude using a
	 *         google's timezone API request to determine TimeZone Id, and then
	 *         a call to {@link #getTimeZone(String)}
	 * @throws IOException
	 */
	public static DateTimeZone getTimeZone(double lon, double lat) throws IOException, IllegalArgumentException {
		return getTimeZone(getTimeZoneId(lon, lat));
	}

	/**
	 * Returns TimeZoneId from latitude and longitude using a google's timezone
	 * API request, for example:
	 * https://maps.googleapis.com/maps/api/timezone/xml?location=39.6034810,-119.6822510&timestamp=1331161200&sensor=false
	 *  
	 */
	private static String getTimeZoneId(double lon, double lat) throws IOException{
		long tsLong = System.currentTimeMillis() / 1000;
		String request = "https://maps.googleapis.com/maps/api/timezone/xml?location=" + lat + "," + lon + "&timestamp=" + tsLong + "&sensor=false";
		try {
			String xmltext = new URLs.UrlContentSupplier(request).getContent();
			if(!xmltext.contains("<status>OK</status>")){
				throw new IOException("Unable to adquire server response");
			}
			Function<String, String> matcherFunction = Matchers.firstMatchedGroupFunction(Pattern.compile("<time_zone_id>(.+)</time_zone_id>"));
			String id = null, line=null;
			BufferedReader bufReader = new BufferedReader(new StringReader(xmltext));
			try{
				while((line=bufReader.readLine()) != null){
					id = matcherFunction.apply(line.trim());
					if(id != null){
						break;
					}
				}
			}finally{
				bufReader.close();
			}
			if(id != null){
				return id;
			}else{
				throw new IllegalArgumentException("Unable to determine TimeZone id");
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
}
