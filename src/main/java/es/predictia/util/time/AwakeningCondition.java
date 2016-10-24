package es.predictia.util.time;

public interface AwakeningCondition {
	
	/** 
	 * @return true if wait should end  
	 */
	public boolean wakeUp();
	
}