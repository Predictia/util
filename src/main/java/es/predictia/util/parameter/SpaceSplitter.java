package es.predictia.util.parameter;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

class SpaceSplitter {

	private SpaceSplitter(){}
	
	/**
	 * Splits by ' ' char, allowing escaping with '\' char
	 */
	static List<String> split(String in){
		String destinations = in.replace("\\ ", SCAPED_SPACE_SUBS);
		return FluentIterable
			.from(SPLITTER.split(destinations))
			.transform(new Function<String, String>() {
				@Override
				public String apply(String input) {
					return input.replace(SCAPED_SPACE_SUBS, " ");
				}
			})
			.toList();
	}
	
	private static final Splitter SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();
	
	private static final String SCAPED_SPACE_SUBS = "[ScapedSpace]";
	
}
