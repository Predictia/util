package es.predictia.util.date;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ClosestTest {

	@Test
	public void closestTest() throws Exception{
		DateTimeFormatter fmt = Pattern.dd_MM_yyyy.getDateTimeFormatter();
		DateTime dt1 = fmt.parseDateTime("25/10/1983"), dt2 = fmt.parseDateTime("25/10/2000"), dt = fmt.parseDateTime("25/10/2010");
		Assert.assertEquals(dt2, Closest.getClosestDate(Lists.newArrayList(dt1, dt2), dt));
		
		DateTime dt11 = fmt.parseDateTime("13/01/2011"), dt12 = fmt.parseDateTime("14/01/2011"), d13 = fmt.parseDateTime("15/01/2011"), d14 = fmt.parseDateTime("16/01/2011");
		Assert.assertEquals(d14, Closest.getClosestDate(Lists.newArrayList(dt11, dt12, d13, d14), d14));
		
		MiClase mc1 = new MiClase(dt1), mc2 = new MiClase(dt2), mc = new MiClase(dt);
		Closest<MiClase> c = new Closest<MiClase>(Lists.newArrayList(mc1, mc2), mc);
		Assert.assertEquals(mc2, c.getClosest());
	}
	
	private static class MiClase implements DateProvider{
		private final DateTime dt;
		MiClase(DateTime dt){
			this.dt = dt;
		}
		public DateTime getDate() {
			return dt;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dt == null) ? 0 : dt.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			MiClase other = (MiClase) obj;
			if (dt == null) {
				if (other.dt != null) return false;
			} else if (!dt.equals(other.dt)) return false;
			return true;
		}
	}
	
}
