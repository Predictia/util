package es.predictia.util.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Files;

public class StorageServiceTest {

	@Test
	public void testStore() throws Exception{
		StorageService storageService = createStorageService();
		File input = createTestFile();
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
	
	private File createTestFile() throws IOException{
		File input = File.createTempFile("testStore-", ".txt");
		FileWriter writer = new FileWriter(input);
		writer.write("Copy file test");
		writer.close();
		return input;
	}
	
	@Test
	public void testDuplicateStore() throws Exception{
		StorageService storageService = createStorageService();
		File input = createTestFile();
		File output1 = storageService.storeFile(input);
		File output2 = storageService.storeFile(input);
		try{
			Assert.assertFalse(output1.equals(output2));
		}finally{
			output1.delete();
			output2.delete();
			input.delete();
		}
	}
	
	@Test
	public void testNoDepthStore() throws Exception{
		StorageService storageService = createStorageService(0, 0);
		File input = createTestFile();
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalDepth() throws Exception{
		createStorageService(-1, 2);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalLength() throws Exception{
		createStorageService(2, 0);
	}
	
	private StorageService createStorageService() throws Exception{
		return createStorageService(null, null);
	}
	
	private StorageService createStorageService(Integer depth, Integer length) throws Exception{
		StorageServiceImpl ss = new StorageServiceImpl();
		if(length != null){
			ss.setDirLength(length);
		}
		if(depth != null){
			ss.setDirLevels(depth);
		}
		ss.setBaseFolder(Files.createTempDir().getAbsolutePath());
		ss.init();
		return ss;
	}
	
}
