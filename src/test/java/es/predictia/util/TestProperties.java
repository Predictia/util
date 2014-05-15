package es.predictia.util;

import java.io.File;
import java.io.FileReader;
import java.util.NoSuchElementException;

import junit.framework.Assert;
import static es.predictia.util.PropertyDefinitions.updateLinePropertyValue;
import static es.predictia.util.PropertyDefinitions.findPropertyValue;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import es.predictia.util.PropertyDefinitions.Property;

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
	public void testPropertyValue() throws Exception{
		{
			String line = " miProp = miValue            ";
			Assert.assertEquals("miValue", findPropertyValue(line, "miProp"));
		}{
			String line = " miProp = miValue            # mi comentario ";
			Assert.assertEquals("miValue", findPropertyValue(line, "miProp"));
		}{
			String line = " miProp = miProp=2            # mi comentario ";
			Assert.assertEquals("miProp=2", findPropertyValue(line, "miProp"));
		}
	}
	
	@Test
	public void testPropertySet() throws Exception{
		{
			String inline = " miProp = miProp            # mi comentario ";
			String nuevoVal = "miNuevoVal";
			Assert.assertEquals(" miProp = miNuevoVal            # mi comentario ", updateLinePropertyValue(inline, "miProp", nuevoVal));
		}{
			String inline = " miProp2 = miValor2";
			String nuevoVal = "miNuevoValor2";
			Assert.assertEquals(" miProp2 = miNuevoValor2", updateLinePropertyValue(inline, "miProp2", nuevoVal));
		}{
			String inline = " miProp3 = \"miValor2\"";
			String nuevoVal = "miNuevoValor3";
			Assert.assertEquals(" miProp3 = \"miNuevoValor3\"", updateLinePropertyValue(inline, "miProp3", nuevoVal));
		}{
			String inline = "experiment_name=\"meteogrid1\"";
			String nuevoVal = "miNuevoNombre";
			Assert.assertEquals("experiment_name=\"miNuevoNombre\"", updateLinePropertyValue(inline, "experiment_name", nuevoVal));
		}{
			String inline = "experiment_name='meteogrid1'";
			String nuevoVal = "miNuevoNombre";
			Assert.assertEquals("experiment_name='miNuevoNombre'", updateLinePropertyValue(inline, "experiment_name", nuevoVal));
		}{
			String inline = "fields                   = 'Q2,T2,GHT,MSLP,PSFC,U10,V10,RAINTOT,Times,UST,PBLH,REGIME',";
			String nuevoVal = "T2,Q2,PSFC,ACSWDNB,ACLWDNB,RAINTOT,U10,V10,CLDFRA,CLT";
			Assert.assertEquals("fields                   = 'T2,Q2,PSFC,ACSWDNB,ACLWDNB,RAINTOT,U10,V10,CLDFRA,CLT',", updateLinePropertyValue(inline, "fields", nuevoVal));
		}
	}
	
	@Test
	public void testFileProperties() throws Exception{
		File file = File.createTempFile("test-", ".txt");
		Streams.writeLinesToFile(Lists.newArrayList("a = 1", "a = 2"), file, Charsets.UTF_8);
		String newValue = "25", fValue = "fValue";
		PropertyDefinitions.updateFileWithProperties(file, Charsets.UTF_8, 
			new PropertyDefinitions.Property("a", newValue),
			new PropertyDefinitions.Property("f", fValue)
		);
		Assert.assertEquals(newValue, PropertyDefinitions.findPropertyValue(new FileReader(file), "a"));
		Assert.assertEquals(fValue, PropertyDefinitions.findPropertyValue(new FileReader(file), "f"));
	}
	
	@Test
	public void testUpdateFileProperties() throws Exception{
		File file = File.createTempFile("test-", ".txt");
		PropertyDefinitions.updateFileWithProperties(file, Charsets.UTF_8, 
			new PropertyDefinitions.Property("a", "2"),
			new PropertyDefinitions.Property("b", "3")
		);
		Assert.assertEquals("2", PropertyDefinitions.findPropertyValue(new FileReader(file), "a"));
		Assert.assertEquals("3", PropertyDefinitions.findPropertyValue(new FileReader(file), "b"));
		PropertyDefinitions.updateFileWithProperties(file, Charsets.UTF_8, new Function<Property, Property>() {
			@Override
			public Property apply(Property input) {
				return new Property(input.getName(), input.getName() + input.getValue());
			}
		});
		Assert.assertEquals("a2", PropertyDefinitions.findPropertyValue(new FileReader(file), "a"));
		Assert.assertEquals("b3", PropertyDefinitions.findPropertyValue(new FileReader(file), "b"));
	}
	
}
