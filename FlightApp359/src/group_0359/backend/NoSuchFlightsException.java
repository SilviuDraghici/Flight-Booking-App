package group_0359.backend;

public class NoSuchFlightsException extends Exception {

	/**
     * 
     */
    private static final long serialVersionUID = 7610333829430829836L;

    public NoSuchFlightsException(){
		super();
	}
	
	public NoSuchFlightsException(String message){
		super(message);
	}
}
