package mm.controller.server;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * This class will by called by the user via a WebInterface and calls the methods of the server
 * class via REST.
 * @author Benedikt Bakker
 *
 */
@Path("ganeti")
public class ControllerServer {
  
  private static final String url = "http://localhost:8080/mm.server/rest/ganeti";
  private ClientConfig config;
  private Client client;
  private WebTarget target;
  private Invocation.Builder builder;
  
  public ControllerServer() {
    this.config = new ClientConfig();
    this.client = ClientBuilder.newClient(config);
  }

  /**
   * This method will return a list of all instances on the server.
   * @return the list of all instances on the ganeti server.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstances() {
    target = client.target(url);
    return target.request().get(String.class); 
  }
  
  /**
   * Creates an instance with the given parameters.
   * @param params a String of a JSONObject with all requested settings of the new instance.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void createInstance(String params) {
    target = client.target(url);
    builder = target.request().header("Content-Type", "application/json");
    builder.accept("application/json").post(Entity.json(params));
  }
  
  /**
   * Deletes a given Instance from the server.
   * @param instance name of the instance which should be deleted.
   */
  @DELETE
  @Path("{instance}")
  public void deleteInstance(@PathParam("instance") String instance) {
    target = client.target(url);
    target.path(instance).request().delete();
  }
  
  /**
   * Reboots a given instance.
   * @param instance name of the VM which should reboot.
   * @param type must be a String of a JSONObject with either soft, hard or full for the type of
   *rebooting.
   */
  @POST
  @Path("{instance}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void rebootInstance(@PathParam("instance") String instance, 
      String type) {
    target = client.target(url);
    builder = target.path(instance).request()
        .header("Content-Type", "application/json");
    builder.accept("application/json").post(Entity.json(type));
  }
  
  /**
   * Starts a given instance.
   * @param instance name of the instance that will be started.
   * @param type must be a String of a JSONObject with settings.
   */
  @PUT
  @Path("{instance}/start")
  @Consumes(MediaType.APPLICATION_JSON)
  public void startInstance(@PathParam("instance") String instance,
      String type) {
    target = client.target(url);    
    builder = target.path(instance).path("start").request()
        .header("Content-Type", "application/json");
    builder.put(Entity.json(type));
  }
  
  /**
   * Stops a given instance.
   * @param instance name of the VM which should be stopped.
   * @param type must be a String of a JSONObject with settings.
   */
  @PUT
  @Path("{instance}/stop")
  @Consumes(MediaType.APPLICATION_JSON)
  public void stopInstance(@PathParam("instance") String instance,
      String type) {
    target = client.target(url);
    builder = target.path(instance).path("stop").request()
        .header("Content-Type", "application/json");
    builder.accept("application/json").put(Entity.json(type));
  }
  
  /**
   * Renames a given instance with a new name.
   * @param instance name of the VM which should be renamed.
   * @param newName of the given VM.
   */
  @PUT
  @Path("{instance}/rename")
  @Consumes(MediaType.APPLICATION_JSON)
  public void renameInstance(@PathParam("instance") String instance, 
      String newName) {
    target = client.target(url);
    builder = target.path(instance).path("rename").request()
      .header("Content-Type", "application/json");
    builder.accept("application/json").put(Entity.json(newName));
  }
}