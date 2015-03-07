package mm.controller.servlet;

import javax.ws.rs.Consumes;
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
	 * Creates a new Experiment.
	 * 
	 * Used for creating a new Experiment and adding it to the ExperimentData,
	 * expects a Experiment with ID and a list of nodes in JSON format.
	 * 
	 * Possible HTTP status codes:
	 * 
	 * <li>201: Experiment was successfully created. <li>409: Experiment with
	 * this ID already exists.
	 * 
	 * @param exp
	 *            experiment to create in the controller
	 * @return 201 for successful creation, 409 if experiment with this id
	 *         already exists
	 */
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/exp")
	public Response addNewExperiment(String exp) {

		Experiment experiment = gson.fromJson(exp, Experiment.class);
		String responseString;
		String id = experiment.getId();

		if (exp != null && ControllerData.exists(experiment)) {
			responseString = "Experiment with this ID already exists";
			return Response.status(409).entity(responseString).build();
		} else {
			ControllerData.addExp(experiment);
			responseString = "New Experiment posted/created with ID : " + id;
			return Response.status(201).entity(responseString).build();
		}

	}
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/turnOff")
	public Response turnNodeOff(String data) {

		NodeObjects node = ControllerData.getNodeById(data);

		String responseString;

		if (node != null && ControllerData.exists(node)) {

			Response r = node.turnOff();

			if (r.getStatus() == 200) {
				responseString = "All Components in the Node turned off";
				return Response.status(200).entity(responseString).build();
			} else {
				responseString = "WARNING: Not all Components were turned off! \n";
				responseString += (String) r.getEntity();
				return Response.status(500).entity(responseString).build();
			}

		} else {
			responseString = "404, Node '" + data + "' not found!";
			return Response.status(404).entity(responseString).build();
		}

	}

	/**
	 * Turns all the components from a given node on.
	 * <p>
	 * Address of this method: baseuri:port/mm.controller/rest/turnOn The node
	 * to turn on has to be identified in the message body.
	 * <p>
	 * Possible HTTP status codes:
	 * 
	 * <li>200: All components of the node were turned on. No further data.
	 * <li>404: The node was not found. Additional Data in the message body.
	 * <li>500: Overwrites a 404 status code. Something else happened and the
	 * node could not be turned off. Additional data in the message body.
	 * 
	 * @param data
	 *            Identifier for the node in the message body.
	 * @return  Response Object with a status code and a possible message body.
	 */
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/turnOn")
	public Response turnNodeOn(String data) {

		NodeObjects node = ControllerData.getNodeById(data);
		String responseString;

		if (!(node == null) && ControllerData.exists(node)) {

			Response r = node.turnOn();

			if (r.getStatus() == 200) {
				responseString = "All Components in the Node turned on";
				return Response.status(200).entity(responseString).build();
			} else {
				responseString = "On Node " + data 
						+ " - "
						+ "WARNING: Not all Components were turned on!  \n";
				responseString += (String) r.getEntity();
				return Response.status(500).entity(responseString).build();
			}

		} else {
			responseString = "404, Node not found! Node " + node;
			return Response.status(404).entity(responseString).build();
		}

	}

	/**
	 * Turns the given Component from the given Node on.
	 * <p>
	 * Address of this method:
	 * baseuri:port/mm.controller/rest/turnOn/{component}, where component is a
	 * String with the type of the component. The node, to which the component
	 * belongs, has to be specified in the message body.
	 * <p>
	 * Possible HTTP status codes:
	 * 
	 * <li>200: The component was turned on. No additional data provided.
	 * <li>404: The node or the specified component was not found. Additional
	 * Data in the message body.
	 * <li>500: Overwrites a 404 status code. Something else happened and the
	 * component could not be turned off. Additional data in the message body.
	 * 
	 * @param data
	 *            Identifier for the node in the message body.
	 * @param comp
	 *            Type/Identifier for the component in the node, specified in
	 *            the URI
	 * @return a Response Object with a status code and a possible message body.
	 */
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/turnOn/{comp}")
	public Response turnCompOn(String data, @PathParam("comp") String comp) {

		NodeObjects node = ControllerData.getNodeById(data);
		String responseString;

		if (node != null && ControllerData.exists(node)) {

			Response r = node.turnOn(comp);

			if (r.getStatus() == 200) {
				responseString = "Component in the Node turned on";
				return Response.status(200).entity(responseString).build();
			} else {

				responseString = "WARNING: Component '" + comp + "' "
										+ "was not turned on " 
										+ "on Node '" + data + "'\n";
				String s = r.readEntity(String.class);
				responseString = responseString + s;
				return Response.status(500).entity(responseString).build();
			}  

		} else {
			responseString = "404, Node not found! Node: " + data;
			return Response.status(404).entity(responseString).build();
		}

	}

	/**
	 * Turns the given Component from the given Node off.
	 * <p>
	 * Address of this method:
	 * baseuri:port/mm.controller/rest/turnOff/{component}, where component is a
	 * String with the type of the component. The node, to which the component
	 * belongs, has to be specified in the message body.
	 * <p>
	 * Possible HTTP status codes:
	 * 
	 * <li>200: The component was turned off. No additional data provided.
	 * <li>404: The node or the specified component was not found. Additional
	 * Data in the message body.
	 * <li>500: Overwrites a 404 status code. Something else happened and the
	 * component could not be turned off. Additional data in the message body.
	 * 
	 * @param data
	 *            Identifier for the node in the message body.
	 * @param comp
	 *            Type/Identifier for the component in the node, specified in
	 *            the URI
	 * @return a Response Object with a status code and a possible message body.
	 */
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/turnOff/{comp}")
	public Response turnCompOff(String data, @PathParam("comp") String comp) {

		NodeObjects node = ControllerData.getNodeById(data);
		String responseString;

		if (node != null && ControllerData.exists(node)) {

			Response r = node.turnOff(comp);

			if (r.getStatus() == 200) {
				responseString = "Component in the Node turned off";
				return Response.status(200).entity(responseString).build();
			} else {

				responseString = "WARNING: Component was not turned off \n";
				responseString += (String) r.getEntity();
				return Response.status(500).entity(responseString).build();
			}

		} else {
			responseString = "404, Node not found! Node: " + data;
			return Response.status(404).entity(responseString).build();
		}

	}

}
