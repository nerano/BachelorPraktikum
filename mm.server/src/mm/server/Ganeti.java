package mm.server;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
  public String getInstances() {
    target = client.target(url);
    String list = "";
    String id = "";
    List<String> arr = new ArrayList<String>();
    try { 
      list = target.path("instances").request().get(String.class);
      System.out.println(list);
      // extract every instance-id and add this name to an ArrayList
      do {
        if (list.startsWith("id\":")) {
          list = list.substring(6);
          do {
            id += list.charAt(0);
            list = list.substring(1);
          } while (!list.startsWith("\""));
          arr.add(id);
          System.out.println(id);
          id = "";
        } else {
          list = list.substring(1);
          System.out.println(list);
        }
      } while (!list.isEmpty());
      return arr.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Creates an instance with the given name.
   * @param param is the String with all parameters to create an instance.
   * @return true if creating a VM was successful otherwise false.
   */
  public boolean create(String param) {
    target = client.target(url);
    try { 
      //System.out.println(param);
      builder = target.path("instances").request().header("Content-Type", "application/json");
      rep = builder.accept("application/json").post(Entity.json(param));
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
      json.put("name_check", false);
      json.put("ip_check", false);
      
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
    nic: 0:ip=10.10.11.2,mode=bridged,link=br0 
  {"id":"testvm.seemoo.tu-darmstadt.de","uri":"/2/instances/testvm.seemoo.tu-darmstadt.de"}
  {"__version__":1,"name_check":false,"pnode":"pxhost01.seemoo.tu-darmstadt.de","disk_template":"plain",
   "conflicts_check":false,"ip_check":false,"instance_name":"test123.seemoo.tu-darmstadt.de",
   "nics":[{"link":"br0"},{"mode":"bridged"},{"ip":"10.10.11.3"}],"disks":[{"size":5120}],
   "os_type":"debootstrap+wheezy","mode":"create"}*/
  public static void main(String[] args) throws Exception { 
    Ganeti ga = new Ganeti();
    String createJson = "{\"__version__\":1,\"name_check\":false,"
        + "\"pnode\":\"pxhost01.seemoo.tu-darmstadt.de\",\"disk_template\":\"plain\","
        + "\"conflicts_check\":false,\"ip_check\":false,"
        + "\"instance_name\":\"test123.seemoo.tu-darmstadt.de\","
        + "\"nics\":[{\"link\":\"br0\"},{\"mode\":\"bridged\"},{\"ip\":\"10.10.11.3\"}],"
        + "\"disks\":[{\"size\":5120}],\"os_type\":\"debootstrap+wheezy\",\"mode\":\"create\","
        + "\"start\":false}";
    System.out.println(ga.getInstances());
    //System.out.println(ga.create(createJson));
    //ga.startup("testvm.seemoo.tu-darmstadt.de");
    //ga.shutdown("testvm.seemoo.tu-darmstadt.de");
    //ga.rename("test456.seemoo.tu-darmstadt.de", "testvm.seemoo.tu-darmstadt.de");
  }
} 
