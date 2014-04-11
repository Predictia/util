package es.predictia.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junit.framework.Assert;

import com.google.common.collect.Lists;

public class FunctionsTest {

	@Test
	public void countTest() throws Exception{
		Assert.assertEquals(
			Integer.valueOf(1), 
			Functions.count.apply(new ArrayList<Object>(Lists.newArrayList(Integer.valueOf(0), null)))
		);
		List<Object> list2 = new ArrayList<Object>(); 
		list2.add(Integer.valueOf(0));
		list2.add(null);
		list2.add(null);
		list2.add(Double.valueOf(2));
		Assert.assertEquals(
			Integer.valueOf(2), 
			Functions.count.apply(list2)
		);
	}
	
	@Test
	public void sumTest() throws Exception{
		Assert.assertEquals(
			Double.valueOf(0), 
			Functions.sum.apply(new ArrayList<Object>(Lists.newArrayList(Integer.valueOf(0), null)))
		);
		List<Object> list2 = new ArrayList<Object>(); 
		list2.add(Integer.valueOf(0));
		list2.add(null);
		list2.add(null);
		list2.add(Double.valueOf(2));
		Assert.assertEquals(
			Double.valueOf(2), 
			Functions.sum.apply(list2)
		);
	}
	
	@Test
	public void avgTest() throws Exception{
		Assert.assertEquals(
			Double.valueOf(0), 
			Functions.avg.apply(new ArrayList<Object>(Lists.newArrayList(Integer.valueOf(0), null)))
		);
		List<Object> list2 = new ArrayList<Object>(); 
		list2.add(Integer.valueOf(0));
		list2.add(null);
		list2.add(null);
		list2.add(Double.valueOf(2));
		Assert.assertEquals(
			Double.valueOf(1), 
			Functions.avg.apply(list2)
		);
		list2.add(Double.valueOf(4));
		Assert.assertEquals(
			Double.valueOf(4+2) / Integer.valueOf(3), 
			Functions.avg.apply(list2)
		);
	}
	
}
