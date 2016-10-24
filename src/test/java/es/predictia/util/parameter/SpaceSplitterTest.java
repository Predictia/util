package es.predictia.util.parameter;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SpaceSplitterTest {

	@Test
	public void testSplitEscapedSpaces() throws Exception{
		List<String> result = SpaceSplitter.split("one\\ two three");
		Assert.assertEquals(2, result.size());
		Assert.assertTrue(result.contains("one two"));
		Assert.assertTrue(result.contains("three"));
	}
	
	@Test
	public void testSplitSpaces() throws Exception{
		List<String> result = SpaceSplitter.split("one two three");
		Assert.assertEquals(3, result.size());
		Assert.assertTrue(result.contains("one"));
		Assert.assertTrue(result.contains("two"));
		Assert.assertTrue(result.contains("three"));
	}
	
}
