package es.predictia.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class MetadataTest {

	@Test
	public void metadataTest() throws Exception{
		Metadata meta = new Metadata();
		String var1Name = "var1", var2Name = "var2", var3Name = "var3";
		String var1 = "var1value", var2 = "var2value";
		List<String> var3 = Lists.newArrayList("var3value1", "var3value2");
		meta.setVariable(var1Name, var1);
		meta.setVariable(var2Name, var2);
		meta.setVariableList(var3Name, var3);
		Assert.assertEquals(var1, meta.getVariable(var1Name));
		Assert.assertEquals(var2, meta.getVariable(var2Name));
		Assert.assertEquals(var3, meta.getVariableList(var3Name));
	}
	
	@Test
	public void metadataCustomTest() throws Exception{
		Metadata meta = new Metadata();
		meta.setVariableSeparatorCode("[mi;]");
		meta.setListSeparatorCode("[mi,]");
		meta.setAssignCode("[mi=]");
		String var1Name = "var1", var2Name = "var2", var3Name = "var3";
		String var1 = "var1value", var2 = "var2value";
		List<String> var3 = Lists.newArrayList("var3value1", "var3value2");
		meta.setVariable(var1Name, var1);
		meta.setVariable(var2Name, var2);
		meta.setVariableList(var3Name, var3);
		Assert.assertEquals(var1, meta.getVariable(var1Name));
		Assert.assertEquals(var2, meta.getVariable(var2Name));
		Assert.assertEquals(var3, meta.getVariableList(var3Name));
	}
	
}
