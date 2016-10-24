package es.predictia.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class IterablesTest {

	@Test
	public void iterablesTest() throws Exception{
		Iterable<List<Integer>> lists = Iterables.partitionContinous(Lists.newArrayList(1, 2, 3), 2);
		log.debug(lists.toString());
		Assert.assertEquals(2, Lists.newArrayList(lists).size());
		Assert.assertEquals(2, Lists.newArrayList(lists).get(1).size());
		lists = Iterables.partitionContinous(Lists.newArrayList(1, 2, 3), 3);
		log.debug(lists.toString());
		Assert.assertEquals(1, Lists.newArrayList(lists).size());
		Assert.assertEquals(3, Lists.newArrayList(lists).get(0).size());
		lists = Iterables.partitionContinous(Lists.newArrayList(1, 2, 3, 4), 3);
		log.debug(lists.toString());
		Assert.assertEquals(2, Lists.newArrayList(lists).size());
		Assert.assertEquals(3, Lists.newArrayList(lists).get(0).size());
		Assert.assertEquals(2, Lists.newArrayList(lists).get(1).size());
		lists = Iterables.partitionContinous(Lists.newArrayList(1, 2, 3, 4), 2);
		log.debug(lists.toString());
		Assert.assertEquals(3, Lists.newArrayList(lists).size());
		Assert.assertEquals(2, Lists.newArrayList(lists).get(1).size());
		Assert.assertEquals(2, Lists.newArrayList(lists).get(2).size());
		Assert.assertEquals(Integer.valueOf(2), Lists.newArrayList(lists).get(0).get(1));
		Assert.assertEquals(Integer.valueOf(2), Lists.newArrayList(lists).get(1).get(0));
		Assert.assertEquals(Integer.valueOf(3), Lists.newArrayList(lists).get(1).get(1));
		Assert.assertEquals(Integer.valueOf(3), Lists.newArrayList(lists).get(2).get(0));
	}

	private final static Logger log = LoggerFactory.getLogger(IterablesTest.class);
}