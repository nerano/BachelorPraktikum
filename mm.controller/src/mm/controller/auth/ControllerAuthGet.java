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
  
  public void authtentification() {
    da.setUserName();
    da.setPassword();
    String user = da.getUserName();
    String pw = da.getPassword();

    System.out.println(target.path(user).path(pw).request().get(String.class));
  }
  
  public URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest/authmain").build();
  }
  
  public static void main(String[] args) {
    ControllerAuthGet auth = new ControllerAuthGet();
    auth.authtentification();
  }
}
