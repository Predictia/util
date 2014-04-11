package es.predictia.util;

import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import es.predictia.util.backoff.BackOff;

public class URLsTest {

	@Test @Ignore("external resource")
	public void testUrlContent() throws Exception{
		String content = URLs.getUrlContent(new URL("http://www.predictia.es/"), 5000);
		Assert.assertTrue(content.length() > 0);
		content = URLs.getUrlContent(new URL("http://www.predictia.es/"), 5000, new BackOff() {
			@Override public Integer getMaxNumberOfRetrys() {
				return 1;
			}
			@Override public long getBackOffTime() {
				return 5000;
			}
		});
		Assert.assertTrue(content.length() > 0);
	}
	
}
