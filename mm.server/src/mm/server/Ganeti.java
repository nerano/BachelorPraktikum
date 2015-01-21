package mm.server;

import org.glassfish.jersey.client.ClientConfig;
import org.json.simple.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ganeti")
public class Ganeti {
  
  private final String url = "https://localhost:5080/2/";
  
  /**
   * 
   * @return list of all instances of the server.
   */
  @GET
  public String getInstances() {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    String list = "";
    try { 
      WebTarget target = client.target(url);
      target.path("instances");
      list = target.request(MediaType.TEXT_PLAIN).get().readEntity(String.class);
      
     // System.out.println(target.request().accept(MediaType.TEXT_PLAIN).get().getEntity().
      //toString());
      
      System.out.println(list);
      
      /*String quest = url + "info";
      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec(quest);
      BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
      String str;
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