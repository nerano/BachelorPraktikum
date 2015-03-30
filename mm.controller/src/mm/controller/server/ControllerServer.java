package mm.controller.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.util.HashMap;

import org.glassfish.jersey.client.ClientConfig;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * This class will by called by the user via a WebInterface and calls the
 * methods of the server class via REST.
 * 
 * @author Benedikt Bakker
 *
 */
public class ControllerServer {

  private static ClientConfig config = new ClientConfig();
  private static Client client = ClientBuilder.newClient(config);
  private static WebTarget target;
  private static Invocation.Builder builder;
  private static Gson gson = new GsonBuilder().create();

  /**
   * Returns a list of all instances on the server.
   * 
   * @param vmserver
   *          the name of the vm server
   * @return the list of all instances on the ganeti server
   */
  public static String getInstances(String vmserver) {
    target = client.target(getBaseUri());
    return target.path(vmserver).request().get(String.class);
  }

  /**
   * Returns a list of all attributes of a given instance.
   * 
   * @param vmserver
   *          the name of the vm server
   * @param instance
   *          the name of the instance which attributes will be shown
   * @return the list of all attributes of an instances
   */
  public static String getInstanceInfo(String vmserver, String instance) {
    target = client.target(getBaseUri());
    return target.path(vmserver).path(instance).request().get(String.class);
  }

  /**
   * Returns the status of a given parameter of an instances on the server.
   * 
   * @param vmserver
   *          the name of the vm server
   * @param instance
   *          name of the instance which parameter will be shown
   * @param param
   *          the name of the status which should be returned
   * @return the attribute of the given parameter of an instance
   */
  public static String getInstanceInfoParam(String vmserver, String instance, String param) {
    target = client.target(getBaseUri());
    return target.path(vmserver).path(instance).path(param).request()
        .get(String.class);
  }

  /**
   * Creates an instance with the given parameters.
   * 
   * @param vmserver
   *          the name of the vm server
   * @param params
   *          a String of a JSONObject with all requested settings of the new
   *          instance
   */
  public static Response createInstance(String vmserver, String instanceName, String templateName,
      int diskSize, int vlanId, String ip) {
    target = client.target(getBaseUri());
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("template", templateName);
    map.put("name", instanceName);
    map.put("bridge", vlanId);
    map.put("size", diskSize);
    if (!ip.isEmpty()) {
      map.put("ip", ip);
    }
    builder = target.path(vmserver).request()
        .header("Content-Type", "application/json");
    return builder.accept("application/json").post(Entity.json(gson.toJson(map)));
  }
  
  /**
   * Deletes a given Instance from the server.
   * 
   * @param vmserver
   *          the name of the vm server
   * @param instance
   *          name of the instance which should be deleted
   */
  public static Response deleteInstance(String vmserver, String instance) {
    target = client.target(getBaseUri());
    return target.path(vmserver).path(instance).request().delete();
  }

  /**
   * Reboots a given instance.
   * 
   * @param vmserver
   *          the name of the vm server
   * @param instance
   *          name of the VM which should reboot
   * @param type
   *          must be a String of a JSONObject with either soft, hard or full
   *          for the type of rebooting
   */
  public static Response rebootInstance(String vmserver, String instance, String type) {
    target = client.target(getBaseUri());
    builder = target.path(vmserver).path(instance).request()
        .header("Content-Type", "application/json");
    return builder.accept("application/json").post(Entity.json(type));
  }

  /**
   * Starts a given instance.
   * 
   * @param vmserver
   *          the name of the vm server
   * @param instance
   *          name of the instance that will be started
   * @param type
   *          must be a String of a JSONObject with settings
   */
  public static Response startInstance(String vmserver, String instance, String type) {
    target = client.target(getBaseUri());
    builder = target.path(vmserver).path(instance).path("start").request()
        .header("Content-Type", "application/json");
    return builder.put(Entity.json(type));
  }

  /**
   * Stops a given instance.
   * 
   * @param vmserver
   *          the name of the vm server
   * @param instance
   *          name of the VM which should be stopped
   * @param type
   *          must be a String of a JSONObject with settings
   */
  public static Response stopInstance(String vmserver, String instance, String type) {
    target = client.target(getBaseUri());
    builder = target.path(vmserver).path(instance).path("stop").request()
        .header("Content-Type", "application/json");
    return builder.accept("application/json").put(Entity.json(type));
  }

  /**
   * Renames a given instance with a new name.
   * 
   * @param vmserver
   *          the name of the vm server
   * @param instance
   *          name of the VM which should be renamed
   * @param newName
   *          of the given VM
   */
  public static Response renameInstance(String vmserver, String instance, String newName) {
    target = client.target(getBaseUri());
    builder = target.path(vmserver).path(instance).path("rename").request()
        .header("Content-Type", "application/json");
    return builder.accept("application/json").put(Entity.json(newName));
  }

  /**
   * Returns a list of all templates of instances.
   * 
   * @return the list of all templates of instances
   */
  public static String getTemplate() {
    target = client.target(getBaseUri());
    return target.path("template").request().get(String.class);
  }

  /**
   * Renames a given instance with a new name.
   * 
   * @param instance
   *          name of the VM which should be renamed
   * @param newName
   *          of the given VM
   */
  public static Response updateTemplate() {
    target = client.target(getBaseUri());
    builder = target.path("template").request()
        .header("Content-Type", "text/plain");
    return builder.accept("text/plain").put(Entity.json(""));
  }
  
  private static URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.server/rest/server")
        .build();
  }
}