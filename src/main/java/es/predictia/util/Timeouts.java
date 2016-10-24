package es.predictia.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timeouts {

	public interface AwakeningCondition {
		
		/** 
		 * @return true if sleep should end
		 * @throws Throwable if check fails, and stops sleeping  
		 */
		public boolean wakeUp() throws Throwable;
		
	}
	
	/** Sleeps checking for an {@link AwakeningCondition}, with a maximum sleeping time (timeOut)
	 * @param sleepInterval time to sleep (in millis) untill checking for {@link AwakeningCondition#wakeUp()}
	 * @param condition
	 * @param timeOut
	 * @param timeOutUnit
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
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
	
	/**
	 * @throws InterruptedException if timeout happened
	 * @throws ExecutionException 
	 */
	public static <T> T callWithTimeOut(Callable<T> callable, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException{
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Future<T> future = executorService.submit(callable);
		try{
			executorService.shutdown();
			if(!executorService.awaitTermination(timeout, unit)){
				throw new InterruptedException("Timeout after " + timeout + " " + unit);
			}
		}finally {
			executorService.shutdownNow();
		}
		return future.get();
	}
	
	/**
	 * @param task
	 * @param timeout
	 * @param unit
	 * @throws InterruptedException if timeout happened
	 */
	public static void runWithTimeOut(Runnable task, long timeout, TimeUnit unit) throws InterruptedException{
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(task);
		try{
			executorService.shutdown();
			if(!executorService.awaitTermination(timeout, unit)){
				throw new InterruptedException("Timeout after " + timeout + " " + unit);
			}
		}finally {
			executorService.shutdownNow();
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Timeouts.class);
	
}
