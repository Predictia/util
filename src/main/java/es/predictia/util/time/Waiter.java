package es.predictia.util.time;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Waiter {

	public static void sleepWithTimeOut(final long sleepInterval, final AwakeningCondition condition, long timeOut, TimeUnit timeOutUnit) throws InterruptedException, ExecutionException{
		if(TimeUnit.MILLISECONDS.convert(timeOut, timeOutUnit) < sleepInterval){
			throw new IllegalArgumentException("Sleep interval should be greather than timeout");
		}
		Throwable e = Timeouts.callWithTimeOut(new Callable<Throwable>() {
			@Override
			public Throwable call() throws Exception {
				try{
					do{
						Thread.sleep(sleepInterval);
					}while(!condition.wakeUp());
				}catch(Throwable e){
					LOGGER.info("Exception while waiting for process: " + e.getMessage());
					return e;
				}
				return null;
			}
		}, timeOut, timeOutUnit);
		if(e instanceof InterruptedException){
			throw (InterruptedException) e;
		}else if(e != null){
			throw new ExecutionException(e);
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Waiter.class);

	
}
