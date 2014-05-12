package es.predictia.util;

import static com.google.common.io.Files.copy;
import static es.predictia.util.Streams.writeLinesToFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FileUpdater {

	private FileUpdater(){}
	
	public interface LinesProcessor{
		List<String> newLines(Reader fileReader) throws IOException; 
	}
	
	public static void updateFile(File destination, Charset charset, LinesProcessor linesProcessor) throws IOException{
		LOGGER.info("Updating file: " + destination);
		File tmpFile = File.createTempFile("temp-", ".properties");
		copy(destination, tmpFile);
		FileReader reader = new FileReader(tmpFile);
		try{
			writeLinesToFile(linesProcessor.newLines(reader), destination, charset);
		}finally{
			reader.close();
			tmpFile.delete();
		}
	}
	
	private static final transient Logger LOGGER = LoggerFactory.getLogger(FileUpdater.class);
	
}
