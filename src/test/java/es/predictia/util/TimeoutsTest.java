package es.predictia.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeoutsTest {

	@Test(expected=ExecutionException.class)
	public void failingSleepTimeout() throws Exception{
		Timeouts.sleepWithTimeOut(10, new Timeouts.AwakeningCondition() {
			@Override
			public boolean wakeUp() throws Throwable {
				LOGGER.info("Failing check");
				throw new IllegalStateException();
			}
		}, 50, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected=InterruptedException.class)
	public void interruptedSleepTimeout() throws Exception{
		Timeouts.sleepWithTimeOut(10, new Timeouts.AwakeningCondition() {
			@Override
			public boolean wakeUp() throws Throwable {
				LOGGER.info("check for sleep");
				return false;
			}
		}, 50, TimeUnit.MILLISECONDS);
	}

	@Test
	public void okSleepTimeout() throws Exception{
		final AtomicLong atomicLong = new AtomicLong(0);
		Timeouts.sleepWithTimeOut(10, new Timeouts.AwakeningCondition() {
			@Override
			public boolean wakeUp() throws Throwable {
				LOGGER.info("check for sleep");
				return atomicLong.incrementAndGet() == 2;
			}
		}, 100, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected=InterruptedException.class)
	public void notOkSleepTimeout() throws Exception{
		final AtomicLong atomicLong = new AtomicLong(0);
		Timeouts.sleepWithTimeOut(10, new Timeouts.AwakeningCondition() {
			@Override
			public boolean wakeUp() throws Throwable {
				LOGGER.info("check for sleep");
				return atomicLong.incrementAndGet() == 50;
			}
		}, 100, TimeUnit.MILLISECONDS);
	}
	
	
	@Test(expected=InterruptedException.class)
	public void testTimeout() throws Exception{
		Timeouts.runWithTimeOut(new Runnable() {
			@Override
			public void run() {
				while(true){
					LOGGER.info("Infinite runnable");
				}
			}
		}, 50, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void testNotTimeout() throws Exception{
		Timeouts.runWithTimeOut(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("Normal runnable");
			}
		}, 50, TimeUnit.MILLISECONDS);
	}
	
	@Test(expected=InterruptedException.class)
	public void testCallTimeout() throws Exception{
		final Integer value = Integer.valueOf(1);
		Assert.assertEquals(value, Timeouts.callWithTimeOut(new Callable<Integer>() {
			@Override
			public Integer call() {
				while(value < 2){
					LOGGER.info("Infinite runnable");
				}
				return value;
			}
		}, 50, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testCallNotTimeout() throws Exception{
		final Integer value = Integer.valueOf(1);
		Assert.assertEquals(value, Timeouts.callWithTimeOut(new Callable<Integer>() {
			@Override
			public Integer call() {
				LOGGER.info("Normal callable");
				return value;
			}
		}, 50, TimeUnit.MILLISECONDS));
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutsTest.class);
	
}
