package mm.server;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;

import org.json.JSONObject;

@Path("/ganeti")
public class RestInterface {

  Ganeti ga = new Ganeti();
  
  @GET
  public JSONObject getInstances() {
    JSONObject json = ga.getInstances();
    return json;
  }
  
  @Path("/create")
  @POST
  public void createInstance(@QueryParam("instance") String instance, 
      @QueryParam("disk") String diskTemplate, @QueryParam("mode") String mode) {
    ga.create(instance, diskTemplate, mode);
  }
  
  @Path("/start")
  @PUT
  public void startInstances(@QueryParam("instances") String instance) {
    ga.startup(instance);
  }
  
  @Path("/stop")
  @PUT
  public void stopInstances(@QueryParam("instances") String instance) {
    ga.shutdown(instance);
  }
  
  @DELETE
  public void deleteInstances(@QueryParam("instances") String instance) {
    ga.delete(instance);
  }
  
  @Path("/reboot")
  @POST
  public void rebootInstances(@QueryParam("instances") String instance, 
      @QueryParam("type") String type) {
    ga.reboot(instance, type);
  }
  
  @Path("/rename")
  @PUT
  public void renameInstances(@QueryParam("instances") String instance, 
      @QueryParam("name") String newName) {
    ga.rename(instance, newName);
  }
}