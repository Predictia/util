package es.predictia.util.date;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Some common date patterns. If not indicated otherwise, the separator between
 * year-month-days is the slash (/) and between hours and minutes the colon (:)
 * 
 * @author Max Tuni San Martín
 * 
 */
public enum Pattern{
	
	yyyy("yyyy"), MM_yyyy("MM/yyyy"), dd_MM_yy("dd/MM/yy"), dd_MM_yyyy("dd/MM/yyyy"), dd_MM_yyyy_HH(
			"dd/MM/yyyy HH"), dd_MM_yyyy_HH_MM("dd/MM/yyyy HH:mm"), EEE_dd_HH_mm(
			"EEE dd HH:mm"), EEE("EEE"), EEEEE("EEEEE"), EEE_HH_mm(
			"EEE HH:mm"), DD_MM_YYYY_HH_MM_SS("dd/MM/yyyy HH:mm:ss"), yyyyMMdd(
			"yyyyMMdd"), ddMMyyyy("ddMMyyyy"), yyyy_MM_dd_dashes("yyyy-MM-dd"), HH_mm_ss(
			"HH:mm:ss"), yyyyMM("yyyyMM"), HH00ddMMM("HH':00' dd MMM"), HHmmddMMM(
			"HH:mm dd MMM"), HH00("HH'00'"), HH_mm("HH:mm'"), yyyy_MM_dd_HH_dashes(
			"yyyy-MM-dd HH"), EEE_dd("EEE dd"), yyyyMMddHHmmss("yyyyMMddHHmmss"), MMMM(
			"MMMM"), mm("mm"), dd_MMMM("dd'-'MMMM"), ddMMyyyy_HH(
			"ddMMyyyy'_'HH"), yyyy_MM_dd_T_HH("yyyy'-'MM'-'dd'T'HH:mm:ss"), dd_MM_yyyy_HH_dots(
			"dd'.'MM'.'yyyy' 'HH:mm:ss"), dd_MMM_yyyy_dashes("dd-MMM-yyyy"), SQL("yyyy-MM-dd HH:mm:ss.S"),
			dd("dd"), dd_MMM_yyyy("dd/MMM/yyyy"), MM_dd_yyyy_HH("MM/dd/yyyy HH"), 
			MM_dd_yyyy_HH_MM("MM/dd/yyyy HH:mm"), MM_dd_yyyy("MM/dd/yyyy"), ISO8601("yyyy-MM-dd'T'HH:mm:ssz");
	
	private Pattern(String pattern){
		this.patternString = pattern;
		this.dateTimeFormatter = DateTimeFormat.forPattern(pattern).withZone(DateTimeZone.UTC);
	}
	
	private final String patternString;
	private final DateTimeFormatter dateTimeFormatter;
	
	public String getPatternString() {
		return patternString;
	}
	
	/**
	 * @return DateTimeFormatter with UTC zone
	 */
	public DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter;
	}
	
}
