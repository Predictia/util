package es.predictia.util.property;

import static com.google.common.io.Files.copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FileUpdater {

	private FileUpdater(){}
	
	public interface LinesProcessor {
		List<String> newLines(Reader reader) throws IOException; 
	}
	
	public static void updateFile(File destination, Charset charset, LinesProcessor linesProcessor) throws IOException {
		if(destination.exists()){
			LOGGER.info("Updating file: " + destination);
		}else{
			destination.createNewFile();
			LOGGER.info("Writing to file: " + destination);
		}
		File tmpFile = File.createTempFile("temp-", ".properties");
		copy(destination, tmpFile);
		try(Reader reader = new InputStreamReader(new FileInputStream(tmpFile), charset)){
			writeLinesToFile(linesProcessor.newLines(reader), destination, charset);
		}finally{
			tmpFile.delete();
		}
	}
	
	static void writeLinesToFile(List<String> lines, File file, Charset charset) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, charset.name()));
		try{
			for(String line : lines){
				pw.println(line);
			}
			pw.flush();
		}finally{
			fos.close();
			pw.close();
		}
	}
	
	private static final transient Logger LOGGER = LoggerFactory.getLogger(FileUpdater.class);
	
}