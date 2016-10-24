package es.predictia.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class Streams {

	private Streams(){}
	
	static void writeLinesToFile(List<String> lines, File file, Charset charset) throws IOException{
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
	
	public static void writeToFile(InputStream is, File destination) throws IOException{
		FileOutputStream fos = new FileOutputStream(destination);
		try{
			IOUtils.copy(is, fos);
		}finally{
			fos.close();
		}
	}
	
}
