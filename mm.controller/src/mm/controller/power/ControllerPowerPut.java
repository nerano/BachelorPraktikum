package mm.controller.power;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ControllerPowerPut {

	
	
	private ClientConfig config = new ClientConfig();
	private  Client client = ClientBuilder.newClient(config);
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public void turnOn(){
		WebTarget postTarget = client.target(getPowerPutUri());
		
		String testString = "TESTAEHOME#1;1;end";
		
		Response response = postTarget.path("turnOff").request().accept(MediaType.TEXT_PLAIN)
											.put(
													Entity.entity(testString, MediaType.TEXT_PLAIN),
																						Response.class);	
	
		
		System.out.println("POWERTEST " + response.getStatus() + response.readEntity(String.class));
	}


	private static URI getPowerPutUri() {
	    return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/put/").build();
	  }
}
