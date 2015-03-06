package mm.net.servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/put")
public class NetPut {

	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public Response setVLanConfig(String incoming) {
		
		
		
		
		
		return Response.ok().build();
	}
	
	
}
