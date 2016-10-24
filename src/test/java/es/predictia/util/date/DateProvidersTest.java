package es.predictia.util.date;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;

public class DateProvidersTest {

	@Test
	public void testOrdering() throws Exception{
		DateTime dt1 = new DateTime().withMillisOfDay(0);
		DateTime dt2 = dt1.plusHours(2);
		List<DateProvider> dps = DateProviders.dateProvidersFromDateTime(dt2, dt1);
		Assert.assertEquals(dt1, DateProviders.ORDERING.sortedCopy(dps).get(0).getDate());
		Assert.assertEquals(dt2, DateProviders.ORDERING.sortedCopy(dps).get(1).getDate());
	}
	
	@Test
	public void testPeriodCheck() throws Exception{
		DateTime dt1 = new DateTime().withMillisOfDay(0);
		DateTime dt2 = dt1.plusHours(2);
		List<DateProvider> dps = DateProviders.dateProvidersFromDateTime(dt1, dt2);
		Assert.assertTrue(DateProviders.isPeriodEqual(dps, Duration.standardHours(2)));
		Assert.assertFalse(DateProviders.isPeriodEqual(dps, Duration.standardHours(3)));
	}
	
}
