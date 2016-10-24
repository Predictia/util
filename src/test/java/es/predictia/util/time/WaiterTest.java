package es.predictia.util.time;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaiterTest {
	
	@Test(expected=IllegalArgumentException.class)
	public void badArgumentSleepTimeout() throws Exception{
		Waiter.sleepWithTimeOut(1000, new AwakeningCondition() {
			@Override
			public boolean wakeUp(){
				return true;
			}
		}, 50, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected=ExecutionException.class)
	public void failingSleepTimeout() throws Exception{
		Waiter.sleepWithTimeOut(10, new AwakeningCondition() {
			@Override
			public boolean wakeUp(){
				LOGGER.info("Failing check");
				throw new IllegalStateException();
			}
		}, 1, TimeUnit.MINUTES);
	}
	
	@Test(expected=InterruptedException.class)
	public void interruptedSleepTimeout() throws Exception{
		Waiter.sleepWithTimeOut(10, new AwakeningCondition() {
			@Override
			public boolean wakeUp(){
				LOGGER.info("check for sleep");
				return false;
			}
		}, 50, TimeUnit.MILLISECONDS);
	}

	@Test
	public void okSleepTimeout() throws Exception{
		final AtomicLong atomicLong = new AtomicLong(0);
		Waiter.sleepWithTimeOut(10, new AwakeningCondition() {
			@Override
			public boolean wakeUp(){
				LOGGER.info("check for sleep");
				return atomicLong.incrementAndGet() == 2;
			}
		}, 1, TimeUnit.MINUTES);
	}
	
	@Test(expected=InterruptedException.class)
	public void notOkSleepTimeout() throws Exception{
		final AtomicLong atomicLong = new AtomicLong(0);
		Waiter.sleepWithTimeOut(10, new AwakeningCondition() {
			@Override
			public boolean wakeUp() {
				LOGGER.info("check for sleep");
				return atomicLong.incrementAndGet() == 50;
			}
		}, 100, TimeUnit.MILLISECONDS);
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WaiterTest.class);

}
