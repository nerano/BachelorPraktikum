package mm.controller.main;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class Main {

  public static void main(String[] args) {

    
   
      ClientConfig config = new ClientConfig();
      Client client = ClientBuilder.newClient(config);

      
     URI powerputuri = UriBuilder.fromUri("http://localhost:8080/mm.controller/rest/put").build();

      WebTarget putTarget = client.target(powerputuri);
      
      String parameter = "Node A";
      
      Response r = putTarget.path("turnOff").path("APU").request().accept(MediaType.TEXT_PLAIN)
                  
                  .put(
                        Entity.entity(parameter, MediaType.TEXT_PLAIN), Response.class);
       
      
      
      
      
      
        System.out.println("TESTKLASSE");
        System.out.println("AUSGABE : " + r.readEntity(String.class)); 
         
         

   
  }

}
