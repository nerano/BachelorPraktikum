package mm.controller.client;


import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.json.*;




public class ControllerMain {
  /**
   * The main of the Controller.
   * @param args argument of main
   */
 
    
  static ClientConfig config = new ClientConfig();
  static Client client = ClientBuilder.newClient(config);
  
    
    
  public static void main(String[] args) {

    // ClientConfig config = new ClientConfig();
    // Client client = ClientBuilder.newClient(config);
   // WebTarget target = client.target(getBaseUri());
   // MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
   // queryParams.add("user", "testUser");

  
    testNet();
    // testAuth();
    // testPower();
    
    //System.out.println(authTarget.request().post(Entity<T>.entity(testliste,MediaType.TEXT_PLAIN))
    //    .toString());

 
  }
  
  private static void testPower() {
        

    WebTarget target = client.target(getPowerBaseUri());
        
    System.out.println(target.request()
            .accept(MediaType.TEXT_PLAIN)
            .get(Response.class).toString());

    System.out.println(target.request()
                .accept(MediaType.TEXT_PLAIN).get(String.class));
   
    return;
  }
     
  private static void testAuth() {
        
        
    WebTarget authTarget = client.target(getAuthBaseUri());

    System.out.println(authTarget.request()
            .accept(MediaType.TEXT_PLAIN)
            .get(Response.class).toString());

    System.out.println(authTarget.queryParam("user", "admin").queryParam("pw", "pw").request()
            .accept(MediaType.TEXT_PLAIN).get(String.class));
          
          
    return;
  } 
  
  private static void testNet() {
    
    WebTarget target = client.target(getNetBaseUri());
    
    System.out.println(target.request()
              .accept(MediaType.TEXT_PLAIN)
              .get(Response.class).toString());

    System.out.println(target.request()
            .accept(MediaType.TEXT_PLAIN).get(String.class));
    
    WebTarget jsonTarget = client.target(getBaseUri());
    
    System.out.println(jsonTarget.path("mm.net").path("rest").path("netjson").request()
            .accept("application/json")
            .get(Response.class).toString());
    
    String response = jsonTarget.path("mm.net").path("rest").path("netjson").request()
            .get(String.class);
      
   //  String jsonPrettyPrintString = jsonObjekt.toString(PRETTY_PRINT_INDENT_FACTOR);
    System.out.println(response); 
    
    
    
    JSONObject jo = new JSONObject(response);
    System.out.println(jo.toString(4));
    return;
  }
  
  
  
  private static URI getBaseUri() {
    // Specify the BaseURI. For Testing Purposes "http://localhost:8080/"
    // URI for the Power "http://localhost:8080/mm.power/rest/powermain"
    // URI for the Auth "http://localhost:8080/mm.auth/rest/authmain"
    // URI for the Net "httP://localhost:8080/mm.net/rest/netmain"
    return UriBuilder.fromUri("http://localhost:8080/").build();
  }

  private static URI getAuthBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.auth/rest/authmain").build();
  }

  private static URI getPowerBaseUri() {
    return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/powermain").build();
  }

  private static URI getNetBaseUri() {

    return UriBuilder.fromUri("http://localhost:8080/mm.net/rest/netmain").build();
  }
}