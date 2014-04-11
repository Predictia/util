package es.predictia.util;


import static es.predictia.util.PropertyDefinitions.updateProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

import es.predictia.util.PropertyDefinitions.Property;

public class Streams {

	private Streams(){}
	
	public static void writeToFileWithProperties(InputStream is, File destination, Charset charset, Property... properties) throws IOException{
		InputStreamReader isr = new InputStreamReader(is);
		try{
			writeLinesToFile(updateProperties(isr, Lists.newArrayList(properties)), destination, charset);
		}finally{
			isr.close();
		}
	}
	
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
