package mm.controller.auth;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("session")
public class Session {

  private String sessionId;
  private GregorianCalendar timer = new GregorianCalendar();
  private Date currentDate;
  private Date expire;

  /*@GET
  @Path("here")
  public String test() {
    return "TEST HERE!";
  }*/
  
  private void setTimer() {
    this.timer.add(GregorianCalendar.MINUTE, 15);
    this.expire = timer.getTime();
    this.timer = null;
  }
  
  private boolean checkTime() {
    this.currentDate = new Date();
    if(this.currentDate.getTime() >= this.expire.getTime()) {
      //this.sessionId = null;
      return false;
    }
    return true;
  }
  
  @GET
  @Path("{sessionId}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  public String testSession(@PathParam("sessionId") String sessionId) {
    System.out.println("test");
    this.setTimer();
    //System.out.println(this.sessionId.toString());
    if(this.checkTime()) {
      if(this.sessionId.equals(sessionId)) {
        return "SessionID still valid!";
      }
    } 
    return "New LogIn!!!";
  }
  
  /*@GET
  @Path("{b2bd89c0-d0ba-47cd-9998-257e2d0dc6a3}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  public String test(@PathParam("b2bd89c0-d0ba-47cd-9998-257e2d0dc6a3") String test) {
    return test;
  }*/
}
