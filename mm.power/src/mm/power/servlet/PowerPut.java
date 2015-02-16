package mm.power.servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.power.modeling.PowerSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Path("/put")
public class PowerPut {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/turn")
	public Response turnOn(String ps){
		
		Response response = null;
		PowerSource powerSource = gson.fromJson(ps, PowerSource.class);
		
		
		
	
		return response;
	}
	

	//turnon
	//turnoff
	
	


}
