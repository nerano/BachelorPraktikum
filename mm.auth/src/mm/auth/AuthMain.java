package mm.auth;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Path("/authmain")
public class AuthMain {

  //MediaType mediatype = new MediaType(MediaType.TEXT_PLAIN, "subAuth");  
  private static final HashMap<String, String> users;

  static
  {
    users = new HashMap<String, String>();
    users.put("test", "test");
    users.put("admin", "0000");
    users.put("Sebastian", "s=8!1");
  }

  // This method is called if TEXT_PLAIN is request
 /**
  * ???.
  */
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  /**
   * This method authorized any user.
   * @param user is the authorized user
   * @param pw is password of this active user
   * @return true if authorization was succesful otherwise false
   */
  public String sayAuth(@QueryParam("user") String user, @QueryParam("pw") String pw) {
    
    if (users.containsKey(user)) {
      if (user != null && users.get(user).equals(pw)) {
        return "Log in successful!";
      } else {
        return "Log in failed!\nUsername or Password was wrong.";
      }
    } else {
      return "Log in failed!\nUsername or Password was wrong.";
    }
  }
} 