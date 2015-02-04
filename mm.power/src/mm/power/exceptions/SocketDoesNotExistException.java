package mm.power.exceptions;


/**
 * Signals that a Socket does not exist on the hardware.
 * @author john
 *
 */
public class SocketDoesNotExistException extends EntryDoesNotExistException {

    public SocketDoesNotExistException(String s){
        
        super(s);
    }
}
