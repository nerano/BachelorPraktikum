package mm.auth;

import java.util.HashMap;

public class AuthMain {

  //MediaType mediatype = new MediaType(MediaType.TEXT_PLAIN, "subAuth");  
  private static final HashMap<String, String> users;

  static
  {
    users = new HashMap<String, String>();
    users.put("test", "test");
    users.put("admin", "0000");
    users.put("sebastian", "s=8!1");
  }

  // This method is called if TEXT_PLAIN is request
  /**
   * This method authorized any user.
   * @param user is the authorized user
   * @param pw is password of this active user
   * @return true if authorization was successful otherwise false
   */
  public boolean sayAuth(String user, String pw) {
    boolean login = false;
    
    if (users.containsKey(user)) {
      if (user != null && users.get(user).equals(pw)) {
        //WebTarget target = client.target(getBaseUri());
        return true;
      } 
    }
    return login;
  }
} 