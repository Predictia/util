package es.predictia.util;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class FileLineSplitterTest {

	private static File computeTestDataRoot(Class<?> anyTestClass) {
		final String clsUri = anyTestClass.getName().replace('.', '/')+ ".class";
		final URL url = anyTestClass.getClassLoader().getResource(clsUri);
		final File root = Files.getFile(url);
		return root.getParentFile();		
	}
	
	@Ignore
	@Test
	public void fileLineSplitterTest() throws Exception{
		File dir = computeTestDataRoot(FileLineSplitterTest.class);
		FileLineSplitter fls = new FileLineSplitter(new File(dir, "test.txt"), dir, 10000);
		List<File> out = fls.getOutFiles();
		for(File file : out){
			System.out.println("Out file: " + file);
		}
		
	}
	
}
