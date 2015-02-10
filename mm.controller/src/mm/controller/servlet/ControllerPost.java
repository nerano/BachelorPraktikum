package mm.controller.servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.controller.main.ExpData;
import mm.controller.modeling.Experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/post")
public class ControllerPost {

	
	 private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	
	/**
	 * Used for creating a new Experiment and adding it to the ExperimentData, expects a Experiment with
	 * ID and a list of nodes in JSON format.
	 * @param exp experiment to create in the controller
	 * @return 201 for successful creation
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("/test")
	public Response addNewExperiment(String exp){
	   
        Experiment experiment = gson.fromJson(exp, Experiment.class);
        
        ExpData.addExp(experiment);
        
	    Response response;
	    String responseString;
	    responseString = "New Experiment posted/created with ID : " + experiment.getId();
        response = Response.status(201).entity(responseString).build();
        
        return response;
	}



}
