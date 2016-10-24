package es.predictia.util.property;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.StandardSystemProperty;

public class PropertySaveTest {
	
	@Test
	public void testDeleteProperty() throws Exception{
		String name = "miProp", value = "miValor";
		File file = File.createTempFile("testEmpty-", ".tmp");
		{
			PropertySet ps = new PropertySet(new Property(name, value));
			ps.save(file, CHARSET, TYPE);
		}{
			PropertySet ps = PropertySet.fromFile(file, CHARSET, TYPE);
			Assert.assertTrue(ps.getValue(name).isPresent());
			ps.delete(name);
			ps.save(file, CHARSET, TYPE);
		}{
			PropertySet ps = PropertySet.fromFile(file, CHARSET, TYPE);
			Assert.assertFalse(ps.getValue(name).isPresent());
		}
	}
	
	@Test
	public void testUpdateProperty() throws Exception{
		String name = "miProp", value = "miValor", finalValue = "miValorFinal";
		File file = File.createTempFile("testEmpty-", ".tmp");
		{
			PropertySet ps = new PropertySet(new Property(name, value));
			ps.save(file, CHARSET, TYPE);
		}{
			PropertySet ps = PropertySet.fromFile(file, CHARSET, TYPE);
			Assert.assertEquals(value, ps.getValue(name).get());
			ps.assign(new Property(name, finalValue));
			ps.save(file, CHARSET, TYPE);
		}{
			PropertySet ps = PropertySet.fromFile(file, CHARSET, TYPE);
			Assert.assertEquals(finalValue, ps.getValue(name).get());
		}
	}
	
	@Test
	public void testNewFile() throws Exception{
		String name = "miProp", value = "miValor";
		File file = new File(StandardSystemProperty.JAVA_IO_TMPDIR.value(), "testNew-" + System.currentTimeMillis() + ".tmp");
		writeProperties(file, new Property(name, value));
		PropertySet newPs = PropertySet.fromFile(file, CHARSET, TYPE);
		Assert.assertEquals(value, newPs.getValue(name).get());
	}
	
	private void writeProperties(File dest, Property...properties) throws IOException{
		PropertySet ps = new PropertySet(properties);
		ps.save(dest, CHARSET, TYPE);
	}
	
	@Test
	public void testEmptyFile() throws Exception{
		String name = "miProp", value = "miValor";
		File file = File.createTempFile("testEmpty-", ".tmp");
		writeProperties(file, new Property(name, value));
		PropertySet newPs = PropertySet.fromFile(file, CHARSET, TYPE);
		Assert.assertEquals(value, newPs.getValue(name).get());
	}

	private static final Charset CHARSET = Charsets.UTF_8;
	private static final PropertyType TYPE = PropertyType.EQUAL;	
	
}
