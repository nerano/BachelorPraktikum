package mm.auth;

import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

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
   * This Method.
   * @param user
   * @param pw
   * @return
   */
  @GET
  @Path("/createSession/{user}/{pw}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response createSessionId(@PathParam("user") String user, @PathParam("pw") String pw) {
    if (this.auth.sayAuth(user, pw)) {
      this.buildSessionId();
      this.setTimer();
      if (!this.ids.containsKey(this.sessionId.toString())) {
        this.ids.put(this.sessionId.toString(), this.expire);
      }
      return Response.ok(this.sessionId.toString()).build();
    }
    return Response.status(403).entity("LogIn failed!").build();
  }
  
  private void buildSessionId() {
    this.sessionId = UUID.randomUUID();
  } 
  
  private void setTimer() {
    this.timer = new GregorianCalendar();
    this.timer.add(GregorianCalendar.MINUTE, 15);
    
    // test ob die Session als ungültig erkannt wird
    //this.timer.add(GregorianCalendar.SECOND, 10);
    
    this.expire = timer.getTime();
    //System.out.println(this.expire.toString());
  }
  
  private boolean checkTime(String sessionId) {
    this.currentDate = new Date();
    if (this.currentDate.getTime() >= this.expire.getTime()) {
      this.ids.remove(sessionId);
      return false;
    }
    return true;
  }
  
  /**
   * This Method.
   * @param sessionId
   * @return
   */
  @GET
  @Path("isValid/{sessionId}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  public String testSession(@PathParam("sessionId") String sessionId) {
    if (this.ids.containsKey(sessionId)) {
      this.expire = this.ids.get(sessionId);
      if (this.checkTime(sessionId)) {
        return "SessionID still valid!";
      }
    }
    return "New LogIn!!!";
  }
  
  public String getSessionId() {
    return this.sessionId.toString();
  }
  
  public URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest/authmain").build();
  }
}
