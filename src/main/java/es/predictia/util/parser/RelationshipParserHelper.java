package es.predictia.util.parser;

import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import es.predictia.util.Functions;

/** Parse relationship between beans from text data with a header like: <br>
 * #!parentChildProperty roles <br>
 * #!parentProperties UNAME=username <br>
 * #!childProperties RNAME=name <br>
 * #!data RNAME;UNAME
 * @author Max
 *
 */
public class RelationshipParserHelper {

	private final BufferedReader dataReader;
	
	private final static String HEADER_PREFIX = "#!";
	private final static String HEADER_PARENT_CHILD_PROPERTY_PREFIX = "parentChildProperty";
	private final static String HEADER_PARENT_PROPERTIES_PREFIX = "parentProperties";
	private final static String HEADER_CHILD_PROPERTIES_PREFIX = "childProperties";
	private final static String HEADER_DATA_PREFIX = "data";
	private final static String HEADER_SEPARATOR = " ";
	private final static String HEADER_EQUAL = "=";
	private final static String SEPARATOR = ";";
	
	public RelationshipParserHelper(BufferedReader dataReader) throws Exception{
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
	
	public void fillRelationships(Iterable<?> parents, Iterable<?> children) throws Exception{
		
		String parentChildPropertyName = null;
		Map<String, String> parentPropertyAssociation = new HashMap<String, String>(), childPropertyAssociation = new HashMap<String, String>();
		List<String> dataHeader = new ArrayList<String>();
		
		String queryHeaderParentChildProperty = HEADER_PARENT_CHILD_PROPERTY_PREFIX + HEADER_SEPARATOR;
		String queryHeaderParentProperties = HEADER_PARENT_PROPERTIES_PREFIX + HEADER_SEPARATOR;
		String queryHeaderChildProperties = HEADER_CHILD_PROPERTIES_PREFIX + HEADER_SEPARATOR;
		String queryHeaderData = HEADER_DATA_PREFIX + HEADER_SEPARATOR;
		
		String line = null;
		while((line = dataReader.readLine()) != null){
			line = line.trim();
			if(line.length() == 0) continue;
			if(!line.startsWith(HEADER_PREFIX)) break; // end of header
			if(line.contains(queryHeaderParentChildProperty)){
				// #!parentChildProperty roles
				parentChildPropertyName = line.substring(line.indexOf(queryHeaderParentChildProperty) + queryHeaderParentChildProperty.length()).trim();
			}else if(line.contains(queryHeaderParentProperties)){
				// #!parentProperties UNAME=username
				String propertiesString = line.substring(line.indexOf(queryHeaderParentProperties) + queryHeaderParentProperties.length()).trim();
				Splitter equalSpitter = Splitter.on(HEADER_EQUAL).trimResults().omitEmptyStrings();
				for(String assoc : Splitter.on(SEPARATOR).trimResults().omitEmptyStrings().split(propertiesString)){
					List<String> assocList = Lists.newArrayList(equalSpitter.split(assoc));
					if(assocList.size() == 2){
						parentPropertyAssociation.put(assocList.get(0), assocList.get(1));
					}
				}
			}else if(line.contains(queryHeaderChildProperties)){
				// #!parentProperties UNAME=username
				String propertiesString = line.substring(line.indexOf(queryHeaderChildProperties) + queryHeaderChildProperties.length()).trim();
				Splitter equalSpitter = Splitter.on(HEADER_EQUAL).trimResults().omitEmptyStrings();
				for(String assoc : Splitter.on(SEPARATOR).trimResults().omitEmptyStrings().split(propertiesString)){
					List<String> assocList = Lists.newArrayList(equalSpitter.split(assoc));
					if(assocList.size() == 2){
						childPropertyAssociation.put(assocList.get(0), assocList.get(1));
					}
				}
			}else if(line.contains(queryHeaderData)){
				// #!data U_ID;UNAME;PASSWORD;MAIL;ACTIVE;PADRE_ID
				String propertiesString = line.substring(line.indexOf(queryHeaderData) + queryHeaderData.length()).trim();
				dataHeader.addAll(Lists.newArrayList(Splitter.on(SEPARATOR).trimResults().omitEmptyStrings().split(propertiesString)));
			}
		}
		
		Splitter lineSplitter = Splitter.on(SEPARATOR).trimResults();
		do{
			line = !StringUtils.isEmpty(line) ? line.trim() : "";
			if(line.isEmpty()) continue;
			List<String> lineData = Lists.newArrayList(lineSplitter.split(line));
			final Map<String, String> parentSerachProperties = new HashMap<String, String>();
			final Map<String, String> childSerachProperties = new HashMap<String, String>();
			int idx = 0;
			for(String property : dataHeader){
				if(parentPropertyAssociation.containsKey(property)){
					parentSerachProperties.put(parentPropertyAssociation.get(property), lineData.get(idx));
				}
				if(childPropertyAssociation.containsKey(property)){
					childSerachProperties.put(childPropertyAssociation.get(property), lineData.get(idx));
				}
				idx++;
			}
			
			Object parent = null, child = null;
			try{
				parent = Iterables.find(parents, new Predicate<Object>() {
					public boolean apply(Object arg0) {
						try {
							return isEquals(arg0, parentSerachProperties);
						} catch (Exception e) {
							logger.warn(e.getMessage());
							return false;
						}
					}
				});
			}catch (NoSuchElementException e) {
				logger.warn("Could not find parent with: " + parentSerachProperties + " (line:" + line + ")");
				continue;
			}
			try{
				child = Iterables.find(children, new Predicate<Object>() {
					public boolean apply(Object arg0) {
						try {
							return isEquals(arg0, childSerachProperties);
						} catch (Exception e) {
							logger.warn(e.getMessage());
							return false;
						}
					}
				});
			}catch (NoSuchElementException e) {
				logger.warn("Could not find child with: " + childSerachProperties + " (line:" + line + ")");
				continue;
			}
			
			@SuppressWarnings("unchecked") // la propiedad del padre donde se almacenan los hijos deberia ser una coleccion
			Collection<Object> propertyParentChildValue = (Collection<Object>) PropertyUtils.getProperty(parent, parentChildPropertyName);
			if(propertyParentChildValue == null){
				Class<?> propertyType = PropertyUtils.getPropertyType(parent, parentChildPropertyName);
				if(propertyType.equals(List.class)) propertyParentChildValue = new ArrayList<Object>();
				else{
					if(propertyType.equals(Set.class)) propertyParentChildValue = new HashSet<Object>();
				}
			}
			if(propertyParentChildValue != null){
				propertyParentChildValue.add(child);
			}
			PropertyUtils.setProperty(parent, parentChildPropertyName, propertyParentChildValue);
		}
		while((line = dataReader.readLine()) != null);
		
	}
	
	/** Compares object with some properties represented as strings
	 * @param object
	 * @param propertyValueCheck
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private boolean isEquals(Object object, Map<String, String> propertyValueCheck) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		for(Map.Entry<String, String> propertyValue : propertyValueCheck.entrySet()){
			Class<?> propertyType = PropertyUtils.getPropertyType(object, propertyValue.getKey());
			Object compareValue = null;
			if(propertyType.equals(String.class)){
				compareValue = propertyValue.getValue();
			}else{
				for(Map.Entry<Class<?>, Function<String, ?>> converterEntry : converters.entrySet()){
					if(propertyType.equals(converterEntry.getKey())){
						compareValue = converterEntry.getValue().apply(propertyValue.getValue());
						break;
					}
				}
			}
			Object value = PropertyUtils.getProperty(object, propertyValue.getKey());
			if(value != null){
				if(!value.equals(compareValue)) return false;
			}else if(compareValue != null) return false;
		}
		return true;
	}
	
	private final Map<Class<?>, Function<String, ?>> converters;
	
	public <T> void registerConverter(Class<T> clazz, Function<String, T> converter){
		converters.put(clazz, converter);
	}
	
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RelationshipParserHelper.class);
	
}
