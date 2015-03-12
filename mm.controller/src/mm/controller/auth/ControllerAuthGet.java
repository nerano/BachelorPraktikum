package mm.controller.auth;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

/* No more need for this class */
public class ControllerAuthGet {

  private static ClientConfig config = new ClientConfig();
  static Client client = ClientBuilder.newClient(config);
  static WebTarget target = client.target(getBaseUri());
  static String sessionId;
  
  static DataInput da = new DataInput();
  
  public Response authtentification() {
    
    da.setUserName();
    da.setPassword();
    String user = da.getUserName();
    String pw = da.getPassword();

    return target.path("createSession").path(user).path(pw).request().get();
  }
  
  public boolean isValid(String sessionId) {
    boolean bool = false;
    
    Response r = target.path(sessionId).request().get(Response.class);
    
    if(r.getStatus() == 200) {
      bool = true;
    }
    
    return bool;
  }
  
  public static URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest/session").build();
  }
  
  public static void main(String[] args) {
    ControllerAuthGet auth = new ControllerAuthGet();
    sessionId = auth.authtentification().readEntity(String.class);
    System.out.println(sessionId);
    System.out.println(target.path("validation").path(sessionId).request().get().readEntity(String.class));
    System.out.println(target.path("role").path(sessionId).request().get().readEntity(String.class));
    String sessionId2 = auth.authtentification().readEntity(String.class);
    System.out.println(sessionId2);
    System.out.println(target.path("validation").path(sessionId2).request().get().readEntity(String.class));
    System.out.println(target.path("role").path(sessionId2).request().get().readEntity(String.class));
    
    /* Pausiert die Methode für 11 Sekunden um zu testen ob die SessionID invalid wird
    try {
      Thread.sleep(11000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
    
    if (target.path("validation").path(sessionId2).request().get().readEntity(String.class).equals("New LogIn!!!")) {
      System.out.println("Your session expired. Please log in again: ");
      auth.authtentification();
    }
    System.out.println(target.path("validation").path(sessionId).request().get().readEntity(String.class));
    
    da.closeScanner();
  }
}
