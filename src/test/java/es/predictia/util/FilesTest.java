package es.predictia.util;

import org.junit.Assert;
import org.junit.Test;

public class FilesTest {

	@Test
	public void testFileNamePath() throws Exception{
		Assert.assertEquals("index.php", Files.getLocationFileName("http://www.test.domain/index.php"));
		Assert.assertEquals("file.ext", Files.getLocationFileName("/tmp/folder/file.ext"));
		Assert.assertEquals("file", Files.getLocationFileName("/tmp/folder/file"));
		Assert.assertEquals("file.ext", Files.getLocationFileName("c:\\Temp\\folder\\file.ext"));
		Assert.assertEquals("file", Files.getLocationFileName("c:\\Temp\\folder\\file"));
	}
	
}
