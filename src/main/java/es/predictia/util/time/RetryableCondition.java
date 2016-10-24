package es.predictia.util.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

import es.predictia.util.backoff.ObjectFactory;

public class RetryableCondition<T> implements AwakeningCondition {

	public final ObjectFactory<T> objectFactory;
	
	private final Predicate<T> validObject;
	
	public RetryableCondition(ObjectFactory<T> objectFactory, Predicate<T> validObject) {
		super();
		this.objectFactory = objectFactory;
		this.validObject = validObject;
	}

	@Override
	public boolean wakeUp() {
		try{
			T input = objectFactory.getObject();
			return validObject.apply(input);
		}catch(Exception e){
			LOGGER.debug("Error checking for condition: {}", e);
			return false;
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RetryableCondition.class);

}
