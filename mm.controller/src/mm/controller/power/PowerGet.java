package mm.controller.power;

import java.net.URI;
import java.util.LinkedList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PowerGet {


  private ClientConfig config = new ClientConfig();
  Client client = ClientBuilder.newClient(config);
  
  WebTarget powerTarget = client.target(getBaseUri());
    
  
  
  
  
  public LinkedList<Node> getAll() {
   
    
      String powerString = powerTarget.path("get").request().get(String.class);
      
      
      Gson gson = new Gson();
      
      LinkedList<Node> nodeList = new LinkedList<Node>();
      
      nodeList = gson.fromJson(powerString, LinkedList.class);
    
      return nodeList;
      
        
        
  }
  
  public void getById(int id){
      
      
      String responseString = powerTarget.path("get").path(Integer.toString(id))
              .request().get(String.class);
      
      
        
        
  }
  
  
  

  private static URI getBaseUri() {
      return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/").build();
    }
    


}
