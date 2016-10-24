package es.predictia.util.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

public enum PropertyType {

	EQUAL("="), COLON(":");
	
	private final String separator;

	private PropertyType(String separator) {
		this.separator = separator;
	}
	
	Collection<Property> parseProperties(File file, Charset charset) throws IOException {
		try(Reader reader = new InputStreamReader(new FileInputStream(file), charset)){
			return parseProperties(reader);
		}
	}
	
	Collection<Property> parseProperties(Readable readable) throws IOException {
		List<Property> properties = new ArrayList<Property>();
		for(String line : CharStreams.readLines(readable)){
			Optional<Property> property = parsePropertyValue(line);
			if(property.isPresent()){
				properties.add(property.get());
			}
		}
		return properties;
	}
	
	Optional<Property> parsePropertyValue(String line){
		List<String> lineComments = Lists.newArrayList(COMMENT_SPLITTER.split(line));
		String lineInput = lineComments.isEmpty() ? line : lineComments.get(0);
		int idx = lineInput.indexOf(separator);
		if((idx > 0) && (idx < lineInput.length())){
			String property = lineInput.substring(0, idx).trim();
			String value = lineInput.substring((idx + 1)).trim();
			if((value.length() > 1) && isQuoute(value.charAt(0))){
				int lastIndex = value.lastIndexOf(value.charAt(0));
				if(lastIndex > 0){
					value = value.substring(1, lastIndex);
				}
			}
			return Optional.of(new Property(property, value));
		}else{
			return Optional.absent();
		}
	}
	
	String createLine(Property property){
		return property.getName() + separator + property.getValue();
	}
	
	String updateLinePropertyValue(String line, String propertyName, String propertyValue){
		try{
			String oldValue = findLinePropertyValue(line, propertyName);
			if(oldValue != null && oldValue.equals(propertyValue)){
				LOGGER.debug("Leaving unchanged {}", propertyName);
				return line;
			}
			LOGGER.debug("Setting {}={} (was {})", propertyName, propertyValue, oldValue);
			try{
				return replaceFirstFrom(line, line.indexOf(separator), oldValue, propertyValue);
			}catch(Exception e){
				LOGGER.debug("Unable to replace inline, creating new line...");
				return propertyName + separator + propertyValue;
			}
		}catch(NoSuchElementException e){
			return line;
		}
	}
	
	private String findLinePropertyValue(String line, String propertyName) throws NoSuchElementException{
		Optional<Property> property = parsePropertyValue(line);
		if(property.isPresent()){
			if(propertyName.equals(property.get().getName())){
				return property.get().getValue();
			}
		}
		throw new NoSuchElementException();
	}
	
	private static String replaceFirstFrom(String str, int from, String target, String replacement){
	    String prefix = str.substring(0, from);
	    String rest = str.substring(from);
	    rest = rest.replaceFirst(Pattern.quote(target), replacement);
	    return prefix+rest;
	}
	
	private static boolean isQuoute(char chararacter){
		return (("'".charAt(0) == chararacter) || ("\"".charAt(0) == chararacter));
	}
	
	private static final transient Splitter COMMENT_SPLITTER = Splitter.on("#").trimResults();
	private static final transient Logger LOGGER = LoggerFactory.getLogger(PropertyType.class);

}
