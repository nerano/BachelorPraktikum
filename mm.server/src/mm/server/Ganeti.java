package mm.server;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.JSONObject;

/*import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;*/

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import javax.ws.rs.core.Response;

public class Ganeti {
  //benedikt:ben2305@
  private static final String url = "http://localhost:5080/2";
  private ClientConfig config;
  private Client client;
  WebTarget target;
  
  /**
   * Creating instances of Client and authenticate with username and password.
   */
  public Ganeti() {
    this.config = new ClientConfig();    
    //this.client = Client.create(config);
    this.client = ClientBuilder.newClient(config);
    HttpAuthenticationFeature feat = HttpAuthenticationFeature.basic("benedikt", "ben2305");
    this.client.register(feat);
  }
  
  /*static {
    //for localhost testing only
    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
      public boolean verify(String hostname, SSLSession sslSession) {
        if (hostname.equals("localhost")) {
          return true;
        }
        return false;
      }
    });
  }*/
  
  /**
   * 
   * @return list of all instances of the server.
   */
  public JSONObject getInstances() {
    target = client.target(url);
    //target = client.resource(url);
    String list = "";
    try { 
      list = target.path("instances").request().get(String.class);
      System.out.println(list);
      list = list.substring(1);
      System.out.println(list);
      JSONObject json = new JSONObject(list);
      System.out.println(json.toString());
      return json;      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
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
    target = client.target(url);
    JSONObject json = new JSONObject();
    try {
      json.put("instance_name", instance);
      json.put("__version__", Integer.valueOf(1));
      json.put("conflicts_check", true);
      json.put("disk_template", diskTemplate);
      json.put("mode", mode);
      //StringEntity quest = new StringEntity(json.toString());
      String quest = "[" + json.toString() + "]";
      System.out.println(quest);
      Response rep = target.path("instances").request().post(Entity.text(quest));
      System.out.println(rep);
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
    target = client.target(url);
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
    target = client.target(url);
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    JSONObject json = new JSONObject();
    try {
      json.put("...", "...");
      target.path("instances").path(instance).path("shutdown").request().put(Entity.json(json));
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
    target = client.target(url);
    try {
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
    target = client.target(url);
    JSONObject json = new JSONObject();
    try {
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
   * @return true if renaming was successful otherwise false.
   */
  public boolean rename(String instance, String newName) {
    target = client.target(url);
    JSONObject json = new JSONObject();
    try {
      json.put("new_name", newName);
      target.path("instances").path(instance).path("rename").request().put(Entity.json(json));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  public static void main(String[] args) throws Exception {
    Ganeti ga = new Ganeti();  
    //ga.getInstances();
    System.out.println(ga.create("test123", null, "create"));
  }
} 
