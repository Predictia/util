package es.predictia.util.parser;

import java.io.BufferedReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import es.predictia.util.Functions;

/** Parse beans from text data with a header like: <br>
 * #!class es.predictia.ehealth.model.User <br>
 * #!properties UNAME=username;PASSWORD=password;MAIL=email;ACTIVE=active <br>
 * #!data U_ID;UNAME;PASSWORD;MAIL;ACTIVE;PADRE_ID
 * @author Max
 *
 */
public class NodeParserHelper {

	private final BufferedReader dataReader;
	private final static String HEADER_PREFIX = "#!";
	private final static String HEADER_CLASS_PREFIX = "class";
	private final static String HEADER_PROPERTIES_PREFIX = "properties";
	private final static String HEADER_DATA_PREFIX = "data";
	private final static String HEADER_SEPARATOR = " ";
	private final static String HEADER_EQUAL = "=";
	private final static String COMMENT = "#";
	private final static String SEPARATOR = ";";
	
	public NodeParserHelper(BufferedReader dataReader) throws Exception{
		this.dataReader = dataReader;
		this.converters = new LinkedHashMap<Class<?>, Function<String,?>>();
		this.converters.put(int.class, Functions.stringToIntegerConverter);
		this.converters.put(Integer.class, Functions.stringToIntegerConverter);
		this.converters.put(long.class, Functions.stringToLongConverter);
		this.converters.put(Long.class, Functions.stringToLongConverter	);
		this.converters.put(float.class, Functions.stringToFloatConverter);
		this.converters.put(Float.class, Functions.stringToFloatConverter);
		this.converters.put(double.class, Functions.stringToDoubleConverter);
		this.converters.put(Double.class, Functions.stringToDoubleConverter);
		this.converters.put(boolean.class, Functions.stringToBooleanConverter);
		this.converters.put(Boolean.class, Functions.stringToBooleanConverter);
	}
	
	private List<Object> parse() throws Exception{
		
		List<Object> parsedBeans = new ArrayList<Object>();
		
		String className = null;
		Map<String, String> propertyAssociation = new HashMap<String, String>();
		List<String> dataHeader = new ArrayList<String>();
		
		String queryHeaderClass = HEADER_CLASS_PREFIX + HEADER_SEPARATOR;
		String queryHeaderProperties = HEADER_PROPERTIES_PREFIX + HEADER_SEPARATOR;
		String queryHeaderData = HEADER_DATA_PREFIX + HEADER_SEPARATOR;
		
		String line = null;
		while((line = dataReader.readLine()) != null){
			line = line.trim();
			if(line.isEmpty()) continue;
			if((!line.startsWith(HEADER_PREFIX)) && line.startsWith(COMMENT)) continue;
			if(line.startsWith(HEADER_PREFIX)){
				if(line.contains(queryHeaderClass)){
					// #!class es.predictia.ehealth.model.User
					className = line.substring(line.indexOf(queryHeaderClass) + queryHeaderClass.length()).trim();
				}else if(line.contains(queryHeaderProperties)){
					// #!properties UNAME=username;PASSWORD=password;MAIL=email;ACTIVE=active
					String propertiesString = line.substring(line.indexOf(queryHeaderProperties) + queryHeaderProperties.length()).trim();
					Splitter equalSpitter = Splitter.on(HEADER_EQUAL).trimResults().omitEmptyStrings();
					for(String assoc : Splitter.on(SEPARATOR).trimResults().omitEmptyStrings().split(propertiesString)){
						List<String> assocList = Lists.newArrayList(equalSpitter.split(assoc));
						if(assocList.size() == 2){
							propertyAssociation.put(assocList.get(0), assocList.get(1));
						}
					}
				}else if(line.contains(queryHeaderData)){
					// #!data U_ID;UNAME;PASSWORD;MAIL;ACTIVE;PADRE_ID
					String propertiesString = line.substring(line.indexOf(queryHeaderData) + queryHeaderData.length()).trim();
					dataHeader.addAll(Lists.newArrayList(Splitter.on(SEPARATOR).trimResults().omitEmptyStrings().split(propertiesString)));
				}
			}
			else break; // end of header
		}
		
		Class<?> clazz = Class.forName(className);
		
		do{
			line = !StringUtils.isEmpty(line) ? line.trim() : "";
			if(line.isEmpty()) continue;
			if(line.startsWith(COMMENT)) continue;
			List<String> lineData = getLineData(line);
			Constructor<?> con = clazz.getConstructor();
			Object parsedElement = con.newInstance();
			int idx = 0;
			for(String dataHeaderEl : dataHeader){
				if(propertyAssociation.containsKey(dataHeaderEl)){
					String property = propertyAssociation.get(dataHeaderEl);
					Class<?> propertyType = PropertyUtils.getPropertyType(parsedElement, property);
					if(idx >= lineData.size()) break;
					String value = lineData.get(idx);
					if(value != null){
						if(!value.trim().isEmpty()){
							Object parsedValue = null;
							if(propertyType.equals(String.class)){
								parsedValue = value;
							}else{
								for(Map.Entry<Class<?>, Function<String, ?>> converterEntry : converters.entrySet()){
									if(propertyType.equals(converterEntry.getKey())){
										parsedValue = converterEntry.getValue().apply(value);
										break;
									}
								}
							}
							if(parsedValue != null){
								PropertyUtils.setProperty(parsedElement, property, parsedValue);
							}
						}
					}
				}
				idx++;
			}
			
			parsedBeans.add(parsedElement);
			
		}
		while((line = dataReader.readLine()) != null);
		
		return parsedBeans;
	}

	private final Map<Class<?>, Function<String, ?>> converters;
	
	public <T> void registerConverter(Class<T> clazz, Function<String, T> converter){
		converters.put(clazz, converter);
	}
	
	private List<Object> parsedBeans;
	
	public List<?> getParsedBeans() {
		if(parsedBeans == null){
			try {
				parsedBeans = parse();
			} catch (Exception e) {
				LoggerFactory.getLogger(getClass()).warn("Excepcion "+e.getClass()+" al parsear fichero: " + e.getMessage());
			}
		}
		return parsedBeans;
	}

	static List<String> getLineData(String line){
		List<String> parsedString = new ArrayList<String>();
		String token="";
		boolean endOfToken=false;
		for(int i=0;i<line.length();i++){
			char letter = line.charAt(i);
			if(letter!=';' || (letter==';' && endOfToken)){
				if(letter=='\"' && !endOfToken){
					endOfToken=true;
				}else if(letter=='\"' && endOfToken){
					endOfToken=false;
				}else if(letter=='\\'){
					i=i+1;
					letter = line.charAt(i);
					token=token+letter;
				}else{//letra normal
					token=token+letter;
				}
			}else{//;
				parsedString.add(token.trim());
				token="";
			}
		}
		parsedString.add(token.trim());
		return parsedString;
	}
}
