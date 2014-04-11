package es.predictia.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class Beans {

	private Beans() {
	}

	/**
	 * @param <T>
	 *            Bean object
	 * @param beans
	 *            Iterable collection of beans
	 * @param property
	 *            Name of property to check
	 * @param value
	 *            Value of property to check
	 * @return Bean object from list
	 * @throws NoSuchElementException
	 *             if no element in beans satisfies property.equals(value)
	 */
	public static <T> T find(Iterable<T> beans, final String property,
			final Object value) throws NoSuchElementException {
		return Iterables.find(beans, new Predicate<T>() {
			public boolean apply(T arg0) {
				Object prop;
				try {
					prop = PropertyUtils.getProperty(arg0, property);
					if (prop != null) {
						return (prop.equals(value));
					} else if (value == null) {
						return true;
					}
				} catch (Exception e) {
					log.debug("Problem getting bean property: "
							+ e.getMessage());
				}
				return false;
			}
		});
	}

	/**
	 * Finds in a list of beans for the ones whos property is contained in
	 * values
	 * 
	 * @param <T>
	 * @param beans
	 *            Input search list
	 * @param property
	 *            Property to check with
	 * @param values
	 *            Accepted property values
	 * @return new Collection with the beans from input list whose property was
	 *         cointained in 'values'
	 */
	public static <T> Collection<T> findMultiple(Iterable<T> beans,
			final String property, final Collection<? extends Object> values) {
		List<T> found = new ArrayList<T>();
		if ((values != null) && (beans != null)) {
			for (T arg0 : beans) {
				try {
					Object prop = PropertyUtils.getProperty(arg0, property);
					if (values.contains(prop)) {
						found.add(arg0);
					}
				} catch (Exception e) {
					log.debug("Problem getting bean property: "
							+ e.getMessage());
				}
			}
		}
		return found;
	}

	public static <T> List<T> getPropertyFromBeanList(Iterable<?> beans,
			String properity, Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		if (beans != null) {
			for (Object elem : beans) {
				try {
					Object resultelem = PropertyUtils.getProperty(elem,
							properity);
					result.add(clazz.cast(resultelem));
				} catch (Exception e) {
					log.debug("Problem getting bean property: "
							+ e.getMessage());
				}
			}
		}
		return result;
	}

	public static <T> List<T> deffensiveBeanListCopy(Iterable<T> beans,
			Class<T> clazz) {
		List<T> salida = new ArrayList<T>();
		if (beans != null) {
			for (T bean : beans) {
				try {
					if (java.beans.Beans.isInstanceOf(bean, clazz)) {
						salida.add(clazz.cast(BeanUtils.cloneBean(bean)));
					}

				} catch (Exception e) {
					log.debug("Problem getting bean property: "
							+ e.getMessage());
				}
			}
		}
		return salida;
	}

	@SuppressWarnings("unchecked")
	public static String toString(Object bean) {
		StringBuilder salida = new StringBuilder();
		salida.append(bean.getClass().getName());
		try {
			Map<String, Object> p1 = BeanUtils.describe(bean);
			for (Map.Entry<String, Object> propE : p1.entrySet()) {
				salida.append(" - " + propE.getKey() + ": " + propE.getValue());
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return salida.toString();
	}

	@SuppressWarnings("unchecked")
	public static boolean equals(Object b1, Object b2) {
		try {
			Map<String, Object> p1 = BeanUtils.describe(b1);
			Map<String, Object> p2 = BeanUtils.describe(b2);
			for (String prop : p1.keySet()) {
				if (!p1.get(prop).equals(p2.get(prop))) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			log.debug(e.getMessage());
			return false;
		}
	}

	/** Busca en una bean por una propiedad de forma insensitiva
	 * @param bean
	 * @param name Nombre de la propiedad
	 * @return Valor de la propiedad en el bean
	 * @throws Exception
	 *             si no se encuentra propiedad con el nombre proporcionado o
	 *             {@link PropertyUtils#getProperty(Object, String)} arroja una
	 *             excepcion
	 */
	public static Object getInsensitiveProperty(Object bean, String name) throws Exception {
		for(PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(bean)){
			if(StringUtils.equalsIgnoreCase(pd.getName(), name)){
				return PropertyUtils.getProperty(bean, pd.getName()); 
			}
		}
		throw new IllegalArgumentException("No property with name " + name + " found.");
	}
	

	/** Asigna un valor a una propiedad de una bean de forma insensitiva
	 * 
	 * @throws Exception
	 *             si {@link PropertyUtils#setProperty(Object, String, Object)} arroja una
	 *             excepcion
	 */
	public static void setInsensitiveProperty(Object bean, String name, Object value) throws Exception {
		for(PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(bean)){
			if(StringUtils.equalsIgnoreCase(pd.getName(), name)){
				PropertyUtils.setProperty(bean, pd.getName(), value);
				break;
			}
		}
	}

	/**
	 * Applies to all String properties
	 * org.apache.commons.lang.StringUtils.stripToNull function: trim and set to
	 * null if empty
	 * 
	 * @param bean
	 * @throws Exception
	 */
	public static void stripToNullStringProperties(Object bean)
			throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> desc = PropertyUtils.describe(bean);
		for (String prop : desc.keySet()) {
			if (String.class.equals(PropertyUtils.getPropertyType(bean, prop))) {
				String propValue = (String) PropertyUtils.getProperty(bean,
						prop);
				try {
					PropertyUtils.setProperty(bean, prop, StringUtils
							.stripToNull(propValue));
				} catch (Exception e) {
					log.debug(e.getMessage());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void copyProperties(Object dest, Object orig){
		try {
			log.debug("copyProperties requested from " + orig + " to " + dest);
			Map<String, Object> properties = PropertyUtils.describe(orig);
			log.debug("source bean contains " + properties.entrySet().size() + " properties");
			for(Map.Entry<String, Object> property : properties.entrySet()){
				if(property.getValue() != null){
					log.debug("Inserting property " + property.getKey() + " with value: " + property.getValue());
					try{
						callSetter(dest, property.getKey(), property.getValue());
					} catch (Exception e) {
						log.debug("Error calling setter: " + e.getMessage());
					}
				}else{
					log.debug("Skipping empty property " + property.getKey());
				}				
			}
		} catch (Exception e) {
			log.info("Error describing bean:" + e.getMessage());
		}
	}

    private static boolean callSetter(Object obj, String property, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method m = null;
        boolean result = false;
        m = findMethod(obj, property, value);
        if(m != null) {
            m.invoke(obj, value);
            result = true;
        }else{
        	log.debug("No method found while copying property " + property + " to destination: " + obj);
        }
        return result;
    }
    
    private static Method findMethod(Object obj, String property, Object value) {
        Method m = null;
        Class<?> theClass = obj.getClass();
        String setter = String.format("set%C%s", property.charAt(0), property.substring(1));
        Class<?> paramType = value.getClass();
        while (paramType != null) {
            try {
                m = theClass.getMethod(setter, paramType);
                return m;
            } catch (NoSuchMethodException ex) {
                // try on the interfaces of this class
                for (Class<?> iface : paramType.getInterfaces()) {
                    try {
                        m = theClass.getMethod(setter, iface);
                        return m;
                    } catch (NoSuchMethodException ex1) {
                    }
                }
                paramType = paramType.getSuperclass();
            }
        }
        return m;
    }
	
	private final static Logger log = LoggerFactory.getLogger(Beans.class);

}
