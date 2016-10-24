package es.predictia.util.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;

public class PropertySet {

	private final Set<Property> properties;
	
	public static PropertySet fromFile(File file, Charset charset, PropertyType type) throws IOException {
		return new PropertySet(type.parseProperties(file, charset));
	}
	
	/** Lee las propiedades a partir de un readable sin cerrar el recurso al terminar 
	 * @param readable
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public static PropertySet fromReadable(Readable readable, PropertyType type) throws IOException {
		return new PropertySet(type.parseProperties(readable));
	}
	
	public PropertySet(Property... properties) {
		this(properties != null ? Lists.newArrayList(properties) : new ArrayList<Property>());
	}
	
	public PropertySet(Collection<Property> properties) {
		super();
		this.properties = new LinkedHashSet<Property>(properties);
	}

	public Optional<Property> get(String name){
		return find(properties, name);
	}
	
	private static Optional<Property> find(Collection<Property> properties, String name){
		for(Property p : properties){
			if(name.equals(p.getName())){
				return Optional.of(p);
			}
		}
		return Optional.absent();
	}
	
	public Optional<String> getValue(String name){
		Optional<Property> op = get(name);
		if(op.isPresent()){
			return Optional.of(op.get().getValue());
		}
		return Optional.absent();
	}
	
	public Set<Property> get() {
		return new LinkedHashSet<Property>(properties);
	}
	
	public void delete(String... names){
		if(names == null) return;
		delete(Lists.newArrayList(names));
	}
	
	public void delete(final Collection<String> names){
		Iterables.removeIf(properties, new Predicate<Property>() {
			@Override
			public boolean apply(Property input) {
				boolean delete = names.contains(input.getName());
				if(delete) LOGGER.debug("Deleting {}", input.getName());
				return delete;
			}
		});
	}
	
	/** if a property exists, updates its value. If not, creates it. It does not delete any properties
	 * @param properties
	 */
	public void assign(Property... properties){
		if(properties == null) return;
		assign(Lists.newArrayList(properties));
	}
	
	/** if a property exists, updates its value. If not, creates it. It does not delete any properties
	 * @param properties
	 */
	public void assign(List<Property> properties){
		for(Property property : properties){
			Optional<Property> existingProperty  = get(property.getName());
			if(existingProperty.isPresent()){
				this.properties.remove(existingProperty.get());
				LOGGER.debug("Updating property {}", property.getName());
			}else{
				LOGGER.debug("Setting new property {}", property.getName());
			}
			this.properties.add(new Property(property.getName(), property.getValue()));
		}
	}

	public void update(Function<Property, Property> propertyTransform){
		List<Property> newProperties = FluentIterable
			.from(properties)
			.transform(propertyTransform)
			.toList();
		properties.clear();
		properties.addAll(newProperties);
	}
	
	public void save(File dest, Charset charset, PropertyType type) throws IOException {
		Set<Property> remainingProperties = Sets.newHashSet(properties);
		final List<String> processedLines = new ArrayList<String>();
		if(dest.exists()){
			try(Reader reader = new InputStreamReader(new FileInputStream(dest), charset)){		
				for(String line : CharStreams.readLines(reader)){
					Optional<Property> sourceProperty = type.parsePropertyValue(line);
					if(!sourceProperty.isPresent()){
						processedLines.add(line); // no era una propiedad, la dejo igual
						continue;
					}
					String sourcePropertyName = sourceProperty.get().getName();
					Optional<Property> newProperty = find(remainingProperties, sourcePropertyName);
					if(!newProperty.isPresent()){
						LOGGER.debug("Removing deleted property {}", sourcePropertyName);
						continue; // propiedad borrada
					}else{
						String processedLine = type.updateLinePropertyValue(line, sourcePropertyName, newProperty.get().getValue());
						processedLines.add(processedLine);
						remainingProperties.remove(newProperty.get());
					}
				}
			}
		}
		for(Property remainingProperty : remainingProperties){
			LOGGER.debug("Adding new property {}={}", remainingProperty.getName(), remainingProperty.getValue());
			processedLines.add(type.createLine(remainingProperty));
		}
		FileUpdater.updateFile(dest, charset, new FileUpdater.LinesProcessor() {
			@Override
			public List<String> newLines(Reader reader) throws IOException {
				return processedLines;
			}
		});
	}

	private static final transient Logger LOGGER = LoggerFactory.getLogger(PropertySet.class);
	
}
