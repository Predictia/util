package es.predictia.util.storage;

import java.io.File;
import java.io.FileWriter;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Files;

public class StorageServiceTest {

	@Test
	public void testStore() throws Exception{
		StorageService storageService = createStorageService();
		File input = File.createTempFile("testStore-", ".txt");
		FileWriter writer = new FileWriter(input);
		writer.write("Copy file test");
		writer.close();
		File output = storageService.storeFile(input);
		try{
			Assert.assertTrue(output.exists());
			String relative = storageService.relativeFilePath(output);
			Assert.assertEquals(output, storageService.expandFilePath(relative));
		}finally{
			output.delete();
			input.delete();
		}
	}
	
	private StorageService createStorageService() throws Exception{
		StorageServiceImpl ss = new StorageServiceImpl();
		ss.setBaseFolder(Files.createTempDir().getAbsolutePath());
		ss.init();
		return ss;
	}
	
}
