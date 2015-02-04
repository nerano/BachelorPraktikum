package mm.power.exceptions;


/**
 * Signals that a transfer could not completed, please specify the target and source 
 * in the String 
 * @author john
 *
 */
public class TransferNotCompleteException extends Exception {

    public TransferNotCompleteException(String s){
        
        super(s);
    }
}
