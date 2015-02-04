package mm.controller.auth;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class ControllerAuthGet {

  private ClientConfig config = new ClientConfig();
  Client client = ClientBuilder.newClient(config);
  WebTarget target = client.target(getBaseUri());
  
  DataInput da = new DataInput();
  
  public boolean authtentification() {
    boolean login = false;
    da.setUserName();
    da.setPassword();
    String user = da.getUserName();
    String pw = da.getPassword();
    String data = "authmain?user=" + user + "&pw=" + pw;
    
    if(target.path(data).request().get(String.class).equals("Log in successful!")) {
      login = true;
    }
    
    return login;
  }
  
  public URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost8080/mm.auth/rest").build();
  }
}
