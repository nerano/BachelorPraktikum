package mm.controller.servlet;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import mm.controller.main.ControllerData;

@Path("/delete")
public class ControllerDelete {

	@DELETE
	@Path("/exp/{id}")
	public Response deleteExperiment(@PathParam("id") String id){
		Response response;
		String responseString;
		
		if(ControllerData.removeExp(id)){
			responseString = "Experiment with ID " + id + " was removed";
			response = Response.status(200).entity(responseString).build();
			return response;
		} else {
			responseString = "Experiment with ID " + id + " was not found";
			response = Response.status(401).entity(responseString).build();
			return response;
		}
		
	}

}
