package es.predictia.util;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
		
		public String getName() {
			return name;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Property other = (Property) obj;
			if (name == null) {
				if (other.name != null) return false;
			} else if (!name.equals(other.name)) return false;
			if (value == null) {
				if (other.value != null) return false;
			} else if (!value.equals(other.value)) return false;
			return true;
		}

		@Override
		public String toString() {
			return EQUAL_JOINER.join(name, value) ;
		}
		
	}
	
	/**
	 * @throws IOException 
	 */
	public static List<Property> findProperties(Readable readable) throws IOException{
		List<Property> properties = new ArrayList<PropertyDefinitions.Property>();
		for(String line : CharStreams.readLines(readable)){
			Optional<Property> property = parsePropertyValue(line);
			if(property.isPresent()){
				properties.add(property.get());
			}
		}
		return properties;
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
	
	public static void updateFileWithProperties(File destination, Charset charset, final Property... properties) throws IOException{
		FileUpdater.updateFile(destination, charset, new FileUpdater.LinesProcessor() {
			@Override
			public List<String> newLines(Reader reader) throws IOException {
				return updateProperties(reader, Lists.newArrayList(properties));
			}
		});
	}
	
	public static void updateFileWithProperties(File destination, Charset charset, final Function<Property, Property> propertyTransform) throws IOException{
		FileUpdater.updateFile(destination, charset, new FileUpdater.LinesProcessor() {
			@Override
			public List<String> newLines(Reader reader) throws IOException {
				return updateProperties(reader, propertyTransform);
			}
		});
	}
	
	public static List<String> updateProperties(Readable readable, Function<Property, Property> propertyTransform) throws IOException{
		List<String> processedLines = new ArrayList<String>();
		for(String line : CharStreams.readLines(readable)){
			String processedLine = line;
			Optional<Property> sourcePropertyOptional = parsePropertyValue(line);
			if(sourcePropertyOptional.isPresent()){
				Property sourceProperty = sourcePropertyOptional.get();
				Property newProperty = propertyTransform.apply(sourceProperty);
				if(!newProperty.equals(sourceProperty)){
					processedLine = updateLinePropertyValue(processedLine, sourceProperty.name, newProperty.value);
					processedLine = updateLinePropertyName(processedLine, sourceProperty.name, newProperty.name);
				}
			}
			processedLines.add(processedLine);
		}
		return processedLines;
	}
	
	public static List<String> updateProperties(Readable readable, List<Property> properties) throws IOException{
		Set<Property> processedProperties = new HashSet<PropertyDefinitions.Property>();
		List<String> processedLines = new ArrayList<String>();
		for(String line : CharStreams.readLines(readable)){
			String processedLine = line;
			for(Property property : properties){
				processedLine = updateLinePropertyValue(processedLine, property.name, property.value);
			}
			processedLines.add(processedLine);
			Optional<Property> processedProperty = parsePropertyValue(processedLine);
			if(processedProperty.isPresent()){
				processedProperties.add(processedProperty.get());
			}
		}
		Set<Property> remainingProperties = Sets.newHashSet(properties);
		remainingProperties.removeAll(processedProperties);
		for(Property remainingProperty : remainingProperties){
			processedLines.add(remainingProperty.toString());
		}
		return processedLines;
	}
	
	static String updateLinePropertyName(String line, String propertyName, String newPropertyName){
		if(propertyName.equals(newPropertyName)){
			return line;
		}else{
			return replaceFirstFrom(line, 0, propertyName, newPropertyName);
		}
	}
	
	static String updateLinePropertyValue(String line, String propertyName, String propertyValue){
		try{
			String value = findLinePropertyValue(line, propertyName);
			return replaceFirstFrom(line, line.indexOf("="), value, propertyValue);
		}catch(NoSuchElementException e){
			return line;
		}
	}
	
	static String findLinePropertyValue(String line, String propertyName) throws NoSuchElementException{
		Optional<Property> property = parsePropertyValue(line);
		if(property.isPresent()){
			if(propertyName.equals(property.get().getName())){
				return property.get().getValue();
			}
		}
		throw new NoSuchElementException();
	}
	
	static Optional<Property> parsePropertyValue(String line){
		List<String> lineComments = Lists.newArrayList(COMMENT_SPLITTER.split(line));
		String lineInput = lineComments.isEmpty() ? line : lineComments.get(0);
		List<String> lineElements = Lists.newArrayList(EQUAL_SPLITTER.split(lineInput));
		if(lineElements.size() > 1){
			String property = lineElements.get(0);
			String value = EQUAL_JOINER.join(lineElements.subList(1, lineElements.size()));
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
	
	private static boolean isQuoute(char chararacter){
		return (("'".charAt(0) == chararacter) || ("\"".charAt(0) == chararacter));
	}
	
	private static String replaceFirstFrom(String str, int from, String target, String replacement){
	    String prefix = str.substring(0, from);
	    String rest = str.substring(from);
	    rest = rest.replaceFirst(Pattern.quote(target), replacement);
	    return prefix+rest;
	}
	
	private static final transient Splitter EQUAL_SPLITTER = Splitter.on("=").trimResults().omitEmptyStrings();
	private static final transient Joiner EQUAL_JOINER = Joiner.on("=").skipNulls();
	private static final transient Splitter COMMENT_SPLITTER = Splitter.on("#").trimResults();
	private static final transient Logger LOGGER = LoggerFactory.getLogger(PropertyDefinitions.class);
	
}
