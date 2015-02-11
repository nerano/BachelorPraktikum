package mm.server;

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
  public String getInstances() {
    String list = ga.getInstances();
    return list;
  }
  
  @POST
  public boolean createInstance(@QueryParam("json") String json) {
    return ga.create(json);
  }
  
  @DELETE
  @Path("{instance}")
  public boolean deleteInstances(@PathParam("instance") String instance) {
    return ga.delete(instance);
  }
  
  @POST
  @Path("{instance}")
  public boolean rebootInstances(@PathParam("instance") String instance, 
      @QueryParam("type") String type) {
    return ga.reboot(instance, type);
  }
  
  @PUT
  @Path("{instance}/start")
  public void startInstances(@PathParam("instance") String instance) {
    ga.startup(instance);
  }
  
  @PUT
  @Path("{instance}/stop")
  public boolean stopInstances(@PathParam("instance") String instance) {
    return ga.shutdown(instance);
  }
  
  @PUT
  @Path("{instance}/rename")
  public boolean renameInstances(@PathParam("instance") String instance, 
      @QueryParam("name") String newName) {
    return ga.rename(instance, newName);
  }
}