package mm.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.jersey.client.ClientConfig;
//import org.json.simple.JSONObject;
import org.json.JSONObject;



/*import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;*/
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

@Path("/ganeti")
public class Ganeti {
  
  private final String url = "https://localhost:5080/2";
  
  /**
   * 
   * @return list of all instances of the server.
   */
  @GET
  public String getInstances() {
    //String str = "";
    //ClientConfig config = new DefaultClientConfig();
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    JSONObject json = new JSONObject();
    String list = "";
    try { 
      WebTarget target = client.target(UriBuilder.fromPath(url).build());
      //WebResource target = client.resource(UriBuilder.fromPath(url).build());
      json = target.path("instances").request().accept(MediaType.APPLICATION_JSON).get(JSONObject.class);
      //list = json.toString();
     // System.out.println(target.request().accept(MediaType.TEXT_PLAIN).get().getEntity().
      //toString());
      
      System.out.println(list);
      /*
    try {
      String quest = url + "innstances";
      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec(quest);
      BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
      while ((str = br.readLine()) != null) {
        System.out.println(str);
      }*/
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }
  
  /**
   * Creates an instance with the given name.
   * @param instance name of the instance.
   * @param diskTemplate must be either Null, sharedfile, diskless, plain, blockdev, drbd, ext,
   *file or rbd.
   * @param mode must be either import, create or remote-import.
   * @return true if creating a VM was successful otherwise false.
   */
  public boolean create(String instance, String diskTemplate, String mode) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    JSONObject json = new JSONObject();
    try {
      WebTarget target = client.target(url);
      
      json.put("instance_name", instance);
      json.put("__version__", new Integer(1));
      json.put("conflicts_check", true);
      json.put("disk_template", diskTemplate);
      json.put("mode", mode);
      
      target.path("instances").request().post(Entity.json(json));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  /**
   * Starts a given instance.
   * @param instance name of the instance that will be started.
   * @return true if starting the VM was successful otherwise false.
   */
  public boolean startup(String instance) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    JSONObject json = new JSONObject();
    try {
      WebTarget target = client.target(url);
      target.path("instances").path(instance).path("startup").request().put(Entity.json(json));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  /**
   * Stops a given instance.
   * @param instance name of the VM which should be stopped.
   * @return true if stopping the VM was successful otherwise false.
   */
  public boolean shutdown(String instance) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    JSONObject json = new JSONObject();
    try {
      WebTarget target = client.target(url);
      json.put("...", "...");
      target.path("instances").path(instance).path("shutdown").request().put(Entity.json(json));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  /**
   * Deletes a given Instance from the server.
   * @param instance name of the VM which should be deleted.
   * @return true if deleting was successful otherwise false.
   */
  public boolean delete(String instance) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    try {
      WebTarget target = client.target(this.url);
      target.path("instances").path(instance).request().delete();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  /**
   * Reboots a given instance.
   * @param instance name of the VM which should reboot.
   * @param type must be either soft, hard or full for the type of rebooting.
   * @return true if rebooting was successful otherwise false.
   */
  public boolean reboot(String instance, String type) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    JSONObject json = new JSONObject();
    try {
      WebTarget target = client.target(this.url);
      json.put("type", type);
      target.path("instances").path(instance).path("reboot").request().post(Entity.json(json));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  /**
   * Renames a given instance with a new name.
   * @param instance name of the VM which should be renamed.
   * @param newName of the given VM.
   * @return
   */
  public boolean rename(String instance, String newName) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    JSONObject json = new JSONObject();
    try {
      WebTarget target = client.target(this.url);
      json.put("new_name", newName);
      target.path("instances").path(instance).path("rename").request().put(Entity.json(json));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  public static void main(String[] args) {
    Ganeti ga = new Ganeti();
    ga.getInstances();
  }
} 