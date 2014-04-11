package es.predictia.util;

import static com.google.common.io.Files.copy;
import static es.predictia.util.Streams.writeLinesToFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

/**
 * Clase lectura y escritura para ficheros de propiedades, que contienen
 * propiedades y valores separados por un signo de igualdad (=)
 * 
 * @author Max
 * 
 */
public class PropertyDefinitions {

	private PropertyDefinitions(){}
	
	public static class Property{
		private final String name, value;
		public Property(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
	}
	
	/**
	 * @throws NoSuchElementException
	 * @throws IOException 
	 */
	public static String findPropertyValue(String line, String propertyName) throws NoSuchElementException, IOException{
		StringReader reader = new StringReader(line);
		try{
			return findPropertyValue(reader, propertyName);
		}finally{
			reader.close();
		}		
	}
	
	/**
	 * @throws NoSuchElementException
	 * @throws IOException 
	 */
	public static String findPropertyValue(Readable readable, String propertyName) throws NoSuchElementException, IOException{
		for(String line : CharStreams.readLines(readable)){
			try{
				return findLinePropertyValue(line, propertyName);
			}catch(NoSuchElementException e){
				LOGGER.trace("Property " + propertyName + " not found in line " + line);
			}
		}
		throw new NoSuchElementException("Property " + propertyName + " not found");
	}
	
	public static void updateFileWithProperties(File destination, Charset charset, Property... properties) throws IOException{
		LOGGER.info("Updating properties from file: " + destination);
		File tmpFile = File.createTempFile("temp-", ".properties");
		copy(destination, tmpFile);
		FileReader reader = new FileReader(tmpFile);
		try{
			writeLinesToFile(updateProperties(reader, Lists.newArrayList(properties)), destination, charset);
		}finally{
			reader.close();
			tmpFile.delete();
		}
	}
	
	public static List<String> updateProperties(Readable readable, List<Property> properties) throws IOException{
		List<String> processedLines = new ArrayList<String>();
		for(String line : CharStreams.readLines(readable)){
			String processedLine = line;
			for(Property property : properties){
				processedLine = processLine(processedLine, property.name, property.value);
			}
			processedLines.add(processedLine);
		}
		return processedLines;
	}
	
	static String processLine(String line, String propertyName, String propertyValue){
		try{
			String value = findLinePropertyValue(line, propertyName);
			return replaceFirstFrom(line, line.indexOf("="), value, propertyValue);
		}catch(NoSuchElementException e){
			return line;
		}
	}
	
	static String findLinePropertyValue(String line, String propertyName){
		List<String> lineComments = Lists.newArrayList(COMMENT_SPLITTER.split(line));
		String lineInput = lineComments.isEmpty() ? line : lineComments.get(0);
		List<String> lineElements = Lists.newArrayList(EQUAL_SPLITTER.split(lineInput));
		if(lineElements.size() > 1){
			String property = lineElements.get(0);
			if(propertyName.equals(property)){
				String value = lineElements.get(1);
				if((value.length() > 1) && isQuoute(value.charAt(0))){
					int lastIndex = value.lastIndexOf(value.charAt(0));
					if(lastIndex > 0){
						value = value.substring(1, lastIndex);
					}
				}
				return value;
			}
		}
		throw new NoSuchElementException();
	}
	
	private static boolean isQuoute(char chararacter){
		return (("'".charAt(0) == chararacter) || ("\"".charAt(0) == chararacter));
	}
	
	private static String replaceFirstFrom(String str, int from, String target, String replacement){
	    String prefix = str.substring(0, from);
	    String rest = str.substring(from);
	    rest = rest.replaceFirst(Pattern.quote(target), replacement);
	    return prefix+rest;
	}
	
	private static final Splitter EQUAL_SPLITTER = Splitter.on("=").trimResults().omitEmptyStrings();
	private static final Splitter COMMENT_SPLITTER = Splitter.on("#").trimResults();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyDefinitions.class);
	
}
