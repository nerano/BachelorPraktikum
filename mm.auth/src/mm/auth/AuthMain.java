package mm.auth;

import java.util.HashMap;

import javax.ws.rs.core.Response;

public class AuthMain {

  //MediaType mediatype = new MediaType(MediaType.TEXT_PLAIN, "subAuth");  
  private static final HashMap<String, UserData> users;

  static
  {
    users = new HashMap<String, UserData>();
    users.put("test", new UserData("test", "standard"));
    users.put("admin", new UserData("0000", "admin"));
    users.put("sebastian", new UserData("s=8!1", "admin"));
  }

  /**
   * This method checks if the given parameters match the saved information.
   * If the data matches true is returned otherwise false.
   * 
   * @param user is the authorized user
   * @param pw is password of this active user
   * @return true if authorization was successful otherwise false
   */
  public Response authorization(String user, String pw) {
    
    if (users.containsKey(user)) {
      if (user != null && users.get(user).getPassword().equals(pw)) {
        return Response.ok(AuthMain.users.get(user).getRole()).build();
      } 
    }
    return Response.status(403).entity("Wrong user name or password!").build();
  }
} 