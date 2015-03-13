package mm.auth;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("session")
@Singleton
public class Session {

  private UUID sessionId;
  private Date expire;
  private HashMap<UUID, SessionData> ids = new HashMap<UUID, SessionData>();
  private AuthMain auth = new AuthMain();
  
  /**
   * This Method creates a valid sessionId if the given user name and password is valid.
   * <p>
   * To check the logIn parameters the method calls mm.auth.sayAuth. If the response is "200"
   * the method buildSessionId is called to build a valid sessionId. After the sessionId exists
   * the method setTimer() is called to set an initial expire time for the sessionId.
   * If the sessionId does not already exists, it is saved in the hash map "ids". 
   * <p>
   * The hash map provides the possibility for more than one "active" user at the same time. 
   * 
   * Possible HTTP status codes: 
   * <li>200: A valid sessionId is build and overturned.
   * <li>403: LogIn failed, either the user name or the password does not fit the
   *          saved values.
   * 
   * @param user name 
   * @param pw password 
   * @return Response, with the status code 200 for a successful logIn, 403 if logIn fails.
   */
  @GET
  @Path("createSession/{user}/{pw}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response createSessionId(@PathParam("user") String user, @PathParam("pw") String pw) {
    Response response = this.auth.authorization(user, pw);
    if (response.getStatus() == 200) {
      this.buildSessionId();
      this.setTimer();
      if (!this.ids.containsKey(this.sessionId)) {
        this.ids.put(this.sessionId, 
            new SessionData(this.expire, response.getEntity().toString()));
      }
      return Response.ok(this.sessionId.toString()).build();
    }
    return Response.status(403).entity("LogIn failed!").build();
  }
  
  /**
   * Builds a new sessionId using the Java class UUID.
   * The global variable "sessionId" is initialized through a random UUID with the form
   * xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, with x = [a-z,0-9]
   * 
   */
  private void buildSessionId() {
    this.sessionId = UUID.randomUUID();
  } 
  
  /**
   * The method uses the GregorianCalendar() to set a expire time for a sessionId.
   *
   * <p>
   * As default the expire time for a sessionId is set to 15 minutes. This could be changed in
   * the line "this.timer.add(GregorianCalendar.MINUTE, 15);", by editing the value "15".
   */
  private void setTimer() {
    GregorianCalendar timer = new GregorianCalendar();
    timer.add(GregorianCalendar.MINUTE, 15);
    
    // test ob die Session als ung�ltig erkannt wird
    //this.timer.add(GregorianCalendar.SECOND, 10);
    
    this.expire = timer.getTime();
    //System.out.println(this.expire.toString());
  }
  
  /**
   * Checks if the time of the given sessionId is still within the 15 minute span.
   * The method uses the Java class Date() to get the current time and compares 
   * it to the expire time of the sessionId.
   *
   * <p>
   * If the sessionId is expired it will be deleted from the hash map "ids", else
   * a new timer is set to become a new 15 minute time span for the sessionId. In this case
   * the hash map "ids" will be updated for the key "sessionId".
   * 
   * @param sessionId
   * @return true if sessionId is still valid, else false.
   */
  private boolean checkTime(String sessionId) {
    UUID temp = UUID.fromString(sessionId);
    Date currentDate = new Date();
    String userRole;
    if (currentDate.getTime() >= this.ids.get(temp).getExpire().getTime()) {
      this.ids.remove(temp);
      return false;
    }
    userRole = this.ids.get(temp).getRole();
    this.setTimer();
    this.ids.put(temp, new SessionData(this.expire, userRole));
    return true;
  }
  
  /**
   * Checks if the given sessionId is still valid or expired
   * (sessionId expires after 15 minutes).
   * 
   * Possible HTTP status codes:
   * 
   * <li>200: The given SessionID is still valid.
   * <li>408: Request timeout. The given sessionId is expired a new log in is required.
   * 
   * @param sessionId holding a 128bit sessionId with the form 
   *     xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, with x = [a-z,0-9].
   * @return a Response Object with a status code and a possible message body.
   */
  @GET
  @Path("validation/{sessionId}")
  //@Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  public Response testSession(@PathParam("sessionId") String sessionId) {
    UUID temp = UUID.fromString(sessionId);
    if (this.ids.containsKey(temp)) {
      this.expire = this.ids.get(temp).getExpire();
      if (this.checkTime(temp.toString())) {
        return Response.ok("valid sessionID").build();
      }
    }
    return Response.status(408).entity("sessionID expired!").build();
  }
  
  /**
   * Checks if the given sessionId is still valid or expired
   * (sessionId expires after 15 minutes).
   * 
   * Possible HTTP status codes:
   * 
   * <li>200: The given SessionID is still valid.
   * <li>408: Request timeout. The given sessionId is expired a new log in is required.
   * 
   * @param sessionId holding a 128bit sessionId with the form 
   *     xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, with x = [a-z,0-9].
   * @return a Response Object with a status code and a possible message body.
   */
  /*@GET
  @Path("validation")
  public Response testSession(@HeaderParam("sessionId") String sessionId) {
    //System.out.println(sessionId);
    if (this.ids.containsKey(sessionId)) {
      this.expire = this.ids.get(sessionId).getExpire();
      if (this.checkTime(sessionId)) {
        return Response.ok("valid sessionID").build();
      }
    }
    return Response.status(408).entity("SessionID expired!").build();
  }*/
  
  /**
   * Returns the role of the given user.
   * 
   * @param user name for which the role is needed.
   * @return Response containing a status and the user role.
   *         See AuthMain.getUserRole for more information about the status.
   *         Status 403 if user is not logged In at this time.
   */
  /*@GET
  @Path("role/{userName}")
  @Consumes("text/plain")
  public Response testRole(@PathParam("userName") String user) {
    if (this.users.contains(user)) {
      return auth.getUserRole(user);
    }
    return Response.status(403).entity(user + " not logged In!").build();
  }*/
  
  /**
   * Returns the user role associated to the given sessionId
   * 
   * @param sessionId to which the user role is associated.
   * @return Response with status 200 and the user role as entity.
   */
  @GET
  @Path("role")
  public Response getRole(@HeaderParam("sessionId") String sessionId) {
    UUID temp = UUID.fromString(sessionId);
    return Response.ok(this.ids.get(temp).getRole()).build();
  }
  
  @GET
  @Path("getSessionId")
  @Produces(MediaType.TEXT_PLAIN)
  public String getSessionId() {
    return this.sessionId.toString();
  }
}
