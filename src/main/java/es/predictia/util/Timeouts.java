package es.predictia.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Timeouts {

	/**
	 * @param task
	 * @param timeout
	 * @param unit
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
	
}
