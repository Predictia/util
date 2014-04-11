package es.predictia.util.backoff;

public interface ObjectFactory<T> {

	public T getObject() throws Exception;
	
}
