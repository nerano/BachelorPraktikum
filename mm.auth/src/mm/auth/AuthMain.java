package mm.auth;

import java.util.HashMap;

import javax.ws.rs.core.Response;

public class AuthMain {

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
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: The given pair of user name and password are valid and saved in users.
   * <li>403: False user name or password.
   *          User name and password does not match the saved information.
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

  /**
   * Returns a Response holding the user role as entity.
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: If the user name is registered and the role is send.
   * <li>403: Status error if the user name does not exist.
   * 
   * @param user name for which the role is needed.
   * @return Response with status 200 if user name is known and its role as entity,
   *          otherwise status 403 user name is not registered with entity string.
   */
  public Response getUserRole(String user) {
    if (AuthMain.users.containsKey(user)) {
      return Response.ok(AuthMain.users.get(user).getRole()).build();
    }
    return Response.status(403).entity("User name not registered!").build();
  }
}