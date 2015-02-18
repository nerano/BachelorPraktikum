package mm.auth;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/session")
@Singleton
public class Session {

  private UUID sessionId;
  private GregorianCalendar timer;
  private Date currentDate;
  private Date expire;
  private HashMap<String, Date> ids = new HashMap<String, Date>();
  
  /*@GET
  @Path("getID")
  @Produces(MediaType.TEXT_PLAIN)
  public String test() {
    System.out.println(this.sessionId.toString());
    return this.sessionId.toString();
  }*/
  
  @GET
  @Path("getId")
  @Produces(MediaType.TEXT_PLAIN)
  public String test() {
    return "test";
  }
  
  @GET
  @Path("/createSession")
  @Produces(MediaType.TEXT_PLAIN)
  public String createSessionId() {
    this.buildSessionId();
    this.setTimer();
    if (!this.ids.containsKey(this.sessionId.toString())) {
      this.ids.put(this.sessionId.toString(), this.expire);
    }
    return this.sessionId.toString();
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
    //System.out.println(currentDate.toString());
    //System.out.println(this.expire.toString());
    if (this.currentDate.getTime() >= this.expire.getTime()) {
      this.ids.remove(sessionId);
      return false;
    }
    return true;
  }
  
  @GET
  @Path("{sessionId}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  public String testSession(@PathParam("sessionId") String sessionId) {
    //System.out.println("Local SessionId: " + sessionId);
    //System.out.println("Globale SessionId " + this.sessionId.toString());
    System.out.println(this.ids.containsKey(sessionId));
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
}
