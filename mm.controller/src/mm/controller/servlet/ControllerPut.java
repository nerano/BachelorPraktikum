package mm.controller.servlet;

import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mm.controller.main.ControllerData;
import mm.controller.main.Initialize;
import mm.controller.modeling.Config;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.WPort;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/put")
public class ControllerPut {

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	/**
	 * Creates a new Experiment.
	 * 
	 * URI <code> baseuri:port/mm.controller/rest/put/exp </code>
	 * 
	 * Used for creating a new Experiment and adding it to the ExperimentData,
	 * expects a Experiment with ID and a list of nodes in JSON format.
	 * 
	 * Possible HTTP status codes:
	 * 
	 * <li>201: Experiment was successfully created. </li>
	 * <li>409: Experiment with this ID already exists. </li>
	 * 
	 * @param data
	 *            experiment to create in the controller
	 * @return 201 for successful creation, 409 if experiment with this id
	 *         already exists
	 */
	@PUT
	@Path("/exp")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response addNewExperiment(String data) {

	   //TODO Lege Experiment an
	   //TODO VMs hinzufÃ¼gen
	   //TODO VLANS setzen
	   //TODO VMs anlegen
	  
	  Experiment oldExp = gson.fromJson(data, Experiment.class);
	    Config config;
	    String responseString;
		String id = oldExp.getId();
		String user = oldExp.getUser();
		String nodeId;
		boolean success = true;
		String successString = "";
		Response response;
		
		if(oldExp.getId().contains("ä") || 
		   oldExp.getId().contains("ö") || 
		   oldExp.getId().contains("ü")) {
		    return Response.status(500).entity("Umlaut not allowed!").build();
		}
		
		System.out.println(data);

		if (data != null && ControllerData.existsExp(user+id)) {
			responseString = "Experiment with this ID already exists!";
			return Response.status(409).entity(responseString).build();
		} else {
		    
		    // Create new Experiment with full Data
		    Experiment experiment = new Experiment(id, user);
	        experiment.setStatus("stopped");
	        
	        LinkedList<NodeObjects> nodeList = new LinkedList<NodeObjects>();
	        LinkedList<WPort> wPortList = new LinkedList<WPort>();
	        
	        // Transfer all Nodes from the sent experiment to the new experiment
	        for (NodeObjects node : oldExp.getList()) {
	            
	            //TODO DELETE the next line, only for testing purposes
	            node.setConfig(ControllerData.getConfig("WARP+APU"));
	            //////////////////////////////////////////////////////
	            nodeId = node.getId();
	            config = node.getConfig();
	            nodeList.add(ControllerData.getNodeById(nodeId));
	            experiment.addNodeConfig(nodeId, config);
	        }
	        experiment.setNodeList(nodeList);
	        
	        //Check if the nodes are applicable for the chosen configuration
	        for (NodeObjects node : experiment.getList()) {
                config = experiment.getNodeConfig(node.getId());
	            if(!node.isApplicable(config)) {
	                System.out.println("NODE IS NOT APPLICABLE");
	                successString += "Node '" + node.getId() + "' is not applicable for "
	                        + "config: '" + config.getName() + "' \n";
	                success = false;
	            }
            }
	        
	        if(!success) {
	            return Response.status(500).entity(successString).build();
	        }
	        
	        // Transfer all wPorts from the old experiment to the new experiment
	        for (WPort wPort : oldExp.getWports()) {
               wPortList.add(ControllerData.getWportById(wPort.getId()));
            }
	        experiment.setWports(wPortList);
	        
	        
	        // Add a Global VLan to the Experiment
	        response = experiment.addGlobalVlan();
	        if(response.getStatus() != 200) {
	            return response;
	        }
	        
	        
	        // Deploys all Trunk Ports for the Experiment
	        response = experiment.deployAllTrunks();
	        if(response.getStatus() != 200) {
                return response;
            }
	        
	        ControllerData.addExp(experiment);
			responseString = "New Experiment posted/created with ID : " + id;
			System.out.println(responseString);
			return Response.status(201).entity(responseString).build();
		}

		
	}
	
	/**
	 * Stops the experiment.
	 * 
	 * @param expId
	 * @return
	 */
	@PUT
    @Path("/stop")
    @Produces(MediaType.TEXT_PLAIN)
    public Response stopExp(String expId) {
        System.out.println("STOP EXPERIMENT");
        Experiment exp = ControllerData.getExpById(expId);
          
          if (exp == null) {
              String responseString = "404, Experiment not found!";
              return Response.status(404).entity(responseString).build();
          }
          return exp.stop();
    }   
    
	/**
	 * Starts the experiment.
	 * @param expId
	 * @return
	 */
    @PUT
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    public Response startExp(String expId) {
      //TODO Experiment starten
      System.out.println("START EXPERIMENT");
      
      Experiment exp = ControllerData.getExpById(expId);
      
      if (exp == null) {
          String responseString = "404, Experiment not found!";
          return Response.status(404).entity(responseString).build();
      }
      return exp.start();
      
    }
    
    @PUT
    @Path("/unpause")
    @Produces(MediaType.TEXT_PLAIN)
    public Response unpauseExp(String expId) {
        //TODO CHECKEN AUF NEUE KNOTEN
        //TODO KNOTEN ANMACHNE
      return Response.ok().build();
    }
    
