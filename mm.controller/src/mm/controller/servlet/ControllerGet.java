package mm.controller.servlet;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.exclusion.NoStatusNodeStrat;
import mm.controller.main.ControllerData;
import mm.controller.modeling.Component;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.PowerSource;
import mm.controller.modeling.VLan;
import mm.controller.net.ControllerNetGet;
import mm.controller.power.ControllerPowerGet;
import mm.controller.main.Initialize;

@Path("/get")
public class ControllerGet {

	private ControllerPowerGet powerGet = new ControllerPowerGet();
	private ControllerNetGet netGet = new ControllerNetGet();
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	@Path("/nodes")
	public Response getAllNodes() {
		
		Response response;
		String responseString;

		Gson gson = new GsonBuilder()/**.setExclusionStrategies (new NoStatusNodeStrat()) **/
				 .setPrettyPrinting().create();
		// TODO TESTEN
		responseString = gson.toJson(mm.controller.main.Initialize.ALL_NODES);

		response = Response.status(200).entity(responseString).build();
		
		return response;
	}

	@GET
	@Produces({ "json/application", "text/plain" })
	@Path("/activeExp/{id}")
	public Response getActiveNodesByExpId(@PathParam("id") String id)
			throws CloneNotSupportedException, UnsupportedEncodingException {
		
		String responseString;
		Response response;

		if (!(ControllerData.exists(id))) {
			responseString = "404, Experiment not found";
			response = Response.status(404).entity(responseString).build();
			return response;
		}
		
		Experiment exp = ControllerData.getById(id);

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

	/** public void merge(LinkedList<NodeObjects> expList, VLan vlan) {

		LinkedList<Component> compList = new LinkedList<Component>();
		Component component;
		LinkedList<String> portList = vlan.getPortList();

		for (String string : portList) {
			component = mm.controller.main.Initialize.getComponent(string);

			for (NodeObjects nodeObjects : expList) {

				compList = nodeObjects.getComponents();

				for (Component component2 : compList) {
					if (component == component2) {
						component2.setvLanId(vlan.getId());
					}
				}

			}

		}

	}

	/** protected void merge(LinkedList<NodeObjects> expList,
			LinkedList<NodeObjects> secondList, String merge) {

		String nodeId;

		for (NodeObjects nodeObjects : expList) {

			nodeId = nodeObjects.getId();

			for (NodeObjects secondObject : secondList) {
				if (secondObject.getId().equals(nodeId)) {

					if (merge.equals("status")) {
						mergeComponentsStatus(nodeObjects, secondObject);
					}

				}
			}

		}

	}

	protected void mergeComponentsStatus(NodeObjects expNode, NodeObjects node) {

		LinkedList<Component> expCompList = expNode.getComponents();
		LinkedList<Component> secondCompList = node.getComponents();

		for (Component component : expCompList) {
			String compType = component.getType();

			for (Component component2 : secondCompList) {
				if (component2.getType().equals(compType)) {
					component.setStatus(component2.getStatus());
				}
			}
		}

	} **/

	@GET
	@Produces({ "json/application", "text/plain" })
	@Path("/exp/{id}")
	public Response getExpById(@PathParam("id") String id) {

		Response response;
		String responseString;

		if (!(ControllerData.exists(id))) {
			responseString = "404, Experiment not found";
			response = Response.status(404).entity(responseString).build();
			return response;
		}

		Gson gson = new GsonBuilder().setExclusionStrategies(new NoStatusNodeStrat())
									 .setPrettyPrinting().create();

		responseString = gson.toJson(ControllerData.getById(id));

		response = Response.status(200).entity(responseString).build();

		return response;
	}
	
	@GET
	@Produces({ "json/application", "text/plain" })
	@Path("/exp")
	public Response getAllExp(){
		
		String responseString;
	
		responseString = gson.toJson(ControllerData.getAllExp());
		
		return Response.status(200).entity(responseString).build();
	}

	/** @GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getJSON() {

		ControllerPowerGet powerget = new ControllerPowerGet();

		LinkedList<NodeObjects> test;
		test = powerget.getAll();
		System.out.println(test.toString());
		return test.toString();

	}**/

}
