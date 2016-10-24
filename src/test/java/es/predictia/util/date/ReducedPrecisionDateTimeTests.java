package es.predictia.util.date;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class ReducedPrecisionDateTimeTests {

	@Test
	public void testReducedPrecisionDateTime(){
		ReducedPrecisionDateTime rpdt1 = new ReducedPrecisionDateTime(Pattern.dd_MM_yyyy, new DateTime());
		ReducedPrecisionDateTime rpdt2 = new ReducedPrecisionDateTime(Pattern.dd_MM_yyyy, new DateTime().minusMillis(1));
		ReducedPrecisionDateTime rpdt3 = new ReducedPrecisionDateTime(Pattern.dd_MM_yyyy, new DateTime().plusDays(1));
		Assert.assertEquals(rpdt1, rpdt2);
		Assert.assertNotSame(rpdt1, rpdt3);
		ReducedPrecisionDateTime rpdt4 = new ReducedPrecisionDateTime(Pattern.yyyy, new DateTime());
		ReducedPrecisionDateTime rpdt5 = new ReducedPrecisionDateTime(Pattern.yyyy, new DateTime().minusMillis(1));
		ReducedPrecisionDateTime rpdt6 = new ReducedPrecisionDateTime(Pattern.yyyy, new DateTime().plusYears(1));
		Assert.assertEquals(rpdt4, rpdt5);
		Assert.assertNotSame(rpdt4, rpdt6);
	}
	
}
