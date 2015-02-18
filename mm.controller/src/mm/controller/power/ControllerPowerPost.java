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

public class ControllerPowerPost {



	private ClientConfig config = new ClientConfig();
	private  Client client = ClientBuilder.newClient(config);
	private WebTarget postTarget = client.target(getPowerPostUri());

	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public boolean toggle(String parameter) {
		
		String tmp;
		try {
			tmp = URLDecoder.decode(parameter, "UTF-8");
			parameter = URLEncoder.encode(tmp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Response response = postTarget.path("toggle").request().accept(MediaType.TEXT_PLAIN)
				.post(
						Entity.entity(parameter, MediaType.TEXT_PLAIN),
															Response.class);	
		
		System.out.println("POWERTEST " + response.getStatus() + response.readEntity(String.class));

		if(response.getStatus() == 200){
			return true;
		} else {
			return false;
		}
	}

	private static URI getPowerPostUri() {
	    return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/post/").build();
	  }


}
