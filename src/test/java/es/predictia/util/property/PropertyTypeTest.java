package es.predictia.util.property;

import org.junit.Assert;
import org.junit.Test;

public class PropertyTypeTest {

	@Test
	public void testParseColonProperty() throws Exception{
		String propLine = "myProp: myValue";
		Assert.assertEquals(
			new Property("myProp", "myValue"), 
			PropertyType.COLON.parsePropertyValue(propLine).get()
		);	
	}
	
	@Test
	public void testParseColonCommentProperty() throws Exception{
		String propLine = "myProp: myValue # my property";
		Assert.assertEquals(
			new Property("myProp", "myValue"), 
			PropertyType.COLON.parsePropertyValue(propLine).get()
		);		
	}
	
	@Test
	public void testParseEqualProperty() throws Exception{
		String propLine = "myProp = myValue";
		Assert.assertEquals(
			new Property("myProp", "myValue"), 
			PropertyType.EQUAL.parsePropertyValue(propLine).get()
		);	
	}
	
	@Test
	public void testParseEqualCommentProperty() throws Exception{
		String propLine = "myProp = myValue # my property";
		Assert.assertEquals(
			new Property("myProp", "myValue"), 
			PropertyType.EQUAL.parsePropertyValue(propLine).get()
		);		
	}
	
	@Test
	public void testUpdateColonProperty() throws Exception{
		String origPropLine = "myProp: myValue";
		Assert.assertEquals("myProp: myNewValue", 
			PropertyType.COLON.updateLinePropertyValue(origPropLine, "myProp", "myNewValue")
		);	
	}
	
	@Test
	public void testUpdateColonCommentProperty() throws Exception{
		String origPropLine = "myProp: myValue # my property";
		Assert.assertEquals("myProp: myNewValue # my property", 
			PropertyType.COLON.updateLinePropertyValue(origPropLine, "myProp", "myNewValue")
		);	
	}
	
	@Test
	public void testUpdateEqualProperty() throws Exception{
		String origPropLine = "myProp = myValue";
		Assert.assertEquals("myProp = myNewValue", 
			PropertyType.EQUAL.updateLinePropertyValue(origPropLine, "myProp", "myNewValue")
		);	
	}
	
	@Test
	public void testUpdateEqualCommentProperty() throws Exception{
		String origPropLine = "myProp = myValue # my property";
		Assert.assertEquals("myProp = myNewValue # my property", 
			PropertyType.EQUAL.updateLinePropertyValue(origPropLine, "myProp", "myNewValue")
		);	
	}
	
}
