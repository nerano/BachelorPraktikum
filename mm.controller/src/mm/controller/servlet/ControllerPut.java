package mm.controller.servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.controller.main.ControllerData;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;

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
        
        
        if(exp != null && ControllerData.exists(experiment)) {
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
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/turnOff")
	public Response turnNodeOff(String data) {
		
		NodeObjects node = ControllerData.getNodeById(data);
	
		String responseString;
		
		if(!(node == null) && ControllerData.exists(node)) {
			
			boolean bool = node.turnOff();
			
			if(bool){
				responseString = "All Components in the Node turned off";
				return Response.status(200).entity(responseString).build();
			} else {
				responseString = "WARNING: Not all Components were turned off";
				return Response.status(500).entity(responseString).build();
			}
			
		} else {
			responseString = "404, Node not found!";
			return Response.status(404).entity(responseString).build();
		}
		
		
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/turnOn")
	public Response turnNodeOn(String data) {
		
		NodeObjects node = ControllerData.getNodeById(data);
		String responseString;
		
		if(!(node == null) && ControllerData.exists(node)) {
			
			boolean bool = node.turnOn();
			
			if(bool){
				responseString = "All Components in the Node turned on";
				return Response.status(200).entity(responseString).build();
			} else {
				responseString = "WARNING: Not all Components were turned on";
				return Response.status(500).entity(responseString).build();
			}
			
		} else {
			responseString = "404, Node not found! Node " + node;
			return Response.status(404).entity(responseString).build();
		}
		
		
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/turnOn/{comp}")
	public Response turnCompOn(String data, @PathParam("comp") String comp) {
		
		NodeObjects node = ControllerData.getNodeById(data);
		String responseString;
		
		if(!(node == null) && ControllerData.exists(node)) {
			
			if(node.turnOn(comp)){
				responseString = "Component in the Node turned on";
				return Response.status(200).entity(responseString).build();
			} else {
				responseString = "WARNING: Component were not turned on";
				return Response.status(500).entity(responseString).build();
			}
			
		} else {
			responseString = "404, Node not found!";
			return Response.status(404).entity(responseString).build();
		}
		
		
	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/turnOff/{comp}")
	public Response turnCompOff(String data, @PathParam("comp") String comp) {
		
		NodeObjects node = ControllerData.getNodeById(data);
		String responseString;
		
		if(!(node == null) && ControllerData.exists(node)) {
			
			if(node.turnOff(comp)){
				responseString = "Component in the Node turned off";
				return Response.status(200).entity(responseString).build();
			} else {
				responseString = "WARNING: Component were not turned off";
				return Response.status(500).entity(responseString).build();
			}
			
		} else {
			responseString = "404, Node not found!";
			return Response.status(404).entity(responseString).build();
		}
		
		
	}



}
