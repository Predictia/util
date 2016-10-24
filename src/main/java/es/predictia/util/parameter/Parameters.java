package es.predictia.util.parameter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.primitives.Doubles;

/** Clase de ayuda al acceso del conjunto de parametros de un job
 * @author Max
 *
 */
public class Parameters implements Serializable {
	
	private final Map<String, Object> parameters;

	public Parameters(Map<String, Object> parameters) {
		super();
		this.parameters = new HashMap<String, Object>(parameters);
	}
	
	/**
	 * @return new map with all parameters
	 */
	public Map<String, Object> asMap() {
		return new HashMap<String, Object>(parameters);
	}

	public Long longOrDefault(Parameter param){
		return optionalLong(param).or((Long) param.getDefaultValue());
	}
	
	public Optional<Long> optionalLong(Parameter param){
		Object paramValue = this.parameters.get(param.getName());
		if(paramValue instanceof Long){
			return Optional.of((Long) paramValue);
		}else{
			return Optional.absent();
		}
	}	
	
	public String mandatoryString(Parameter param) throws IllegalStateException {
		Optional<String> od = optionalString(param);
		if(od.isPresent()){
			return od.get();
		}else{
			throw new IllegalStateException(param  + " is required");
		}
	}
	
	public Optional<String> optionalString(Parameter param){
		Object paramValue = this.parameters.get(param.getName());
		if(paramValue instanceof String){
			return Optional.fromNullable(StringUtils.trimToNull((String) paramValue));
		}else{
			return Optional.absent();
		}
	}
	
	public List<String> commaSeparatedStrings(Parameter param){
		Object paramValue = this.parameters.get(param.getName());
		if(paramValue instanceof String){
			return COMMA_SPLITTER.splitToList((String) paramValue);
		}
		return Collections.emptyList();
	}
	
	private static final transient Splitter COMMA_SPLITTER = Splitter.on(",").trimResults().omitEmptyStrings();
	
	public List<Double> commaSeparatedDoubles(Parameter param){
		Object paramValue = this.parameters.get(param.getName());
		if(paramValue instanceof String){
			return parseCommaSeparatedDoubles((String) paramValue);
		}else{
			return Collections.emptyList();
		}
	}
	
	private static List<Double> parseCommaSeparatedDoubles(String str){
		return FluentIterable
			.from(COMMA_SPLITTER.split(str))
			.transform(new Function<String, Double>() {
				@Override
				public Double apply(String longStr) {
					return Doubles.tryParse(longStr);
				}
			})
			.filter(Predicates.notNull())
			.toList();
	}
	
	public List<Double> commaSeparatedDoublesOrDefault(Parameter param){
		List<Double> result = commaSeparatedDoubles(param);
		if(result.isEmpty()){
			return parseCommaSeparatedDoubles((String) param.getDefaultValue());
		}else{
			return result;
		}
	}
	
	public Optional<Double> optionalDouble(Parameter param){
		Object paramValue = this.parameters.get(param.getName());
		if(paramValue instanceof Double){
			return Optional.of((Double) paramValue);
		}else{
			return Optional.absent();
		}
	}
	
	public Double mandatoryDouble(Parameter param) throws IllegalStateException {
		Optional<Double> od = optionalDouble(param);
		if(od.isPresent()){
			return od.get();
		}else{
			throw new IllegalStateException(param  + " is required");
		}
	}
	
	public Optional<Date> optionalDate(Parameter param){
		Object paramValue = this.parameters.get(param.getName());
		if(paramValue instanceof Date){
			return Optional.of((Date) paramValue);
		}else{
			return Optional.absent();
		}
	}
	
	public Date mandatoryDate(Parameter param) throws IllegalStateException {
		Optional<Date> od = optionalDate(param);
		if(od.isPresent()){
			return od.get();
		}else{
			throw new IllegalStateException(param  + " is required");
		}
	}
	
	public List<String> spaceSeparatedStrings(Parameter param){
		Object paramValue = this.parameters.get(param.getName());
		if(paramValue instanceof String){
			return SpaceSplitter.split((String) paramValue);
		}
		return Collections.emptyList();
	}
	
	public String stringOrDefault(Parameter param){
		Optional<String> optValue = Optional.absent();
		Object paramValue = this.parameters.get(param.getName());
		if(paramValue instanceof String){
			optValue = Optional.fromNullable(StringUtils.trimToNull((String) paramValue));
		}
		return optValue.or((String) param.getDefaultValue());
	}
	
	public boolean booleanOrDefault(Parameter param){
		return parseBoolean(this.parameters.get(param.getName()))
			.or(parseBoolean(param.getDefaultValue()))
			.get();
	}
	
	private static Optional<Boolean> parseBoolean(Object value){
		if(value instanceof Boolean){
			return Optional.of((Boolean) value);
		}else if(value instanceof String){
			return Optional.of(Boolean.valueOf((String) value));
		}else{
			return Optional.absent();
		}
	}
	
	public Map<String, String> stringSubset(String parameterPrefix) {
		Map<String, String> conf = new HashMap<String, String>();
		for(Map.Entry<String, Object> jobParameterEntry : parameters.entrySet()){
			if(jobParameterEntry.getKey().startsWith(parameterPrefix)){
				conf.put(
					jobParameterEntry.getKey().substring(parameterPrefix.length()), 
					objectToStr(jobParameterEntry.getValue())
				);
			}
		}
		return conf;
	}
	
	private static String objectToStr(Object o){
		return o != null ? o.toString() : null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Parameters other = (Parameters) obj;
		if (parameters == null) {
			if (other.parameters != null) return false;
		} else if (!parameters.equals(other.parameters)) return false;
		return true;
	}

	public static Builder builder(){
		return new Builder();
	}
	
	public static class Builder {
		
		private final Map<String, Object> parameters = new HashMap<>();
		
		public Builder value(String name, Object value){
			parameters.put(name, value);
			return this;
		}
		
		public Builder value(Parameter parameter, Object value){
			if(value != null){
				if(!parameter.getType().isAssignableFrom(value.getClass())){
					throw new IllegalArgumentException("Expecting parameter " + parameter.getName() + " with type " + parameter.getType());
				}
			}
			parameters.put(parameter.getName(), value);
			return this;
		}
		
		public Parameters build(){
			return new Parameters(parameters);
		}
		
	}
	
	private static final long serialVersionUID = 6931307258686649321L;
	
}
