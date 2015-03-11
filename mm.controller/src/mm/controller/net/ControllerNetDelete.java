package mm.controller.net;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;


import org.glassfish.jersey.client.ClientConfig;


public class ControllerNetDelete {

    private static ClientConfig config = new ClientConfig();
    private static Client client = ClientBuilder.newClient(config);
    private static WebTarget target = client.target(getBaseUri());

    
    public static Response freeGlobalVLan(int id) {
        
        Response response = target.path("globalVLan/" + id).request().delete( Response.class);
        
        String responseString = response.readEntity(String.class);
        return Response.status(response.getStatus()).entity(responseString).build();
    }
    
    
    private static URI getBaseUri() {
        return UriBuilder.fromUri("http://localhost:8080/mm.net/rest/delete")
                .build();
    }

}
