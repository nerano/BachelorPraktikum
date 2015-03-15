package mm.controller.net;

import java.net.URI;

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
     * 
     * @param vlan
     * @return
     */
    public static Response addPorts(VLan vlan) {
        
        String data = gson.toJson(vlan);
        
        Response response = target.path("newVlan").request().accept(MediaType.TEXT_PLAIN).put(
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
