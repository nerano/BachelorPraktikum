package mm.controller.client;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

@Path("/conjson")
public class ConJSON {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getJSON() {
  
    
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    
    WebTarget jsonTarget = client.target(getBaseUri());
    
   /** int indent = 4;
    String xmlString = 
        "<?xml version=\"1.0\" ?><test attrib=\"moretest\">Turn this to JSON</test>"; 
    JSONObject jsob = XML.toJSONObject(xmlString);
    
    String responseString = jsob.toString(indent);
    
    Response response = Response.ok(responseString, MediaType.APPLICATION_JSON).build();
    
    return response; **/
    
 
    String responseString = jsonTarget.path("mm.net").path("rest").path("netjson").request()
        .get(String.class) + "CONTROLLER";
    
    Response response = Response.ok(responseString, MediaType.APPLICATION_JSON).build();
    
    return response;
    
    
    
    
    
   /** System.out.println(jsonTarget.path("mm.net").path("rest").path("netjson").request()
            .accept(MediaType.APPLICATION_JSON)
            .get(Response.class).toString()); **/
   

  
  
  }
  private static URI getBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/").build();
  }
  
  
}
