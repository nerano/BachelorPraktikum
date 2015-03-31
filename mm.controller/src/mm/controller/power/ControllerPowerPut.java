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

	
	
	private static ClientConfig config = new ClientConfig();
	private static  Client client = ClientBuilder.newClient(config);


	private static WebTarget putTarget = client.target(getPowerPutUri());
	
	/**
	 * Turns on a set of PowerSources. 
	 * <p>
	 * Expects a String in the following form: "[PowerSource;Socket;]end" 
	 * In the following all PowerSources in the string are going to turned on.
	 * To achieve this the parameter String is sent to the PowerService.
	 * <p>
	 * Possible HTTP status codes in the returned Response Object:
	 * 
	 * <li> 200: Everything went as expected and the PowerSources are now turned on </li>
	 * <li> 404: Only one or multiple PowerSources do not exist in the PowerSources under
	 * the given identifier </li>
	 * <li> 500: Some other error occurred(and possible 404s), the response body contains a specified error description
	 *   A 500 ResponseCode overwrites a 404 code. </li>
	 *   
	 * @param parameter A String with all PowerSources to turn on, divided by ";", ending with "end". e.g. [PowerSource1;5;]end
	 * @return an Outbound Response Object with a status code and message body
	 */
	public static Response turnOn(String parameter) {
	
		System.out.println("CONTROLLERPOWERPUT parameter: " + parameter);
		Response response = putTarget.path("turnOn").request().accept(MediaType.TEXT_PLAIN)
											.put(
													Entity.entity(parameter, MediaType.TEXT_PLAIN),
																						Response.class);	
	
		System.out.println("ControllerPowerPut status " + response.getStatus());
		String responseString = response.readEntity(String.class);
        System.out.println(responseString);
		return Response.status(response.getStatus()).entity(responseString).build();
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
   *           The message body/entity is empty. </li>
   * <li> 404: Only one or multiple PowerSources do not exist in the PowerSources under </li>
   *           the given identifier. The message body contains a String with further information.
   * <li> 500: Some other error occurred(and possible 404s), the response body contains a specified error description
   *   A 500 ResponseCode overwrites a 404 code. </li>
   *   
   * @param parameter A String with all PowerSources to turn off, divided by ";", ending with "end". e.g. [PowerSource1;5;]end
   * @return an Outbound Response Object with a status code and message body
   */
	public static Response turnOff(String parameter) {
		
		Response response = putTarget.path("turnOff").request().accept(MediaType.TEXT_PLAIN)
											.put(
													Entity.entity(parameter, MediaType.TEXT_PLAIN),
																						Response.class);	
	
		
		System.out.println("ControllerPowerPut status " + response.getStatus());
		
		String responseString = response.readEntity(String.class);
		
		// System.out.println("POWERTEST " + response.getStatus() + response.readEntity(String.class));

		return Response.status(response.getStatus()).entity(responseString).build();
	
	}

	private static URI getPowerPutUri() {
	    return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/put/").build();
	  }
}
