package mm.power.exceptions;


/**
 * Signals that a requested entry does not exist on the Hardware (e.g. 
 * requesting the 4th socket on a Outlet with 3 sockets or the 9th port on a switch
 * with 8 ports.)
 * @author julian
 * 
 *
 */
public class EntryDoesNotExistException extends Exception{


    public EntryDoesNotExistException(String s){
        
        super(s);
    }



}
