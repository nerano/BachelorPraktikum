package mm.power.exceptions;


/**
 * Signals that a Socket does not exist on the hardware.
 * @author john
 *
 */
public class SocketDoesNotExistException extends EntryDoesNotExistException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3562102439796636150L;

	public SocketDoesNotExistException(String s){
        
        super(s);
    }
}
