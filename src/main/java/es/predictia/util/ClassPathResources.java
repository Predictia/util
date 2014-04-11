package es.predictia.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class ClassPathResources {

	private ClassPathResources(){}
	
	public static File toTemporalFile(String resource, String tempFilePrefix, String tempFileSufix) throws IOException{
		File resultFile = File.createTempFile(tempFilePrefix, tempFileSufix);
		InputStream is = ClassPathResources.class.getResourceAsStream(resource);
		FileOutputStream fos = new FileOutputStream(resultFile);
		try{
			IOUtils.copy(is, fos);
		}finally{
			is.close();
			fos.close();
		}
		return resultFile;
	}
	
}
