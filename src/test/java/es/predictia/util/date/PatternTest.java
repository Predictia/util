package es.predictia.util.date;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternTest {

	@Test
	public void testPatterns() throws Exception{
		DateTime dateTime = new DateTime(); 
		for(Pattern pattern : Pattern.values()){
			LOGGER.debug(pattern.getPatternString() + " pattern, for example: " + pattern.getDateTimeFormatter().print(dateTime));
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PatternTest.class);
	
}
