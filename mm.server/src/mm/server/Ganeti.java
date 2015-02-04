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
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class Ganeti {
  //benedikt:ben2305@
  private static final String url = "http://localhost:5080/2";
  private ClientConfig config;
  private Client client;
  private Invocation.Builder builder;
  private Response rep;
  private WebTarget target;
  
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
      list = list.substring(1);
      JSONObject json = new JSONObject(list);
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
   * @param disks must be a JSONObject with key of size and 
   *may be a key of mode with value either ro or rw.
   *@param nics must be a JSONObject with keys one of name, ip, mac, link, mode and network
   *and values of Strings, either empty or not.
   * @return true if creating a VM was successful otherwise false.
   */
  public boolean create(String instance, String diskTemplate, JSONObject disks, JSONObject nics,
      String mode) {
    target = client.target(url);
    JSONObject json = new JSONObject();
    try {
      json.put("instance_name", instance);
      json.put("__version__", Integer.valueOf(1));
      json.put("conflicts_check", true);
      json.put("disk_template", diskTemplate);
      json.put("mode", mode);
      json.put("nics", nics);
      json.put("disks", disks);
      json.put("os_type", "debootstrap+wheezy");
      json.put("pnode", "pxhost01.seemoo.tu-darmstadt.de");
      
      builder = target.path("instances").request().header("Content-Type", "application/json");
      rep = builder.accept("application/json").post(Entity.json(json.toString()));
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
    JSONObject json = new JSONObject();
    try {
      builder = target.path("instances").path(instance).path("startup").request()
          .header("Content-Type", "application/json");
      rep = builder.accept("application/json").put(Entity.json(json.toString()));
      System.out.println(rep);
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
    JSONObject json = new JSONObject();
    try {
      builder = target.path("instances").path(instance).path("shutdown").request()
          .header("Content-Type", "application/json");
      rep = builder.accept("application/json").put(Entity.json(json.toString()));
      System.out.println(rep);
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
      
      builder = target.path("instances").path(instance).path("reboot").request()
          .header("Content-Type", "application/json");
      rep = builder.accept("application/json").post(Entity.json(json.toString()));
      System.out.println(rep);
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
      
      builder = target.path("instances").path(instance).path("rename").request()
          .header("Content-Type", "application/json");
      rep = builder.accept("application/json").put(Entity.json(json.toString()));
      System.out.println(rep);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  /*disk_template: plain,
    disk: 0:size=5G
    nic: 0:ip=10.10.11.2,mode=bridged,link=br0 */
  //{"id":"testvm.seemoo.tu-darmstadt.de","uri":"/2/instances/testvm.seemoo.tu-darmstadt.de"}
  public static void main(String[] args) throws Exception { 
    JSONObject disks = new JSONObject();
    JSONObject nic = new JSONObject();
    disks.put("size", Integer.valueOf(5120));
    disks.put("size", Integer.valueOf(1024));
    nic.put("ip", "10.10.11.3");
    nic.put("mode", "bridged");
    nic.put("link", "br0");
    Ganeti ga = new Ganeti(); 
    //System.out.println(ga.getInstances());
    System.out.println(ga.create("test123.seemoo.tu-darmstadt.de", "plain", disks, nic, "create"));
    //ga.startup("testvm.seemoo.tu-darmstadt.de");
    //ga.shutdown("testvm.seemoo.tu-darmstadt.de");
    //ga.rename("testvm.seemoo.tu-darmstadt.de", "test456.seemoo.tu-darmstadt.de");
  }
} 
