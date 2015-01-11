package group_0359.backend;

public class NoSuchUserException  extends Exception{
	/**
     * 
     */
    private static final long serialVersionUID = 1265487276676946046L;

    public NoSuchUserException(){
		super();
	}
	
	public NoSuchUserException(String message){
		super(message);
	}
}
