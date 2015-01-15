package mm.server;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
  
  private final String url = "pxhost01.seemoo.tu-darmstadt.de/2/";
  
  /**
   * 
   * @return true if connection to server was successful otherwise false.
   */
  public boolean connect() {
    try {
      String info = "info";
      String quest = this.url;
      quest = quest + info;
      
      ClientConfig config = new ClientConfig();
      Client client = ClientBuilder.newClient(config);
      WebTarget target = client.target(quest);
      
      System.out.println();
      
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
    return false;
  }
  
  /**
   * 
   * @return true if starting the VM was successful otherwise false.
   */
  public boolean startVm() {
    return false;
  }
  
  /**
   * 
   * @return true if stopping the VM was successful otherwise false.
   */
  public boolean stopVm() {
    return false;
  }
} 