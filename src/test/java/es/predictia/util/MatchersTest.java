package es.predictia.util;

import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

public class MatchersTest {

	@Test
	public void testMatchersAll() throws Exception{
		Pattern pattern = Pattern.compile(".+(parsed\\[(.+)\\]).+");
		String text = "{ parsed[dischargeDate] : datesOrdered(parsed[admissionDate], parsed[dischargeDate]) IS TRUE : 'fecha alta anterior a fecha ingreso' }";
		Assert.assertEquals(2, Matchers.allMatchedGroupsFunction(pattern).apply(text).size());
	}
		
	
}
