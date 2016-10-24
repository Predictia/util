package es.predictia.util.comparator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class OrderingsTests {

	@Test
	public void testNullsFirstOrdering() throws Exception{
		List<String> lista = new ArrayList<String>();
		lista.add("el1");
		lista.add("el2");
		lista.add(null);
		Assert.assertNull(Orderings.NULLS_FIRST_ORDERING.sortedCopy(lista).get(0));
		lista = new ArrayList<String>();
		lista.add(null);
		lista.add("el1");
		lista.add("el2");
		lista.add(null);
		Assert.assertNull(Orderings.NULLS_FIRST_ORDERING.sortedCopy(lista).get(0));
		Assert.assertNull(Orderings.NULLS_FIRST_ORDERING.sortedCopy(lista).get(1));
	}
	
	@Test
	public void testNaNsFirstOrdering() throws Exception{
		List<Number> lista = new ArrayList<Number>();
		lista.add(1d);
		lista.add(Double.NaN);
		lista.add(3d);
		Assert.assertEquals(Double.NaN, Orderings.NUMBERS_ORDERING.sortedCopy(lista).get(0));
		lista = new ArrayList<Number>();
		lista.add(4d);
		lista.add(2d);
		lista.add(3d);
		Assert.assertEquals(2d, Orderings.NUMBERS_ORDERING.sortedCopy(lista).get(0));
	}
	
}
