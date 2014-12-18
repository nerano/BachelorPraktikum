package mm.controller.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class ControllerMain {

  public static void main(String[] args) {

    


    ClientConfig config = new ClientConfig();

    Client client = ClientBuilder.newClient(config);

    WebTarget target = client.target(getBaseURI());


    MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();

    queryParams.add("user", "testUser");
    
    
    String[] acceptString = {MediaType.TEXT_PLAIN};
    String[] acceptStringX = {MediaType.TEXT_XML};
    String[] acceptStringH = {MediaType.TEXT_HTML};
    String[] testUser = {"admin"};
    String[] testPassword = {"pw"};
    
    System.out.println(target.path("rest").path("authmain").request()
    		
    .accept(acceptString).get(Response.class)
    
    .toString());

    System.out.println(target.path("rest").path("authmain").queryParam("user", "admin")
    		
    .queryParam("pw", "pw").request()

    .accept(acceptString).get(String.class));

    //http://localhost:8080/mm.auth/rest/authmain

  }

  private static URI getBaseURI() {

	  // String[] uri = {"http:/", "localhost:8080", "com.john.jersey.first"};
	  
	//  return UriBuilder.fromUri("http://localhost:8080/com.john.jersey.first").build(uri);

	  return UriBuilder.fromUri("http://localhost:8080/mm.auth/").build();
  
  
  
  }
} 