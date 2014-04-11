package es.predictia.util.backoff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackOffs {

	private BackOffs(){}
	
	public static <T> T getWithBackOff(ObjectFactory<T> factory, BackOff backOffDefinition) throws ExhaustedRetryException{
		int numberOfRetrys = 0;
		int maxNumberOfRetrys = backOffDefinition.getMaxNumberOfRetrys();
		Throwable lastThrowable = null;
		while(true){
			if(numberOfRetrys < maxNumberOfRetrys){
				try{
					return factory.getObject();
				}catch (Throwable e) {
					lastThrowable = e;
					if(numberOfRetrys++ < maxNumberOfRetrys){
						long sleepTime = getSleepTime(backOffDefinition, numberOfRetrys);
						logger.debug(e.getMessage() + ". Backing off for "+sleepTime+", retry " + numberOfRetrys+"/"+maxNumberOfRetrys);
						tryToSleep(sleepTime);
					}
				}
			}else{
				throw new ExhaustedRetryException(backOffDefinition, lastThrowable);
			}
		}
	}
	
	public static void doWithBackOff(Runnable runnable, BackOff backOffDefinition) throws ExhaustedRetryException{
		int numberOfRetrys = 0;
		boolean success = false;
		int maxNumberOfRetrys = backOffDefinition.getMaxNumberOfRetrys();
		Throwable lastThrowable = null;
		do{
			if(numberOfRetrys < maxNumberOfRetrys){
				try{
					runnable.run();
					success = true;
				}catch (Throwable e) {
					lastThrowable = e;
					if(numberOfRetrys++ < maxNumberOfRetrys){
						long sleepTime = getSleepTime(backOffDefinition, numberOfRetrys);
						logger.debug(e.getMessage() + ". Backing off for "+sleepTime+", retry " + numberOfRetrys+"/"+maxNumberOfRetrys);
						tryToSleep(sleepTime);
					}
				}
			}else{
				throw new ExhaustedRetryException(backOffDefinition, lastThrowable);
			}
		}while(!success);
	}

	private static long getSleepTime(BackOff backOffDefinition, int numberOfRetry){
		return Double.valueOf(backOffDefinition.getBackOffTime() * Math.pow(2, numberOfRetry - 1)).longValue();
	}
	
	private static void tryToSleep(long milis){
		try{
			Thread.sleep(milis);
		}catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BackOffs.class);
	
}
