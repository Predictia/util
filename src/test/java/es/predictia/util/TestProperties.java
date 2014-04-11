package es.predictia.util;

import java.io.File;
import java.io.FileReader;
import java.util.NoSuchElementException;

import junit.framework.Assert;
import static es.predictia.util.PropertyDefinitions.processLine;
import static es.predictia.util.PropertyDefinitions.findPropertyValue;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

public class TestProperties {

	@Test
	public void testStringProperties() throws Exception{
		String in = "MinLat = 32.4425\nMinLon = 4.31796\nMaxLat = 53.2517\nMaxLon = 34.0674";
		Assert.assertNotNull(findPropertyValue(in, "MinLat"));
		Assert.assertNotNull(findPropertyValue(in, "MaxLat"));
		Assert.assertNotNull(findPropertyValue(in, "MinLon"));
		Assert.assertNotNull(findPropertyValue(in, "MaxLon"));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testComment2() throws Exception{
		findPropertyValue("   #   WRF4G_DOMAINPATH=\"/home/kktuax/WRF4G/repository/domains\"", "WRF4G_DOMAINPATH");
	}

	@Test(expected=NoSuchElementException.class)
	public void testComment() throws Exception{
		findPropertyValue("#WRF4G_DOMAINPATH=\"/home/kktuax/WRF4G/repository/domains\"", "WRF4G_DOMAINPATH");
	}
	
	@Test
	public void testPropertySet() throws Exception{
		{
			String inline = " miProp = miProp            # mi comentario ";
			String nuevoVal = "miNuevoVal";
			Assert.assertEquals(" miProp = miNuevoVal            # mi comentario ", processLine(inline, "miProp", nuevoVal));
		}{
			String inline = " miProp2 = miValor2";
			String nuevoVal = "miNuevoValor2";
			Assert.assertEquals(" miProp2 = miNuevoValor2", processLine(inline, "miProp2", nuevoVal));
		}{
			String inline = " miProp3 = \"miValor2\"";
			String nuevoVal = "miNuevoValor3";
			Assert.assertEquals(" miProp3 = \"miNuevoValor3\"", processLine(inline, "miProp3", nuevoVal));
		}{
			String inline = "experiment_name=\"meteogrid1\"";
			String nuevoVal = "miNuevoNombre";
			Assert.assertEquals("experiment_name=\"miNuevoNombre\"", processLine(inline, "experiment_name", nuevoVal));
		}{
			String inline = "experiment_name='meteogrid1'";
			String nuevoVal = "miNuevoNombre";
			Assert.assertEquals("experiment_name='miNuevoNombre'", processLine(inline, "experiment_name", nuevoVal));
		}{
			String inline = "fields                   = 'Q2,T2,GHT,MSLP,PSFC,U10,V10,RAINTOT,Times,UST,PBLH,REGIME',";
			String nuevoVal = "T2,Q2,PSFC,ACSWDNB,ACLWDNB,RAINTOT,U10,V10,CLDFRA,CLT";
			Assert.assertEquals("fields                   = 'T2,Q2,PSFC,ACSWDNB,ACLWDNB,RAINTOT,U10,V10,CLDFRA,CLT',", processLine(inline, "fields", nuevoVal));
		}
	}
	
	@Test
	public void testFileProperties() throws Exception{
		File file = File.createTempFile("test-", ".txt");
		Streams.writeLinesToFile(Lists.newArrayList("a = 1", "a = 2"), file, Charsets.UTF_8);
		String newValue = "25";
		PropertyDefinitions.updateFileWithProperties(file, Charsets.UTF_8, new PropertyDefinitions.Property("a", newValue));
		Assert.assertEquals(newValue, PropertyDefinitions.findPropertyValue(new FileReader(file), "a"));
	}
	
}
