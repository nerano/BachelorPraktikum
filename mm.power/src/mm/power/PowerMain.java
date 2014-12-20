package mm.power;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/powermain")
public class PowerMain {


	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayTest() {
		return "POWERtest";
	}


	

}
