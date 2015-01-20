package mm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

@Path("/ganeti")
public class Ganeti {
  
  private final String url = "https://benedikt:ben2305@pxhost01.seemoo.tu-darmstadt.de:5080/2/";
  
  /**
   * 
   * @return true if connection to server was successful otherwise false.
   */
  public boolean test() {
    try {      
      ClientConfig config = new ClientConfig();
      Client client = ClientBuilder.newClient(config);
      WebTarget target = client.target(url);
      target.path("instances");
      
     // System.out.println(target.request().accept(MediaType.TEXT_PLAIN).get().getEntity().toString());
      System.out.println(target.request(MediaType.TEXT_PLAIN).get().readEntity(String.class));
      
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
    return true;
  }
  
  /**
   * 
   * @return true if creating a VM was successful otherwise false.
   */
  public boolean createVm() {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    WebTarget target = client.target(url);
    JSONObject json = new JSONObject();
    json.put("name", "hans");
    
    target.path("instances").request().post(Entity.json(json));
    
    return false;
  }
  
  /**
   * @param instance name of the instance that will be started.
   * @return true if starting the VM was successful otherwise false.
   */
  public boolean startVm(String instance) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    WebTarget target = client.target(url);
    target.path("instances").path(instance).path("startup").request().put(Entity.text("..."));
    
    return false;
  }
  
  /**
   * 
   * @return true if stopping the VM was successful otherwise false.
   */
  public boolean stopVm(String instance) {
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    WebTarget target = client.target(url);
    target.path("instances").path(instance).path("shutdown").request().put(Entity.text("..."));
    
    return false;
  }
  
  public static void main(String[] args) {
    Ganeti ga = new Ganeti();
    ga.test();
  }
} 