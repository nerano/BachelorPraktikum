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


public class ControllerPowerPut {

	
	
	private ClientConfig config = new ClientConfig();
	private  Client client = ClientBuilder.newClient(config);


	private WebTarget putTarget = client.target(getPowerPutUri());
	
	/**
	 * Turns on a set of PowerSources. 
	 * <p>
	 * Expects a String in the following form: "[PowerSource;Socket;]end" 
	 * In the following all PowerSources in the string are going to turned on.
	 * To achieve this the parameter String is sent to the PowerService.
	 * <p>
	 * Possible HTTP status codes in the returned Response Object:
	 * 
	 * <li> 200: Everything went as expected and the PowerSources are now turned on
	 * <li> 404: Only one or multiple PowerSources do not exist in the PowerSources under
	 * the given identifier
	 * <li> 500: Some other error occurred(and possible 404s), the response body contains a specified error description
	 *   A 500 ResponseCode overwrites a 404 code.
	 *   
	 * @param parameter A String with all PowerSources to turn on, divided by ";", ending with "end". e.g. [PowerSource1;5;]end
	 * @return a Response Object with a status code and message body
	 */
	public Response turnOn(String parameter) {
	
		// String testString = "TESTAEHOME#1;1;end";
		
		Response response = putTarget.path("turnOn").request().accept(MediaType.TEXT_PLAIN)
											.put(
													Entity.entity(parameter, MediaType.TEXT_PLAIN),
																						Response.class);	
	
		//System.out.println(parameter);
		// System.out.println("POWERTEST " + response.getStatus() + response.readEntity(String.class));
		
		return response;
	}
	/**
   * Turns off a set of PowerSources. 
   * <p>
   * Expects a String in the following form: "[PowerSource;Socket;]end" 
   * In the following all PowerSources in the string are going to turned off.
   * To achieve this the parameter String is sent to the PowerService.
   * <p>
   * Possible HTTP status codes in the returned Response Object:
   * 
   * <li> 200: Everything went as expected and the PowerSources are now turned off.
   *           The message body/entity is empty.
   * <li> 404: Only one or multiple PowerSources do not exist in the PowerSources under
   *           the given identifier. The message body contains a String with further information.
   * <li> 500: Some other error occurred(and possible 404s), the response body contains a specified error description
   *   A 500 ResponseCode overwrites a 404 code.
   *   
   * @param parameter A String with all PowerSources to turn off, divided by ";", ending with "end". e.g. [PowerSource1;5;]end
   * @return a Response Object with a status code and message body
   */
	public Response turnOff(String parameter) {
		
	  
	  
	  
		// String testString = "TESTAEHOME#1;1;end";
		
		Response response = putTarget.path("turnOff").request().accept(MediaType.TEXT_PLAIN)
											.put(
													Entity.entity(parameter, MediaType.TEXT_PLAIN),
																						Response.class);	
	
		
		System.out.println("ControllerPowerPut status " + response.getStatus());
		
		// System.out.println("POWERTEST " + response.getStatus() + response.readEntity(String.class));

		return response;
	
	}

	private static URI getPowerPutUri() {
	    return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/put/").build();
	  }
}
