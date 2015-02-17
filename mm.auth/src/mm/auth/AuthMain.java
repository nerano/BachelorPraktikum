package mm.auth;

import java.net.URI;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

@Path("/authmain")
public class AuthMain {

  //MediaType mediatype = new MediaType(MediaType.TEXT_PLAIN, "subAuth");  
  private static final HashMap<String, String> users;
  private ClientConfig config = new ClientConfig();
  private Client client = ClientBuilder.newClient(config);

  static
  {
    users = new HashMap<String, String>();
    users.put("test", "test");
    users.put("admin", "0000");
    users.put("sebastian", "s=8!1");
  }

  // This method is called if TEXT_PLAIN is request
 /**
  * ???.
  */
  @GET
  @Path("/{user}/{pw}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  /**
   * This method authorized any user.
   * @param user is the authorized user
   * @param pw is password of this active user
   * @return true if authorization was successful otherwise false
   */
  public String sayAuth(@PathParam("user") String user, @PathParam("pw") String pw) {
    String login = "LogIn failed!";
    
    if (users.containsKey(user)) {
      if (user != null && users.get(user).equals(pw)) {
        WebTarget target = client.target(getBaseUri());
        return target.path("/createSession").request().get(String.class);
      } 
    }
    return login;
  }
  
  public static URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest/session").build();
  }
} 