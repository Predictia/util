package es.predictia.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Clase que contiene informacion diversa. Internamente se almacena todo en un
 * String. <br>
 * <br>
 * Se puede construir sobre la marcha mediante los metodos setVariable* o
 * mediante un string compuesto con el siguiente formato:<br>
 * varName1=varValue1;<br>
 * varName2=varValue2;<br>
 * varListName=varListValue1,varListValue2 ;<br>
 * <br>
 * Los valores por defecto de los strings de separacion y asignacion se pueden
 * personalizar.
 * 
 * @author Max
 * 
 */
public class Metadata implements Serializable {

	public static final String DEFAULT_ASSIGN_CODE = "=";
	public static final String DEFAULT_LIST_SEPARATOR_CODE = ",";
	public static final String DEFAULT_VARIABLE_SEPARATOR_CODE = ";";
	
	private String metadata;
	
	public Metadata(){
		this.metadata = "";
	}
	
	/** Construye el objeto a partir de un contenido
	 * @param meta
	 */
	public Metadata(String meta){
		this.metadata = (meta == null) ? "" : meta;
	}
	
	public String getMetadata() {
		return metadata;
	}
	/**
	 * @param metadata Sobreescribe el contenido del objeto
	 */
	public void setMetadata(String metadata) {
		this.metadata = (metadata == null) ? "" : metadata;
	}

	public boolean isVariable(String variable){
		String tmp = this.getVariable(variable);
		return (!tmp.isEmpty());
	}
	
	public String getVariable(String variable, String defaultValue){
		String tmp = this.getVariable(variable);
    	return (!tmp.isEmpty()) ? tmp : defaultValue;
    }
	
	public <T> T getVariable(String name, Function<String, T> converter){
		return converter.apply(getVariable(name));
	}
	
	public <T> T getVariable(String name, T defaultValue, Function<String, T> converter){
		if(isVariable(name)){
			try{
				T el = converter.apply(getVariable(name));
				if(el != null){
					return el;
				}
			}catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}
		return defaultValue;
	}
	
	/** 
	 * @return Listado con las diferentes variables presentes en el metatadata
	 */
	public List<String> getAvailableVariables(){
		List<String> variables = new ArrayList<String>();
		for(String variableData : getVariableSplitter().split(getMetadata())){
			List<String> parsedVd = Lists.newArrayList(getAssignSplitter().split(variableData));
			if(parsedVd.size() > 1){
				variables.add(parsedVd.get(0));
			}
		}
		return variables;
	}
	
	/** Busca una variable de metadata con varios elementos en forma de lista
	 * @param variable
	 * @return Lista con los valores o lista vacia si se encontro
	 */
	public List<String> getVariableList(String variable){
		for(String variableData : getVariableSplitter().split(getMetadata())){
			List<String> parsedVd = Lists.newArrayList(getAssignSplitter().split(variableData));
			if(parsedVd.size() > 1){
				if(parsedVd.get(0).equals(variable)){
					return Lists.newArrayList(getListSplitter().split(parsedVd.get(1)));
				}
			}
		}
		return Collections.emptyList();
    }
	
	public <T> List<T> getVariableList(String name, Function<String, T> converter){
		List<T> resultados = new ArrayList<T>();
		for(String cada : getVariableList(name)){
			try{
				resultados.add(converter.apply(cada));
			}catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}
		return resultados;
	}
	
	/** Capaz de sacar contenidos de un string del estilo: variable1=valor1;variable2=valor2
	 * @param variable Nombre de la variable
	 * @return Valor de la variable si existe o String vacio.
	 */
	public String getVariable(String variable){
		for(String variableData : getVariableSplitter().split(getMetadata())){
			List<String> parsedVd = Lists.newArrayList(getAssignSplitter().split(variableData));
			if(parsedVd.size() > 1){
				if(parsedVd.get(0).equals(variable)){
					return parsedVd.get(1);
				}
			}
		}
		return "";
	}


