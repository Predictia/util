package es.predictia.util.date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class TimeZoneTest {

	@Test
	public void testTimeZoneFromId() throws Exception{
		Assert.assertNotNull(TimeZone.getTimeZone("Madrid"));
	}
	
	@Ignore("depends on internet connection")
	@Test
	public void testTimeZoneFromLatLon() throws Exception{
		Assert.assertEquals(
			TimeZone.getTimeZone("Madrid"),
			TimeZone.getTimeZone(-3.81d, 43.46d)
		);
	}
		
	
}
