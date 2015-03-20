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
@Path("server")
public class ControllerServer {
  
  private static final String url = "http://localhost:8080/mm.server/rest/server";
  private ClientConfig config;
  private Client client;
  private WebTarget target;
  private Invocation.Builder builder;
  
  public ControllerServer() {
    this.config = new ClientConfig();
    this.client = ClientBuilder.newClient(config);
  }

  /**
   * Returns a list of all instances on the server.
   * @return the list of all instances on the ganeti server.
   */
  @GET
  @Path("ganeti")
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstances() {
    target = client.target(url);
    return target.path("ganeti").request().get(String.class); 
  }
  
  /**
   * Returns a list of all attributes of a given instance.
   * @param instance the name of the instance which attributes will be shown.
   * @return the list of all attributes of an instances.
   */
  @GET
  @Path("ganeti/{instance}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstanceInfo(@PathParam("instance") String instance) {
    target = client.target(url);
    return target.path("ganeti").path(instance).request().get(String.class); 
  }
  
  /**
   * Returns the status of a given parameter of an instances on the server.
   * @param instance name of the instance which parameter will be shown.
   * @param param the name of the status which should be returned.
   * @return the attribute of the given parameter of an instance.
   */
  @GET
  @Path("ganeti/{instance}/{param}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getInstanceInfoParam(@PathParam("instance") String instance,
      @PathParam("param") String param) {
    target = client.target(url);
    return target.path("ganeti").path(instance).path(param).request().get(String.class); 
  }
  
  /**
   * Creates an instance with the given parameters.
   * @param params a String of a JSONObject with all requested settings of the new instance.
   */
  @POST
  @Path("ganeti")
  @Consumes(MediaType.APPLICATION_JSON)
  public void createInstance(String params) {
    target = client.target(url);
    builder = target.path("ganeti").request().header("Content-Type", "application/json");
    builder.accept("application/json").post(Entity.json(params));
  }
  
  /**
   * Deletes a given Instance from the server.
   * @param instance name of the instance which should be deleted.
   */
  @DELETE
  @Path("ganeti/{instance}")
  public void deleteInstance(@PathParam("instance") String instance) {
    target = client.target(url);
    target.path("ganeti").path(instance).request().delete();
  }
  
  /**
   * Reboots a given instance.
   * @param instance name of the VM which should reboot.
   * @param type must be a String of a JSONObject with either soft, hard or full for the type of
   *rebooting.
   */
  @POST
  @Path("ganeti/{instance}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void rebootInstance(@PathParam("instance") String instance, 
      String type) {
    target = client.target(url);
    builder = target.path("ganeti").path(instance).request()
        .header("Content-Type", "application/json");
    builder.accept("application/json").post(Entity.json(type));
  }
  
  /**
   * Starts a given instance.
   * @param instance name of the instance that will be started.
   * @param type must be a String of a JSONObject with settings.
   */
  @PUT
  @Path("ganeti/{instance}/start")
  @Consumes(MediaType.APPLICATION_JSON)
  public void startInstance(@PathParam("instance") String instance,
      String type) {
    target = client.target(url);    
    builder = target.path("ganeti").path(instance).path("start").request()
        .header("Content-Type", "application/json");
    builder.put(Entity.json(type));
  }
  
  /**
   * Stops a given instance.
   * @param instance name of the VM which should be stopped.
   * @param type must be a String of a JSONObject with settings.
   */
  @PUT
  @Path("ganeti/{instance}/stop")
  @Consumes(MediaType.APPLICATION_JSON)
  public void stopInstance(@PathParam("instance") String instance,
      String type) {
    target = client.target(url);
    builder = target.path("ganeti").path(instance).path("stop").request()
        .header("Content-Type", "application/json");
    builder.accept("application/json").put(Entity.json(type));
  }
  
  /**
   * Renames a given instance with a new name.
   * @param instance name of the VM which should be renamed.
   * @param newName of the given VM.
   */
  @PUT
  @Path("ganeti/{instance}/rename")
  @Consumes(MediaType.APPLICATION_JSON)
  public void renameInstance(@PathParam("instance") String instance, 
      String newName) {
    target = client.target(url);
    builder = target.path("ganeti").path(instance).path("rename").request()
      .header("Content-Type", "application/json");
    builder.accept("application/json").put(Entity.json(newName));
  }
  
  /**
   * Returns a list of all templates of instances.
   * @return the list of all templates of instances.
   */
  @GET
  @Path("template")
  @Produces({MediaType.APPLICATION_JSON})
  public String getTemplate() {
    target = client.target(url);
    return target.path("template").request().get(String.class); 
  }
  
  /**
   * Renames a given instance with a new name.
   * @param instance name of the VM which should be renamed.
   * @param newName of the given VM.
   */
  @PUT
  @Path("template")
  public void updateTemplate() {
    target = client.target(url);
    builder = target.path("template").request()
      .header("Content-Type", "text/plain");
    builder.accept("text/plain").put(Entity.json(""));
  }
}
