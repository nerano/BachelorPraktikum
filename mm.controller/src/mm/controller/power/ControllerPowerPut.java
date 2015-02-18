package mm.controller.power;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;


public class ControllerPowerPut {

	
	
	private ClientConfig config = new ClientConfig();
	private  Client client = ClientBuilder.newClient(config);


	private WebTarget putTarget = client.target(getPowerPutUri());
	
	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public boolean turnOn(String parameter) {
	
		// String testString = "TESTAEHOME#1;1;end";
		
		System.out.println("HALLO TEST OUT PRINT");
		
		String tmp = parameter;
		/** try {
			// tmp = URLDecoder.decode(parameter, "UTF-8");
			//parameter = URLEncoder.encode(tmp, "UTF-8");
			//parameter = URLEncoder.encode("AeHome#1;1;end", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} **/
		
		
		
		Response response = putTarget.path("turnOn").request().accept(MediaType.TEXT_PLAIN)
											.put(
													Entity.entity(parameter, MediaType.TEXT_PLAIN),
																						Response.class);	
	
		System.out.println(parameter);
		System.out.println("POWERTEST " + response.getStatus() + response.readEntity(String.class));
		
		if(response.getStatus() == 200){
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public boolean turnOff(String parameter) {
		
		// String testString = "TESTAEHOME#1;1;end";
		
		String tmp;
		try {
			tmp = URLDecoder.decode(parameter, "UTF-8");
			parameter = URLEncoder.encode(tmp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Response response = putTarget.path("turnOff").request().accept(MediaType.TEXT_PLAIN)
											.put(
													Entity.entity(parameter, MediaType.TEXT_PLAIN),
																						Response.class);	
	
		
		System.out.println("POWERTEST " + response.getStatus() + response.readEntity(String.class));

		if(response.getStatus() == 200){
			return true;
		} else {
			return false;
		}
	
	}

	private static URI getPowerPutUri() {
	    return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/put/").build();
	  }
}
