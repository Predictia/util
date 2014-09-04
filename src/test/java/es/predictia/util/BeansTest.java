package es.predictia.util;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class BeansTest {

	@Test
	public void findTest() throws Exception{
		MyBean b1 = new MyBean("hola", 1), b2 = new MyBean("hola2", 2);
		List<MyBean> beans = Lists.newArrayList(b1, b2);
		log.debug(PropertyUtils.describe(beans.get(0)).toString());
		Assert.assertEquals(b1, Beans.find(beans, "prop1", "hola"));
		Assert.assertEquals(b1, Beans.find(beans, "prop2", Integer.valueOf(1)));
		Assert.assertEquals(b2, Beans.find(beans, "prop1", "hola2"));
		Assert.assertEquals(b2, Beans.find(beans, "prop2", Integer.valueOf(2)));
		boolean thrown = false;
		try {
			Beans.find(beans, "prop2", "3");
		} catch (NoSuchElementException e) {
			thrown = true;
		}
		Assert.assertTrue(thrown);

	}
	
	@Test
	public void stripTest() throws Exception{
		MyBean b1 = new MyBean("", 1);
		Beans.stripToNullStringProperties(b1);
		Assert.assertNull(b1.getProp1());
	}
	
	@Test
	public void insensitiveTest() throws Exception{
		MyBean b1 = new MyBean("", 1);
		Assert.assertEquals("", Beans.getInsensitiveProperty(b1, "Prop1"));
		Assert.assertEquals(Integer.valueOf(1), Beans.getInsensitiveProperty(b1, "ProP2"));
		Integer newValue = 2;
		Beans.setInsensitiveProperty(b1, "prOp2", newValue);
		Assert.assertEquals(newValue, Beans.getInsensitiveProperty(b1, "ProP2"));
	}
	
	public static class MyBean{
		private String prop1;
		private Integer prop2;
		public MyBean(){
			super();
		}
		public MyBean(String prop1, Integer prop2) {
			super();
			this.prop1 = prop1;
			this.prop2 = prop2;
		}
		public String getProp1() {
			return prop1;
		}
		public void setProp1(String prop1) {
			this.prop1 = prop1;
		}
		public Integer getProp2() {
			return prop2;
		}
		public void setProp2(Integer prop2) {
			this.prop2 = prop2;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((prop1 == null) ? 0 : prop1.hashCode());
			result = prime * result + ((prop2 == null) ? 0 : prop2.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			MyBean other = (MyBean) obj;
			if (prop1 == null) {
				if (other.prop1 != null) return false;
			} else if (!prop1.equals(other.prop1)) return false;
			if (prop2 == null) {
				if (other.prop2 != null) return false;
			} else if (!prop2.equals(other.prop2)) return false;
			return true;
		}
	}

	private final static Logger log = LoggerFactory.getLogger(BeansTest.class);
}