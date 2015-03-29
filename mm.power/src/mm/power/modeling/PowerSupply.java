package mm.power.modeling;


import javax.ws.rs.core.Response;

public interface PowerSupply {

  public Response turnOn(int socket);
  
  public Response turnOff(int socket);
  
  public Response status();
 
  public Response status(int socket);
  
  public Response toggle(int socket);

  public String getId();
  
  public String toString();

}
