package mm.controller.net;

import java.net.URI;
import java.util.LinkedList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import mm.controller.modeling.VLan;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ControllerNetPut {

    
    private static ClientConfig config = new ClientConfig();
    private static Client client = ClientBuilder.newClient(config);
    private static WebTarget target = client.target(getBaseUri());

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    
    
    
    /**
     * Sets all the given Ports of the VLan as Trunk Ports.
     * @param vlan
     * @return
     */
    public static Response setTrunkPort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        
        Response response = target.path("setTrunkPort").request().accept(MediaType.TEXT_PLAIN).put(
                                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
        
    }
    
    /**
     * Adds the given VLan as TrunkPorts.
     * @param vlan  a VLan with ID and a list of Ports
     * @return
     */
    public static Response addTrunkPort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("addTrunkPort").request().accept(MediaType.TEXT_PLAIN).put(
                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
        
        
    }
    
 public static Response setPort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("setPort").request().accept(MediaType.TEXT_PLAIN).put(
                                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
        
    }
    
    
    public static Response addPort(LinkedList<String> ports, int vlanId) {
        
        VLan vlan = new VLan(vlanId);
        vlan.addPorts(ports);
        return addPort(vlan);
        
    }
    
    public static Response addPort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("addPort").request().accept(MediaType.TEXT_PLAIN).put(
                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
    }
    
    public static Response removePort(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("removePort").request().accept(MediaType.TEXT_PLAIN).put(
                Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                    Response.class);
        
        String responseString = response.readEntity(String.class);
        
        return Response.status(response.getStatus()).entity(responseString).build();
        
        
    }
    
    
   public static Response removeVlan(VLan vlan) {
      
       String data = gson.toJson(vlan);
      
       Response response = target.path("removeVLan").request().accept(MediaType.TEXT_PLAIN).put(
               Entity.entity(data, MediaType.TEXT_PLAIN), 
                                                   Response.class);
       
       String responseString = response.readEntity(String.class);
       
       return Response.status(response.getStatus()).entity(responseString).build();
       
   }
    
    private static URI getBaseUri() {
        return UriBuilder.fromUri("http://localhost:8080/mm.net/rest/put")
                .build();
    }
    
    
}
