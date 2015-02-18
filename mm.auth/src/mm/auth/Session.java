package mm.auth;

import java.util.Date;
import java.util.GregorianCalendar;
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
    return this.sessionId.toString();
  }
  
  public void buildSessionId() {
    this.sessionId = UUID.randomUUID();
  } 
  
  private void setTimer() {
    this.timer = new GregorianCalendar();
    this.timer.add(GregorianCalendar.MINUTE, 15);
    this.expire = timer.getTime();
    //System.out.println(this.expire.toString());
    this.timer = null;
  }
  
  private boolean checkTime() {
    this.currentDate = new Date();
    //System.out.println(currentDate.toString());
    //System.out.println(this.expire.toString());
    if (this.currentDate.getTime() >= this.expire.getTime()) {
      this.sessionId = null;
      return false;
    }
    return true;
  }
  
  @GET
  @Path("{sessionId}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  public String testSession(@PathParam("sessionId") String sessionId) {
    System.out.println("Local SessionId: "+sessionId);
    System.out.println("Globale SessionId "+this.sessionId.toString());
    if (this.checkTime()) {
      if (this.sessionId.toString().equals(sessionId)) {
        return "SessionID still valid!";
      }
    } 
    return "New LogIn!!!";
  }
  
  public String getSessionId() {
    return this.sessionId.toString();
  }
}