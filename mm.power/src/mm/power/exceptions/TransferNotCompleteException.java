package mm.power.exceptions;


/**
 * Signals that a transfer could not completed, please specify the target and source 
 * in the String 
 * @author john
 *
 */
public class TransferNotCompleteException extends Exception {

	private static final long serialVersionUID = -7934749168529609745L;

	public TransferNotCompleteException(String s){
        
        super(s);
    }
}
