package es.predictia.util;

import java.util.List;

import org.junit.Test;

import junit.framework.Assert;

import com.google.common.collect.Lists;

public class EqualsTest {

	@Test
	public void equalsTest() throws Exception{
		List<?> list = Lists.newArrayList(Integer.valueOf(1), Integer.valueOf(2));
		List<?> list2 = Lists.newArrayList(Integer.valueOf(1), Integer.valueOf(2));
		Assert.assertTrue(list.equals(list2));
	}
	
}
