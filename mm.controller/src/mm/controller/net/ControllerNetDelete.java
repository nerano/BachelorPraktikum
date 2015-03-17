package mm.controller.net;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;



import mm.controller.modeling.VLan;

import org.glassfish.jersey.client.ClientConfig;

/**
 * 
 * 
 *
 */
public class ControllerNetDelete {

    private static ClientConfig config = new ClientConfig();
    private static Client client = ClientBuilder.newClient(config);
    private static WebTarget target = client.target(getBaseUri());

    /**
     * Frees a VLan for further use and deletes it from all NetComponents.
     * @param id
     * @return
     */
    public static Response freeGlobalVlan(int id) {
        
        Response response = target.path("globalVLan/" + id).request().delete(Response.class);
       
        if(response.getStatus() == 200) {
            System.out.println("Controller: Could delete globalVLan " + id);
        }
        
        
        String responseString = response.readEntity(String.class);
        return Response.status(response.getStatus()).entity(responseString).build();
    }
    
    
    public static Response freeLocalVlan(int id) {
        Response response = target.path("localVLan/" + id).request().delete(Response.class);
        
        if(response.getStatus() == 200) {
            System.out.println("Controller: Could delete local VLan " + id);
        }
        
        String responseString = response.readEntity(String.class);
        return Response.status(response.getStatus()).entity(responseString).build();
    }
    
    
    private static URI getBaseUri() {
        return UriBuilder.fromUri("http://localhost:8080/mm.net/rest/delete")
                .build();
    }

}
