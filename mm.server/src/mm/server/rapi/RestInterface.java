package mm.server.rapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * This class will called by the controller and execute the equivalent methods of the ganeti class.
 * @author Benedikt Bakker
 *
 */
@Path("/ganeti")
public class RestInterface {

  Ganeti ga = new Ganeti();
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstances() {
    return ga.getInstances();
  }
  
  @GET
  @Path("{instance}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstanceInfo(@PathParam("instance") String instance) {
    return ga.getInstanceInfo(instance);
  }
  
  @GET
  @Path("{instance}/{param}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstanceInfoParam(@PathParam("instance") String instance,
      @PathParam("param") String param) {
    return ga.getInstanceInfoParam(instance,param);
  }
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void createInstance(String json) {
    ga.create(json);
  }
  
  @DELETE
  @Path("{instance}")
  public void deleteInstance(@PathParam("instance") String instance) {
    ga.delete(instance);
  }
  
  @POST
  @Path("{instance}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void rebootInstance(@PathParam("instance") String instance, 
      String type) {
    ga.reboot(instance, type);
  }
  
  @PUT
  @Path("{instance}/start")
  @Consumes(MediaType.APPLICATION_JSON)
  public void startInstance(@PathParam("instance") String instance,
      String type) {
    ga.startup(instance,type);
  }
  
  @PUT
  @Path("{instance}/stop")
  @Consumes(MediaType.APPLICATION_JSON)
  public void stopInstance(@PathParam("instance") String instance,
      String type) {
    ga.shutdown(instance,type);
  }
  
  @PUT
  @Path("{instance}/rename")
  @Consumes(MediaType.APPLICATION_JSON)
  public void renameInstance(@PathParam("instance") String instance, 
      String newName) {
    ga.rename(instance, newName);
  }
}