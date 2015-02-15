package mm.server;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

/**
 * This class will be called by the RestInterface class and communicates with the RAPI of the 
 * Ganeti server via REST.
 * @author Benedikt Bakker
 *
 */
public class Ganeti {
  //benedikt:ben2305@
  private static final String url = "http://localhost:5080/2";
  private ClientConfig config;
  private Client client;
  private Invocation.Builder builder;
  private WebTarget target;
  
  /**
   * Creating instances of Client and authenticate with user name and password.
   */
  public Ganeti() {
    this.config = new ClientConfig();
    this.client = ClientBuilder.newClient(config);
    HttpAuthenticationFeature feat = HttpAuthenticationFeature.basic("benedikt", "ben2305");
    this.client.register(feat);
  }
  
  /**
   * 
   * @return list of all instances of the server.
   */
  public String getInstances() {
    target = client.target(url);
    String list = "";
    String id = "";
    List<String> arr = new ArrayList<String>();
    try { 
      list = target.path("instances").request().get(String.class);
      // extract every instance-id and add this name to an ArrayList
      do {
        if (list.startsWith("id\":")) {
          list = list.substring(6);
          do {
            id += list.charAt(0);
            list = list.substring(1);
          } while (!list.startsWith("\""));
          arr.add(id);
          id = "";
        } else {
          list = list.substring(1);
        }
      } while (!list.isEmpty());
      return arr.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }
  
  /**
   * Creates an instance with the given parameters.
   * @param param is the String with all parameters to create an instance.
   */
  public void create(String param) {
    target = client.target(url);
    try {
      builder = target.path("instances").request().header("Content-Type", "application/json");
      builder.accept("application/json").post(Entity.json(param));      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
 
  
  /**
   * Starts a given instance.
   * @param instance name of the instance that will be started.
   * @param type must be a String of a JSONObject with settings.
   */
  public void startup(String instance, String type) {
    target = client.target(url);
    try {
      builder = target.path("instances").path(instance).path("startup").request()
          .header("Content-Type", "application/json");
      builder.accept("application/json").put(Entity.json(type));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Stops a given instance.
   * @param instance name of the VM which should be stopped.
   * @param type must be a String of a JSONObject with settings.
   */
  public void shutdown(String instance, String type) {
    target = client.target(url);
    try {
      builder = target.path("instances").path(instance).path("shutdown").request()
          .header("Content-Type", "application/json");
      builder.accept("application/json").put(Entity.json(type));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Deletes a given Instance from the server.
   * @param instance name of the VM which should be deleted.
   */
  public void delete(String instance) {
    target = client.target(url);
    try {
      target.path("instances").path(instance).request().delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Reboots a given instance.
   * @param instance name of the VM which should reboot.
   * @param type must be a String of a JSONObject with either soft, hard or full for the type of
   *rebooting.
   */
  public void reboot(String instance, String type) {
    target = client.target(url);
    try {
      builder = target.path("instances").path(instance).path("reboot").request()
          .header("Content-Type", "application/json");
      builder.accept("application/json").post(Entity.json(type));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Renames a given instance with a new name.
   * @param instance name of the VM which should be renamed.
   * @param newName of the given VM.
   */
  public void rename(String instance, String newName) {
    target = client.target(url);
    try {
      builder = target.path("instances").path(instance).path("rename").request()
          .header("Content-Type", "application/json");
      builder.accept("application/json").put(Entity.json(newName));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
} 
