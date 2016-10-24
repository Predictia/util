package es.predictia.util.time;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/** Performs operations with fixing maximum time to wait for them
 * @author tunim
 *
 */
public class LimitedTimeOperation {

	private final long time;
	
	private final TimeUnit unit;

	private LimitedTimeOperation(long time, TimeUnit unit) {
		super();
		this.time = time;
		this.unit = unit;
	}
	
	public static LimitedTimeOperation of(long time, TimeUnit unit){
		return new LimitedTimeOperation(time, unit);
	}
	
	public <T> T get(Callable<T> callable) throws InterruptedException, ExecutionException {
		return Timeouts.callWithTimeOut(callable, time, unit);
	}
	
	public void run(Runnable task) throws InterruptedException, ExecutionException {
		Timeouts.runWithTimeOut(task, time, unit);
	}

	/** Waits checking for an {@link AwakeningCondition}
	 * @param condition
	 * @param checkTime time to wait between checks
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void wait(AwakeningCondition condition, LimitedTimeOperation checkTime) throws InterruptedException, ExecutionException {
		Waiter.sleepWithTimeOut(checkTime.unit.toMillis(checkTime.time), condition, time, unit);
	}
	
}
