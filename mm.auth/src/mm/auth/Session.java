package mm.auth;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/session")
@Singleton
public class Session {

  private UUID sessionId;
  private GregorianCalendar timer;
  private Date currentDate;
  private Date expire;
  private HashMap<String, Date> ids = new HashMap<String, Date>();
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
  @Path("/createSession/{user}/{pw}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response createSessionId(@PathParam("user") String user, @PathParam("pw") String pw) {
    Response response = this.auth.authorization(user, pw);
    if (response.getStatus() == 200) {
      this.buildSessionId();
      this.setTimer();
      if (!this.ids.containsKey(this.sessionId.toString())) {
        this.ids.put(this.sessionId.toString(), this.expire);
      }
      JSONObject  temp = new JSONObject();
      try {
        temp.put(this.sessionId.toString(), response.getEntity().toString());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return Response.ok(this.sessionId.toString() + " " + response.getEntity().toString()).build();
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
   * <p>
   * As default the expire time for a sessionId is set to 15 minutes. This could be changed in
   * the line "this.timer.add(GregorianCalendar.MINUTE, 15);", by editing the value "15".
   */
  private void setTimer() {
    this.timer = new GregorianCalendar();
    this.timer.add(GregorianCalendar.MINUTE, 15);
    
    // test ob die Session als ungültig erkannt wird
    //this.timer.add(GregorianCalendar.SECOND, 10);
    
    this.expire = timer.getTime();
    //System.out.println(this.expire.toString());
  }
  
  /**
   * Checks if the time of the given sessionId is still within the 15 minute span.
   * The method uses the Java class Date() to get the current time and compares 
   * it to the expire time of the sessionId.
   * <p>
   * If the sessionId is expired it will be deleted from the hash map "ids", else
   * a new timer is set to become a new 15 minute time span for the sessionId. In this case
   * the hash map "ids" will be updated for the key "sessionId".
   * 
   * @param sessionId
   * @return true if sessionId is still valid, else false.
   */
  private boolean checkTime(String sessionId) {
    this.currentDate = new Date();
    if (this.currentDate.getTime() >= this.ids.get(sessionId).getTime()) {
      this.ids.remove(sessionId);
      return false;
    }
    this.setTimer();
    this.ids.put(sessionId, this.expire);
    return true;
  }
  
  /**
   * Checks if the given sessionId is still valid or if the
   * sessionId is expired (sessionId expires after 15 minutes).
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
  @Path("isValid/{sessionId}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  public Response testSession(@PathParam("sessionId") String sessionId) {
    if (this.ids.containsKey(sessionId)) {
      this.expire = this.ids.get(sessionId);
      if (this.checkTime(sessionId)) {
        return Response.ok("valid sessionID").build();
      }
    }
    return Response.status(408).entity("sessionID expired!").build();
  }
}
