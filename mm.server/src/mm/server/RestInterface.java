package mm.server;

import org.json.JSONObject;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;

@Path("/ganeti")
public class RestInterface {

  Ganeti ga = new Ganeti();
  
  @GET
  public JSONObject getInstances() {
    JSONObject json = ga.getInstances();
    return json;
  }
  
  @POST
  public void createInstance(@QueryParam("instance") String instance, 
      @QueryParam("disk") String diskTemplate, @QueryParam("mode") String mode, 
      @QueryParam("disks") JSONObject disks, @QueryParam("nics") JSONObject nics) {
    ga.create(instance, diskTemplate, disks, nics, mode);
  }
  
  @DELETE
  @Path("{instance}")
  public void deleteInstances(@PathParam("instance") String instance) {
    ga.delete(instance);
  }
  
  @POST
  @Path("{instance}")
  public void rebootInstances(@PathParam("instance") String instance, 
      @QueryParam("type") String type) {
    ga.reboot(instance, type);
  }
  
  @PUT
  @Path("{instance}/start")
  public void startInstances(@PathParam("instance") String instance) {
    ga.startup(instance);
  }
  
  @PUT
  @Path("{instance}/stop")
  public void stopInstances(@PathParam("instance") String instance) {
    ga.shutdown(instance);
  }
  
  @PUT
  @Path("{instance}/rename")
  public void renameInstances(@PathParam("instance") String instance, 
      @QueryParam("name") String newName) {
    ga.rename(instance, newName);
  }
}