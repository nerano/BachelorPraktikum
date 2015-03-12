package mm.controller.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.auth.WebAuthTest;
import mm.controller.exclusion.NoStatusNodeStrat;
import mm.controller.main.ControllerData;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.PowerSource;
import mm.controller.modeling.VLan;
import mm.controller.net.ControllerNetGet;
import mm.controller.power.ControllerPowerGet;

/**
 * Class for all GET methods in the controller servlet
 * 
 */
@Path("/get")
public class ControllerGet {

	private ControllerPowerGet powerGet = new ControllerPowerGet();
	private ControllerNetGet netGet = new ControllerNetGet();
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Returns all nodes.
	 * <p>
	 * Returns all nodes which are known to the controller; these are all nodes specified 
	 * in the XML-file and parsed on startup. No dynamic information is serialized, only static.
	 * E.g. the VLAN and power status is not serialized, but the location.
	 * 
	 * The Response Object holds a JSON representation of a list, which contains all nodes.
	 *
	 * @return a Response Object with status code 200
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@Path("/nodes")
	public Response getAllNodes() {
	  //if () {
	    
		Gson gson = new GsonBuilder().setExclusionStrategies (new NoStatusNodeStrat())  
				 .setPrettyPrinting().create();
		// TODO TESTEN
		LinkedList<NodeObjects> list = ControllerData.getAllNodesAsList();
		
		String responseString = gson.toJson(list);
		// responseString = ControllerData.getAllNodesAsList().toString();
		return Response.status(200).entity(responseString).build();
	  //}
	}
	/**
	 * 
	 * @param id
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws UnsupportedEncodingException
	 */
	@GET
	@Produces({ "json/application", "text/plain" })
	@Path("/activeExp/{id}")
	public Response getActiveNodesByExpId(@PathParam("id") String id)
			throws CloneNotSupportedException, UnsupportedEncodingException {
		
		String responseString;
		Response response;
		
		

		if (!(ControllerData.existsExp(id))) {
			responseString = "404, Experiment not found";
			response = Response.status(404).entity(responseString).build();
			return response;
		}
		
		Experiment exp = ControllerData.getExpById(id);

		LinkedList<PowerSource> statusList = powerGet.status(exp);
		
		LinkedList<VLan> vlanList = netGet.getVLanFromExperiment(exp);

		// VLan vlan = netGet.getVlan(id);

		exp.updateNodeStatusPower(statusList);
		exp.updateNodeStatusVLan(vlanList);
		
		System.out.println(gson.toJson(statusList));
		System.out.println(gson.toJson(vlanList));
		
		Experiment returnExp = exp.clone();
		LinkedList<NodeObjects> list = returnExp.getList();
		
		for (Iterator<NodeObjects> nodeObject = list.iterator(); nodeObject.hasNext();) {

			NodeObjects object = nodeObject.next();

			System.out.println(returnExp.isNodeActive(object));
			if (!(returnExp.isNodeActive(object))) {
				
				nodeObject.remove();
			}

		} 
		
		responseString = gson.toJson(returnExp);
		response = Response.status(200).entity(responseString).build();
		return response;
	}

	/**
	 * Returns an experiment.
	 * <p>
	 * Returns an experiment with the given ID in JSON format, the ID is passed through the URI
	 * The Response Objects holds the JSON in the message body
	 * URI: baseuri:port/mm.controller/rest/get/exp/{id}
	 * 
	 * Possible HTTP status codes:
	 * 
	 * <li> 200: The requested experiment is located in the message body.
	 * <li> 404: The requested experiment with the given ID does not exist.
	 * @param id  Identifier of the experiment
	 * @return a Response Object
	 */
	@GET
	@Produces({ "json/application", "text/plain" })
	@Path("/exp/{id}")
	public Response getExpById(@PathParam("id") String id) {

		Response response;
		String responseString;

		if (!(ControllerData.existsExp(id))) {
			responseString = "404, Experiment not found";
			response = Response.status(404).entity(responseString).build();
			return response;
		}

		Gson gson = new GsonBuilder()/* .setExclusionStrategies(new NoStatusNodeStrat()) */
									 .setPrettyPrinting().create();

		responseString = gson.toJson(ControllerData.getExpById(id));

		response = Response.status(200).entity(responseString).build();

		return response;
	}
	
	/**
	 * Returns a list of all experiments.
	 * <p>
	 * Returns a list of all currently existing experiments in JSON format. 
	 * No input Parameters are required.
	 * URI: baseuri:port/mm.controller/rest/get/exp
	 * 
	 * @return a Response Object with the JSON in the message body.
	 */
	@GET
	@Produces({ "json/application", "text/plain" })
	@Path("/exp")
	public Response getAllExp(){
		/*
		 * test case if the rolemanagement works
		 */
	  WebAuthTest auth = new WebAuthTest();
	  if (auth.getUserRole().readEntity(String.class).equals("admin")) {
	    String responseString = gson.toJson(ControllerData.getAllExp());
	    return Response.status(200).entity(responseString).build();
	  }
	  
		return Response.status(401).entity("no permission for all experiments!").build();
	}
}
