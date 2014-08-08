package es.predictia.util.date;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Some common date patterns
 * 
 * @author Max Tuni San Martín
 * 
 */
public enum Pattern{
	
	/**
	 * yyyy pattern, for example: 2014
	 */
	yyyy("yyyy"), 
	/**
	 * MM/yyyy pattern, for example: 08/2014
	 */
	MM_yyyy("MM/yyyy"), 
	/**
	 * dd/MM/yy pattern, for example: 08/08/14
	 */
	dd_MM_yy("dd/MM/yy"), 
	/**
	 * dd/MM/yyyy pattern, for example: 08/08/2014
	 */
	dd_MM_yyyy("dd/MM/yyyy"), 
	/**
	 * dd/MM/yyyy HH pattern, for example: 08/08/2014 06
	 */
	dd_MM_yyyy_HH("dd/MM/yyyy HH"), 
	/**
	 * dd/MM/yyyy HH:mm pattern, for example: 08/08/2014 06:47
	 */
	dd_MM_yyyy_HH_MM("dd/MM/yyyy HH:mm"),
	/**
	 * EEE dd HH:mm pattern, for example: vie 08 06:47
	 */
	EEE_dd_HH_mm("EEE dd HH:mm"), 
	/**
	 * EEE pattern, for example: vie
	 */
	EEE("EEE"),
	/**
	 * EEEEE pattern, for example: viernes
	 */
	EEEEE("EEEEE"),
	/**
	 * EEE HH:mm pattern, for example: vie 06:47
	 */
	EEE_HH_mm("EEE HH:mm"),
	/**
	 * dd/MM/yyyy HH:mm:ss pattern, for example: 08/08/2014 06:47:41
	 */
	DD_MM_YYYY_HH_MM_SS("dd/MM/yyyy HH:mm:ss"),
	/**
	 * yyyyMMdd pattern, for example: 20140808
	 */
	yyyyMMdd("yyyyMMdd"), 
	/**
	 * ddMMyyyy pattern, for example: 08082014
	 */
	ddMMyyyy("ddMMyyyy"), 
	/**
	 * yyyy-MM-dd pattern, for example: 2014-08-08
	 */
	yyyy_MM_dd_dashes("yyyy-MM-dd"), 
	/**
	 * HH:mm:ss pattern, for example: 06:47:41
	 */
	HH_mm_ss("HH:mm:ss"), 
	/**
	 * yyyyMM pattern, for example: 201408
	 */
	yyyyMM("yyyyMM"), 
	/**
	 * HH':00' dd MMM pattern, for example: 06:00 08 ago
	 */
	HH00ddMMM("HH':00' dd MMM"), 
	/**
	 * HH:mm dd MMM pattern, for example: 06:47 08 ago
	 */
	HHmmddMMM("HH:mm dd MMM"), 
	/**
	 * HH'00' pattern, for example: 0600
	 */
	HH00("HH'00'"), 
	/**
	 * HH:mm' pattern, for example: 06:47
	 */
	HH_mm("HH:mm'"), 
	/**
	 * yyyy-MM-dd HH pattern, for example: 2014-08-08 06
	 */
	yyyy_MM_dd_HH_dashes("yyyy-MM-dd HH"), 
	/**
	 * EEE dd pattern, for example: vie 08
	 */
	EEE_dd("EEE dd"), 
	/**
	 * yyyyMMddHHmmss pattern, for example: 20140808064741
	 */
	yyyyMMddHHmmss("yyyyMMddHHmmss"), 
	/**
	 * MMMM pattern, for example: agosto
	 */
	MMMM("MMMM"), 
	/**
	 * mm pattern, for example: 47 
	 */
	mm("mm"), 
	/**
	 * dd'-'MMMM pattern, for example: 08-agosto
	 */
	dd_MMMM("dd'-'MMMM"), 
	/**
	 * ddMMyyyy'_'HH pattern, for example: 08082014_06
	 */
	ddMMyyyy_HH("ddMMyyyy'_'HH"), 
	/**
	 * yyyy'-'MM'-'dd'T'HH:mm:ss pattern, for example: 2014-08-08T06:47:41
	 */
	yyyy_MM_dd_T_HH("yyyy'-'MM'-'dd'T'HH:mm:ss"), 
	/**
	 * dd'.'MM'.'yyyy' 'HH:mm:ss pattern, for example: 08.08.2014 06:47:41
	 */
	dd_MM_yyyy_HH_dots("dd'.'MM'.'yyyy' 'HH:mm:ss"), 
	/**
	 * dd-MMM-yyyy pattern, for example: 08-ago-2014
	 */
	dd_MMM_yyyy_dashes("dd-MMM-yyyy"), 
	/**
	 * yyyy-MM-dd HH:mm:ss.S pattern, for example: 2014-08-08 06:47:41.6
	 */
	SQL("yyyy-MM-dd HH:mm:ss.S"),
	/**
	 * dd pattern, for example: 08
	 */
	dd("dd"), 
	/**
	 * dd/MMM/yyyy pattern, for example: 08/ago/2014
	 */
	dd_MMM_yyyy("dd/MMM/yyyy"), 
	/**
	 * MM/dd/yyyy HH pattern, for example: 08/08/2014 06
	 */
	MM_dd_yyyy_HH("MM/dd/yyyy HH"), 
	/**
	 * MM/dd/yyyy HH:mm pattern, for example: 08/08/2014 06:47
	 */
	MM_dd_yyyy_HH_MM("MM/dd/yyyy HH:mm"), 
	/**
	 * MM/dd/yyyy pattern, for example: 08/08/2014
	 */
	MM_dd_yyyy("MM/dd/yyyy"), 
	/**
	 * yyyy-MM-dd'T'HH:mm:ssz pattern, for example: 2014-08-08T06:47:41UTC
	 */
	ISO8601("yyyy-MM-dd'T'HH:mm:ssz");
	
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
	 * @return {@link DateTimeFormatter} with {@link DateTimeZone#UTC} zone
	 */
	public DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter;
	}
	
}
