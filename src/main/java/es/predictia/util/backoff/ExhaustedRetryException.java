package es.predictia.util.backoff;

public class ExhaustedRetryException extends Exception {

	public ExhaustedRetryException(BackOff backOff, Throwable throwable){
		super("Exhausted after " + backOff.getMaxNumberOfRetrys() + " retrys", throwable);
	}
	
	private static final long serialVersionUID = -5559575332942241375L;

}
