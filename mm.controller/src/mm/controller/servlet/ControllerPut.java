package mm.controller.servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.controller.main.ControllerData;
import mm.controller.modeling.Experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/put")
public class ControllerPut {

	
	 private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	
	/**
	 * Used for creating a new Experiment and adding it to the ExperimentData, expects a Experiment with
	 * ID and a list of nodes in JSON format.
	 * @param exp experiment to create in the controller
	 * @return 201 for successful creation, 409 if experiment with this id already exists
	 */
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/exp")
	public Response addNewExperiment(String exp){
	   
        Experiment experiment = gson.fromJson(exp, Experiment.class);
        String responseString;
        Response response;
        String id = experiment.getId();
        
        
        if(ControllerData.exists(id)) {
        	responseString = "Experiment with this ID already exists";
        	response = Response.status(409).entity(responseString).build();
        	return response;
        } else {
        	ControllerData.addExp(experiment);
        	responseString = "New Experiment posted/created with ID : " + id;
        	response = Response.status(201).entity(responseString).build();
        	return response;
        }
       
	}



}
