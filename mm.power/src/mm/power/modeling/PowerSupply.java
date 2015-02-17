package mm.power.modeling;

import java.io.IOException;

import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;



public interface PowerSupply {
	

  public boolean turnOn(int socket) throws IOException, 
               TransferNotCompleteException, EntryDoesNotExistException;
  
  public boolean turnOff(int socket) throws IOException, 
               TransferNotCompleteException, EntryDoesNotExistException;
  
  public String  status() throws IOException, 
               TransferNotCompleteException;
 
  public String  status(int socket) throws IOException, 
               TransferNotCompleteException, EntryDoesNotExistException;
  
  public boolean toggle(int socket) throws IOException, 
               TransferNotCompleteException, EntryDoesNotExistException;

  public String getId();
  
  public String toString();

}
