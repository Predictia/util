package es.predictia.util.parameter;

/**
 * @author Max
 *
 */
public interface Parameter {

	/**
	 * @return nombre del parametro del job
	 */
	public String getName();

	/**
	 * @return tipo de parametro, ha de ser uno de los soportados por batch (String, Date, Long, Double)
	 */
	public Class<?> getType();

	/**
	 * @return valor por defecto. Si es nulo es un parametro requerido
	 */
	public Object getDefaultValue();
		
}
