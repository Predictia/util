package es.predictia.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;

public class Matchers {
	
	private Matchers() {}
	
	/**
	 * Function que devuelve la primera ocurrencia del Pattern si existe en el
	 * String de entrada, o null en otro caso
	 * 
	 * @param pattern
	 * @return
	 */
	public static Function<String, String> firstMatchedGroupFunction(final Pattern pattern){
		return new Function<String, String>() {
			@Override
			public String apply(String arg0) {
				Matcher m = pattern.matcher(arg0);
				if (m.matches()) {
					return m.group(1);
				}else{
					return null;
				}
			}
		};
	}
	
	/**
	 * Function que devuelve las ocurrencias del Pattern
	 * 
	 * @param pattern
	 * @return
	 */
	public static Function<String, Set<String>> allMatchedGroupsFunction(final Pattern pattern){
		return new Function<String, Set<String>>() {
			@Override
			public Set<String> apply(String arg0) {
				Set<String> matches = new LinkedHashSet<String>();
				Matcher m = pattern.matcher(arg0);
				while (m.find()) { // find next match
					String match = m.group(1);
				    matches.add(match);
				    arg0 = arg0.replace(match, "");
				    m = pattern.matcher(arg0);
				}
				return matches;
			}
		};
	}
	
}
