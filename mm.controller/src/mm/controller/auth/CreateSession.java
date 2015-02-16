package mm.controller.auth;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class CreateSession {

  private UUID sessionId;
  //private GregorianCalendar timer = new GregorianCalendar();
  //private Date currentDate;
  //private Date expire;
  
  @GET
  @Path("getID")
  @Produces(MediaType.TEXT_PLAIN)
  public String test() {
    System.out.println(this.sessionId.toString());
    return this.sessionId.toString();
  }
  
  @GET
  @Path("/createSession")
  @Produces(MediaType.TEXT_PLAIN)
  public String sayPlainTextHello() {
    this.buildSessionId();
    return this.sessionId.toString();
  }
  
  public void buildSessionId() {
    this.sessionId = UUID.randomUUID();
  }  
}
