/**
 * 
 */
package es.predictia.util.backoff;

public interface BackOff{
	
	/**
	 * @return time in milis to back off
	 */
	public long getBackOffTime();
	
	public Integer getMaxNumberOfRetrys();
	
}