    /**
     * Pauses the Experiment.
     * 
     * Turns all Nodes of this experiment off. 
     * @param expId experiment ID from the experiment to pause
     * @return  a Response Object with status code and message body, which reports possible errors
     */
    @PUT
    @Path("/pause")
    @Produces(MediaType.TEXT_PLAIN)
    public Response pauseExp(String expId) {
      //TODO Experiment pausieren
        
        Experiment exp = ControllerData.getExpById(expId);
        
        if (exp == null) {
            String responseString = "404, Experiment not found!";
            return Response.status(404).entity(responseString).build();
        }
        
        if(ControllerData.getExpById(expId).getStatus().equals("stopped")) {
            return exp.firstPause();
        }
        
        if(ControllerData.getExpById(expId).getStatus().equals("running")) {
            return exp.pause();
        }
        
        return Response.status(500).entity("Status was not stopped or running, did nothing").build();
    }

    /**
     * Reloads the config.xml file.
     * 
     * After calling this function new added configurations are available for use.
     * @return
     */
    @GET
    @Path("/reloadConfigs")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reloadConfigs () {
      boolean bool = Initialize.reloadConfigs();
      if(bool) {
        return Response.status(200).entity("Configs were reloaded.").build();
      } else {
        return Response.status(500).entity("Reloading Configs failed!").build();
      }
    }
    
    @GET
    @Path("/reloadTopology")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reloadTopology() {
      boolean bool = Initialize.reloadTopology();
      if(bool) {
        return Response.status(200).entity("Topology was reloaded.").build();
      } else {
        return Response.status(500).entity("Reloading Topology failed!").build();
      }
    }
    
    @GET
    @Path("/reloadNodes")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reloadNodes() {
      boolean bool = Initialize.reloadAllNodes();
      if(bool) {
        return Response.status(200).entity("Nodes were reloaded.").build();
      } else {
        return Response.status(500).entity("Reloading Nodes failed!").build();
      }
    }
    
    @GET
    @Path("/reloadwPorts")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reloadwPorts() {
      boolean bool = Initialize.reloadAllwPorts();
      if(bool) {
        return Response.status(200).entity("wPorts were reloaded.").build();
      } else {
        return Response.status(500).entity("Reloading wPorts failed!").build();
      }
    }
    
	/**
	 * Turns all the components from a given node on.
	 * <p>
	 * Address of this method: baseuri:port/mm.controller/rest/put/turnOn The node
	 * to turn on has to be identified in the message body.
	 * <p>
	 * Possible HTTP status codes:
	 * 
	 * <li>200: All components of the node were turned on. No further data. </li>
	 * <li>404: The node was not found. Additional Data in the message body. </li>
	 * <li>500: Overwrites a 404 status code. Something else happened and the
	 * node could not be turned on. Additional data in the message body. </li>
	 * 
	 * @param data
	 *            Identifier for the node in the message body.
	 * @return  Response Object with a status code and a possible message body.
	 */
	@PUT
	@Path("/turnOn")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
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
						+ " : "
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
	 * baseuri:port/mm.controller/rest/put/turnOn/{component}, where component is a
	 * String with the type of the component. The node, to which the component
	 * belongs, has to be specified in the message body.
	 * <p>
	 * Possible HTTP status codes:
	 * 
	 * <li>200: The component was turned on. No additional data provided. </li>
	 * <li>404: The node or the specified component was not found. Additional
	 * Data in the message body. </li>
	 * <li>500: Overwrites a 404 status code. Something else happened and the
	 * component could not be turned off. Additional data in the message body. </li>
	 * 
	 * @param data
	 *            Identifier for the node in the message body.
	 * @param comp
	 *            Type/Identifier for the component in the node, specified in
	 *            the URI
	 * @return a Response Object with a status code and a possible message body.
	 */
	@PUT
	@Path("/turnOn/{comp}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
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
				responseString += (String) r.getEntity();
				return Response.status(500).entity(responseString).build();
			}  

		} else {
			responseString = "404, Node not found! Node: " + data;
			return Response.status(404).entity(responseString).build();
		}

	}
	   
    /**
     * Turns all the components from a given node off.
     * <p>
     * Address of this method: baseuri:port/mm.controller/rest/put/turnOn The node
     * to turn off has to be identified in the message body.
     * <p>
     * Possible HTTP status codes:
     * 
     * <li>200: All components of the node were turned off. No further data.
     * <li>404: The node was not found. Additional Data in the message body.
     * <li>500: Overwrites a 404 status code. Something else happened and the
     * node could not be turned off. Additional data in the message body.
     * 
     * @param data
     *            Identifier for the node in the message body.
     * @return  Response Object with a status code and a possible message body.
     */
    @PUT
    @Path("/turnOff")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
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
	 * Turns the given Component from the given Node off.
	 * <p>
	 * Address of this method:
	 * <code>baseuri:port/mm.controller/rest/put/turnOff/{component}</code>, where component is a
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
	@Path("/turnOff/{comp}")
	@Produces(MediaType.TEXT_PLAIN)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response turnCompOff(String data, @PathParam("comp") String comp) {

		NodeObjects node = ControllerData.getNodeById(data);
		String responseString;

		if (node != null && ControllerData.exists(node)) {

			Response r = node.turnOff(comp);
			System.out.println("ControllerPUT status : " + r.getStatus());

			if (r.getStatus() == 200) {
				responseString = "Component in the Node turned off";
				return Response.status(200).entity(responseString).build();
			} else {

				responseString = "WARNING: Component was not turned off \n";
				
				responseString += (String) r.getEntity();
				
				System.out.println("ControllerPut Status " + r.getStatus() 
            + " \n Controllerput responseString " + responseString);
				
				
				return Response.status(500).entity(responseString).build();
			}

		} else {
			responseString = "404, Node not found! Node: " + data;
			return Response.status(404).entity(responseString).build();
		}

	}

}