	/** Inserta una nueva variable o actualiza su valor al indicado
	 * @param variable
	 * @param nuevoValor
	 * @return Metadata actualizado
	 */
	public String setVariable(String variable, String nuevoValor){
		StringBuilder metadataBuilder = new StringBuilder();
		String nuevaVariable = variable + assignCode + ((nuevoValor != null) ? nuevoValor : "");
		for(String variableData : getVariableSplitter().split(getMetadata())){
			List<String> parsedVd = Lists.newArrayList(getAssignSplitter().split(variableData));
			if(!parsedVd.isEmpty()){
				if(!parsedVd.get(0).equals(variable)){
					metadataBuilder.append(variableData);
					metadataBuilder.append(variableSeparatorCode);
				}
			}
		}
		metadataBuilder.append(nuevaVariable);
		metadataBuilder.append(variableSeparatorCode);
		this.metadata = metadataBuilder.toString();
		return getMetadata();			
	}
	
	public <T> String setVariable(String variable, T nuevoValor, Function<T, String> converter){
		return setVariable(variable, converter.apply(nuevoValor));
	}
	
	public <T> String setVariableList(String variable, List<T> nuevosValores, Function<T, String> converter){
		return setVariableList(variable, Lists.newArrayList(Iterables.transform(nuevosValores, converter)));
	}
	
	/** Inserta una nueva variable o actualiza su valor al indicado
	 * @param variable
	 * @param nuevoValor
	 * @return Metadata actualizado
	 */
	public String setVariableList(String variable, List<String> nuevosValores){
		if(nuevosValores != null){
			String nuevosValoresEscapados = getListJoiner().join(nuevosValores);
			return this.setVariable(variable, nuevosValoresEscapados);			
		}
		return getMetadata();
	}
	
	public String toString(){
		return getMetadata();
	}

	private String assignCode = DEFAULT_ASSIGN_CODE;
	private String listSeparatorCode = DEFAULT_LIST_SEPARATOR_CODE;
	private String variableSeparatorCode = DEFAULT_VARIABLE_SEPARATOR_CODE;
	
	public String getAssignCode() {
		return assignCode;
	}
	public void setAssignCode(String assignCode) {
		this.assignCode = assignCode;
	}
	public String getListSeparatorCode() {
		return listSeparatorCode;
	}
	public void setListSeparatorCode(String listSeparatorCode) {
		this.listSeparatorCode = listSeparatorCode;
	}
	public String getVariableSeparatorCode() {
		return variableSeparatorCode;
	}
	public void setVariableSeparatorCode(String variableSeparatorCode) {
		this.variableSeparatorCode = variableSeparatorCode;
	}

	private Joiner getListJoiner(){
		return Joiner.on(getListSeparatorCode()).skipNulls();
	}
	private Splitter getListSplitter(){
		return Splitter.on(getListSeparatorCode()).trimResults().omitEmptyStrings();
	}
	private Splitter getVariableSplitter(){
		return Splitter.on(getVariableSeparatorCode()).trimResults().omitEmptyStrings();
	}
	private Splitter getAssignSplitter(){
		return Splitter.on(getAssignCode()).trimResults().omitEmptyStrings();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignCode == null) ? 0 : assignCode.hashCode());
		result = prime * result + ((listSeparatorCode == null) ? 0 : listSeparatorCode.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((variableSeparatorCode == null) ? 0 : variableSeparatorCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Metadata other = (Metadata) obj;
		if (assignCode == null) {
			if (other.assignCode != null) return false;
		} else if (!assignCode.equals(other.assignCode)) return false;
		if (listSeparatorCode == null) {
			if (other.listSeparatorCode != null) return false;
		} else if (!listSeparatorCode.equals(other.listSeparatorCode)) return false;
		if (metadata == null) {
			if (other.metadata != null) return false;
		} else if (!metadata.equals(other.metadata)) return false;
		if (variableSeparatorCode == null) {
			if (other.variableSeparatorCode != null) return false;
		} else if (!variableSeparatorCode.equals(other.variableSeparatorCode)) return false;
		return true;
	}

	private static final Logger logger = LoggerFactory.getLogger(Metadata.class);
	private static final long serialVersionUID = 1296008644687400741L;
	
}
