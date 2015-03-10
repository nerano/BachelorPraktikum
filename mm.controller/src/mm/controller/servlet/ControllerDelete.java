package mm.controller.servlet;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.controller.main.ControllerData;

@Path("/delete")
public class ControllerDelete {
	/**
	 * Deletes an experiment.
	 * <p>
	 * Deletes an experiment with the given ID, the message body contains the 
	 * URI: <code>baseuri:port/mm.controller/rest/delete/exp</code> 
	 * The message body contains a human readable form of the status code
	 * <p>
	 * Possible HTTP status codes:
	 * 
	 * <li> 200: Returned if the experiment with the given ID was removed
	 * <li> 404: Returned if there was not experiment with the given ID
	 * @param id Identifier of the Experiment to delete
	 * @return a Response Object
	 */
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/exp")
	public Response deleteExperiment(String id){
		
	  //TODO Experiment löschen
    
    //TODO VLans löschen
    //TODO Strom aus (???)
    //TODO VMs löschen
    //TODO exp
	  
	  
	  
	  
	  Response response;
		String responseString;
		System.out.println(id);
		if(ControllerData.removeExp(id)){
			responseString = "Experiment with ID '" + id + "' was removed";
			response = Response.status(200).entity(responseString).build();
			return response;
		} else {
			responseString = "Experiment with ID '" + id + "' was not found";
			response = Response.status(404).entity(responseString).build();
			return response;
		}
		
	}

}
