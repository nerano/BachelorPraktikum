package mm.controller.net;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ControllerNetGet {

	
	  private ClientConfig config = new ClientConfig();
	  Client client = ClientBuilder.newClient(config);
	  
	  WebTarget target = client.target(getBaseUri());
	
	public VLan getVlan(int id){
		
		String sentence = target.path(Integer.toString(id)).request().get(String.class);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		VLan vlan = gson.fromJson(sentence, VLan.class);
		
		return vlan;
		
	}


	private URI getBaseUri() {
	      return UriBuilder.fromUri("http://localhost:8080/mm.net/rest/get").build();
	    }


}
