package mm.auth.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.auth.main.AuthMain;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("session")
@Singleton
public class Session {

  private UUID sessionId;
  private Date expire;
  private HashMap<UUID, SessionData> ids = new HashMap<UUID, SessionData>();
  private Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private AuthMain auth = new AuthMain();

  /**
   * Creates a valid sessionId if the given user name and password is valid.
   * 
   * <p>
   * To check the logIn parameters the method calls mm.auth.sayAuth. If the
   * response is "200" the method buildSessionId is called to build a valid
   * sessionId. After the sessionId exists the method setTimer() is called to
   * set an initial expire time for the sessionId. If the sessionId does not
   * already exists, it is saved in the hash map "ids".
   * </p>
   * 
   * <p>
   * The hash map provides the possibility for more than one "active" user at
   *    the same time.
   * </p>
   * Possible HTTP status codes:
   * <li>200: A valid sessionId is build and returned.</li>
   * <li>403: LogIn failed, either the user name or the password does not fit
   * the saved values.</li>
   * 
   * @param user
   *          name of the user.
   * @param pw
   *          password
   * @return Response, with the status code 200 for a successful logIn, 403 if
   *         logIn fails.
   */
  @GET
  @Path("createSession/{user}/{pw}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response createSessionId(@PathParam("user") String user,
      @PathParam("pw") String pw) {
    Response response = this.auth.authorization(user, pw);
    if (response.getStatus() == 200) {
      this.buildSessionId();
      this.setTimer();
      if (!this.ids.containsKey(this.sessionId)) {
        this.ids.put(this.sessionId, new SessionData(this.expire, user,
            response.getEntity().toString()));
      }
      return Response.ok(
          gson.toJson(new SessionData(this.sessionId.toString(), response
              .getEntity().toString()))).build();
    }
    return Response.status(403).entity("LogIn failed!").build();
  }

  /**
   * Builds a new sessionId using the Java class UUID.
   * 
   * <p>
   * The global variable "sessionId" is initialized through a random 
   * UUID with the form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx,
   * with x = [a-z,0-9].
   * </p>
   */
  private void buildSessionId() {
    this.sessionId = UUID.randomUUID();
  }

  /**
   * The method uses the GregorianCalendar() to set a expire time for a
   * sessionId.
   *
   * <p>
   * As default the expire time for a sessionId is set to 15 minutes.
   * This could be changed in the line 
   * "this.timer.add(GregorianCalendar.MINUTE, 15);",
   * by editing the value "15".
   * </p>
   */
  private void setTimer() {
    GregorianCalendar timer = new GregorianCalendar();
    timer.add(GregorianCalendar.MINUTE, 15);

    /** test if the sessionId is invalid after a short time. **/
    // timer.add(GregorianCalendar.SECOND, 10);

    this.expire = timer.getTime();
  }

  /**
   * Checks if the time of the given sessionId is still within the 15 minute
   * span. 
   * 
   * <p>
   * Uses the Java class Date() to get the current time and
   * compares it to the expire time of the sessionId.
   * </p>
   * 
   * <p>
   * If the sessionId is expired it will be deleted from the hash map "ids",
   * else a new timer is set to become a new 15 minute time span for the
   * sessionId. In this case the hash map "ids" will be updated for the key
   * "sessionId".
   * </p>
   * 
   * @param sessionId UUID for this session.
   * @return true if sessionId is still valid, else false.
   */
  private boolean checkTime(String sessionId) {
    UUID temp = UUID.fromString(sessionId);
    Date currentDate = new Date();
    String userRole;
    String user;
    if (currentDate.getTime() >= this.ids.get(temp).getExpire().getTime()) {
      this.ids.remove(temp);
      return false;
    }
    userRole = this.ids.get(temp).getRole();
    user = this.ids.get(temp).getUser();
    this.setTimer();
    this.ids.put(temp, new SessionData(this.expire, user, userRole));
    return true;
  }

  /**
   * Checks if the given sessionId is still valid or expired (sessionId expires
   * after 15 minutes).
   * 
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: The given SessionID is still valid.</li>
   * <li>401: Unauthorized:
   *    Header does not contain a sessionId.</li> 
   * <li>403: Request timeout. 
   *    The given sessionId is expired a new log in is required.</li>
   * </p>
   * 
   * @param sessionId
   *          holding a 128bit sessionId with the form
   *          xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, with x = [a-z,0-9].
   * @return a Response Object with a status code and a possible message body.
   */
  @GET
  @Path("validation")
  public Response testSession(@HeaderParam("sessionId") String sessionId) {
    if (sessionId != null) {
      UUID temp = UUID.fromString(sessionId);
      if (this.ids.containsKey(temp)) {
        this.expire = this.ids.get(temp).getExpire();
        if (this.checkTime(temp.toString())) {
          return Response.ok("valid sessionID").build();
        }
      }
      this.logout(sessionId);
      return Response.status(408).entity("sessionID expired!").build();
    } else {
      return Response.status(401)
          .entity("Header does not contain a sessionId!").build();
    }
  }

  /**
   * Returns the user role associated to the given sessionId. The sessionId is
   * located in a Header.
   * 
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: A sessionId exists and the user role is returned. </li>
   * <li>401: Unauthorized: Header does not contain a sessionId. </li>
   * </p>
   * 
   * @param sessionId
   *         UUID to which the user role is associated.
   * @return Response with status 200 and the user role as entity.
   */
  @GET
  @Path("getRole")
  public Response getRole(@HeaderParam("sessionId") String sessionId) {
    if (sessionId != null) {
      UUID temp = UUID.fromString(sessionId);
      return Response.ok(this.ids.get(temp).getRole()).build();
    } else {
      return Response.status(401)
          .entity("Header does not contain a sessionId!").build();
    }
  }

  /**
   * Returns the user name associated to the given sessionId. The sessionId is
   * located in a Header.
   * 
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: A sessionId exists and the user name is returned. </li>
   * <li>401: Unauthorized: Header does not contain a sessionId. </li>
   * </p>
   * 
   * @param sessionId
   *         UUID to which the user role is associated.
   * @return Response with status 200 and the user user as entity.
   */
  @GET
  @Path("getUser")
  public Response getUser(@HeaderParam("sessionId") String sessionId) {
    if (sessionId != null) {
      UUID temp = UUID.fromString(sessionId);
      return Response.ok(this.ids.get(temp).getUser()).build();
    } else {
      return Response.status(401)
          .entity("Header does not contain a sessionId!").build();
    }
  }
  
  /**
   * Deletes the sessionId from HashMap and logs the user out.
   * 
   * <p>
   * Possible HTTP status code:
   * 
   * <li> 200: The sessionId is removed from ids and a entity set. </li>
   * <li> 401: Unauthorized:
   *    Header does not contain a sessionId.</li> 
   * </p>
   * 
   * @param sessionId
   *          UUID to which the user role is associated.
   * @return Response with status 200 if logout is complete, else a status
               code and entity for the error.
   */
  @GET
  @Path("logout")
  public Response logout(@HeaderParam("sessionId") String sessionId) {
    if (sessionId != null) {
      UUID temp = UUID.fromString(sessionId);
      if (this.ids.containsKey(temp)) {
        this.ids.remove(temp);
        return Response.ok().entity("Logout complete!").build();
      } else {
        return Response.status(408).entity("sessionID expired!").build();
      }
    } else {
      return Response.status(401)
          .entity("Header does not contain a sessionId!").build();
    }
  }

  public HashMap<UUID, SessionData> getIdMap() {
    return this.ids;
  }
}
