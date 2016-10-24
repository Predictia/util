package es.predictia.util;

import org.junit.Assert;
import org.junit.Test;

public class NullsTest {

	@Test
	public void testNullEquals() throws Exception{
		{
			Integer el1 = null, el2 = null;
			Assert.assertTrue(Nulls.nullSafeEquals(el1, el2));
		}{
			Integer el1 = null, el2 = 1;
			Assert.assertFalse(Nulls.nullSafeEquals(el1, el2));
		}{
			Integer el1 = 2, el2 = null;
			Assert.assertFalse(Nulls.nullSafeEquals(el1, el2));
		}{
			Integer el1 = 1, el2 = 1;
			Assert.assertTrue(Nulls.nullSafeEquals(el1, el2));
		}{
			Integer el1 = 2, el2 = 1;
			Assert.assertFalse(Nulls.nullSafeEquals(el1, el2));
		}
	}
	
}
