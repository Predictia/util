package es.predictia.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Formatters {

	private Formatters(){}
	
	public static final DecimalFormatSymbols ENGLISH_DECIMAL_FORMAT_SYMBOLS = createEnglishDecimalFormatSymbols(); 
	
	private static DecimalFormatSymbols createEnglishDecimalFormatSymbols(){
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ENGLISH);
		dfs.setDecimalSeparator('.');
		dfs.setGroupingSeparator(','); 
		return dfs;
	}
	
}
