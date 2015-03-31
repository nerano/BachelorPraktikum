package mm.controller.servlet;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
import mm.controller.modeling.Experiment.PossibleState;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.WPort;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/put")
public class ControllerPut {

  private Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * 
   * @param data
   * @return
   */
  @PUT
  @Path("/editExp")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response editExp(String data) {

    System.out.println("DATA IN EDIT EXP " + data);

    Initialize.saveToDisk();
    Experiment newExp = gson.fromJson(data, Experiment.class);
    Experiment oldExp = (ControllerData.getExpById(newExp.getId()));

    // Does the old Experiment exists
    if (oldExp == null) {
      return Response.status(404).entity("Could not find Experiment with name "
          + newExp.getName()).build();
    }
    
    // Is there a default Config
    if(newExp.getDefaultConfig().getName().equals("")) {
        return Response.status(500).entity("Please specify a default Config!").build();
    }
    
    // Update the name / ID of the experiment
    ControllerData.getExpById(newExp.getId()).setName(newExp.getName());
    ControllerData.getExpById(newExp.getId()).setId(newExp.getUser() + newExp.getName());

    PossibleState expState = oldExp.getStatus();

    // Can only edit in paused and stopped
    switch (expState) {
    case running:
      return Response.status(403).
          entity("Can not do this in status " + expState.toString()).build();
    case stopped:
      Initialize.saveToDisk();
      return editExpStopped(oldExp, newExp);
    case paused:
      Initialize.saveToDisk();
      return editExpStopped(oldExp, newExp);
    default:
      return Response.status(500).entity("Could not determine status").build();
    }
  }

  /**
   * 
   * @param oldExp
   * @param newExp
   * @return
   */
  private Response editExpStopped(Experiment oldExp, Experiment newExp) {

    // Changing Default Config //
    Config oldDefaultConfig = oldExp.getDefaultConfig();
    Config newDefaultConfig = ControllerData.getConfig(newExp.getDefaultConfig().getName());

    boolean defaultConfigChanged = false;

    if (oldDefaultConfig == null && newDefaultConfig == null) {
      oldExp.setDefaultConfig(newDefaultConfig);
    } else {
        if(oldDefaultConfig == null) {
            oldExp.setDefaultConfig(newDefaultConfig);
            defaultConfigChanged = true;
        } else {
            if (!oldDefaultConfig.equals(newDefaultConfig)) {
                defaultConfigChanged = true;
                oldExp.setDefaultConfig(newDefaultConfig);
              }
        }
    }

    if (defaultConfigChanged) {

      for (NodeObjects node : newExp.getNodes()) {
        if (node.getConfig().getName().equals("")) {
          node.setConfig(newExp.getDefaultConfig());
        }
      }

    }

    // Adding and removing WPORTS //
    LinkedList<WPort> oldWports = oldExp.getWports();
    LinkedList<WPort> newWports = new LinkedList<WPort>();

    for (WPort wPort : newExp.getWports()) {
      newWports.add(ControllerData.getWportById(wPort.getId()));
    }

    Set<WPort> addedPorts = new HashSet<WPort>();
    Set<WPort> removedPorts = new HashSet<WPort>();

    for (WPort wPort : newWports) {
      boolean toAdd = true;
      for (WPort wport : oldWports) {
        if (wport.getId().equals(wPort.getId())) {
          toAdd = false;
        }
      }
      if (toAdd) {
        addedPorts.add(wPort);
      }
    }

    for (WPort wPort : oldWports) {
      boolean toRemove = true;
      for (WPort wport : newWports) {
        if (wport.getId().equals(wPort.getId())) {
          toRemove = false;
        }
      }
      if (toRemove) {
        removedPorts.add(wPort);
      }
    }

    System.out.println("OLD PORTS " + gson.toJson(oldWports));
    System.out.println("NEW PORTS " + gson.toJson(newWports));

    System.out.println("ADDED PORTS " + gson.toJson(addedPorts));
    System.out.println("REMOVED PORTS " + gson.toJson(removedPorts));

    System.out.println("OLD PORTS " + oldWports);
    System.out.println("NEW PORTS " + newWports);

    oldExp.addPorts(addedPorts);
    oldExp.removePorts(removedPorts);

    // Adding and removing Nodes //
    LinkedList<NodeObjects> oldNodes = oldExp.getNodes();
    LinkedList<NodeObjects> newNodes = newExp.getNodes(); 

    Set<NodeObjects> addedNodes = new HashSet<NodeObjects>();
    Set<NodeObjects> removedNodes = new HashSet<NodeObjects>();

    for (NodeObjects node : oldNodes) {
      boolean toRemove = true;
      for (NodeObjects node2 : newNodes) {
        if (node2.getId().equals(node.getId())) {
          toRemove = false;
          if (!node2.getConfig().getName()
              .equals(oldExp.getNodeConfig(node.getId()).getName())) {
            toRemove = true;
          }
        }
      }
      if (toRemove) {
        removedNodes.add(node);
      }
    }

    for (NodeObjects node : newNodes) {
      boolean toAdd = true;
      for (NodeObjects node2 : oldNodes) {
        if (node.getId().equals(node2.getId())) {
          toAdd = false;
          if (!oldExp.getNodeConfig(node.getId()).getName()
              .equals(node.getConfig().getName())) {
            System.out.println("OLD NODE CONFIG" + oldExp.getNodeConfig(node.getId()));
            System.out.println("NEW NODE CONFIG " + node.getConfig().getName());
            toAdd = true;
          }
        }
      }
      if (toAdd) {
        addedNodes.add(node);
      }
    }

    oldExp.removeNodes(removedNodes);
    Response response = null;
    for (NodeObjects node : addedNodes) {
       response = oldExp.addNode(ControllerData.getNodeById(node.getId()),
          ControllerData.getConfig(node.getConfig().getName()));
    }

    System.out.println("REMOVED NODES " + gson.toJson(removedNodes));
    System.out.println("ADDED NODES " + gson.toJson(addedNodes));

    oldExp.setId(newExp.getUser() + newExp.getName());
    
    return response;
  }

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
   * <li>201: Experiment was successfully created.</li> <li>409: Experiment with
   * this ID already exists.</li>
   * 
   * @param data
   *          experiment to create in the controller
   * @return 201 for successful creation, 409 if experiment with this id already
   *         exists
   */
  @PUT
  @Path("/exp")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response addNewExperiment(String data) {

    // TODO VMs anlegen

    System.out.println(data);

    Experiment oldExp = gson.fromJson(data, Experiment.class);
    Config config;
    Config defaultConfig = ControllerData.getConfig(oldExp.getDefaultConfig().getName());
    String responseString;
    String name = oldExp.getName();
    String user = oldExp.getUser();
    String nodeId;
    boolean success = true;
    String successString = "";
    Response response;

    if (oldExp.getName().contains("\u00c4") // Umlaut not allowed
        || oldExp.getId().contains("\u00d6") 
        || oldExp.getId().contains("\u00dc")
        || oldExp.getId().contains("\u00e4") 
        || oldExp.getId().contains("\u00f6")
        || oldExp.getId().contains("\u00fc")) {
      return Response.status(500).entity("Umlaut not allowed!").build();
    }

    if(oldExp.getDefaultConfig().getName().equals("")) {
        return Response.status(500).entity("Please specify a default Config!").build();
    }
    
    System.out.println(data);

    if (data != null && ControllerData.existsExp(user + name)) {
      responseString = "Experiment with this ID already exists!";
      return Response.status(409).entity(responseString).build();
    } else {

      // Create new Experiment with full Data
      Experiment experiment = new Experiment(name, user, defaultConfig);

      LinkedList<NodeObjects> nodeList = new LinkedList<NodeObjects>();
      LinkedList<WPort> wportList = new LinkedList<WPort>();

      // Transfer all Nodes from the sent experiment to the new experiment
      for (NodeObjects node : oldExp.getList()) {

        nodeId = node.getId();
        config = node.getConfig();

        if (config != null) {
          config = ControllerData.getConfig(config.getName());
        } else {
          config = ControllerData.getConfig(defaultConfig.getName());
        }

        nodeList.add(ControllerData.getNodeById(nodeId));
        System.out.println("added config " + config.getName() + " to node " + nodeId);
        experiment.addNodeConfig(nodeId, config);
      }
      experiment.setNodeList(nodeList);

   
      // Check if the nodes are applicable for the chosen configuration
      for (NodeObjects node : experiment.getList()) {
        config = experiment.getNodeConfig(node.getId());
        if (!node.isApplicable(config)) {
          successString += "Node '" + node.getId() + "' is not applicable for "
              + "config: '" + config.getName() + "' \n";
          success = false;
        }
      }

      if (!success) {
        return Response.status(500).entity(successString).build();
      }

      
      // Transfer all wPorts from the old experiment to the new experiment
      for (WPort wport : oldExp.getWports()) {
        wportList.add(ControllerData.getWportById(wport.getId()));
      }
      experiment.setWports(wportList);

     
      // Add a Global VLan to the Experiment
      response = experiment.addGlobalVlan();
      if (response.getStatus() != 200) {
        return response;
      }

     
      // Deploys all Trunk Ports for the Experiment
      response = experiment.deployAllTrunks();
      if (response.getStatus() != 200) {
        return response;
      }

      experiment.setStatus(PossibleState.stopped);
      ControllerData.addExp(experiment);
      Initialize.saveToDisk();

      responseString = "New Experiment posted/created with ID : " + name;
      return Response.status(201).entity(responseString).build();
    }

  }

  /**
   * Stops the experiment.
   * 
   * <p>
   * To stop the experiment the following steps are taken:
   * 
   * <li>The Config VLans are removed.</li>
   * <li>The PowerSources are turned off.</li>
   * <li>The Virtual Machines are stopped.</li>
   * 
   * If one of the steps above results in an error the process is aborted. 
   * @param expId  The ID of the experiment to stop
   * @return  a Response Object with information if the stopping process was successful
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
    Response response = exp.stop();
    Initialize.saveToDisk();
    return response;
  }

  /**
   * Starts the experiment.
   * 
   * <p>
   * To stop the experiment the following steps are taken:
   * 
   * <li>The Config VLans are activated.</li>
   * <li>The PowerSources are turned on.</li>
   * <li>The Virtual Machines are started.</li>
   * 
   * But only if all Nodes and WPorts are currently available and not active in any other way.
   * @param expId  The ID of the experiment to start
   * @return   a Response Object with information if the starting process was successful
   */
  @PUT
  @Path("/start")
  @Produces(MediaType.TEXT_PLAIN)
  public Response startExp(String expId) {
    System.out.println("START EXPERIMENT");

    Experiment exp = ControllerData.getExpById(expId);

    if (exp == null) {
      String responseString = "404, Experiment not found!";
      return Response.status(404).entity(responseString).build();
    }
    Response response = exp.start();
    Initialize.saveToDisk();
    return response;

  }

  /**
   * Starts the experiment.
   * 
   * <p>
   * Unpausing the Experiment is transferring the experiment from the paused to the running state.
   * </p>
   * 
   * <p>
   * To stop the experiment the following steps are taken:
   * 
   * <li>The PowerSources are turned on.</li>
   * 
   * @param expId  The ID of the experiment to start
   * @return  a Response Object with information if the unpausing process was successful
   */
  @PUT
  @Path("/unpause")
  @Produces(MediaType.TEXT_PLAIN)
  public Response unpauseExp(String expId) {

    Experiment exp = ControllerData.getExpById(expId);

    if (exp == null) {
      String responseString = "404, Experiment not found!";
      return Response.status(404).entity(responseString).build();
    } else {

      // Ckeck if experiment is paused
      if (!exp.getStatus().equals(Experiment.PossibleState.paused)) {
        return Response.status(400).entity("Can not do this in the state "
            + exp.getStatus()).build();
      }
      Response response = exp.unpause();
      Initialize.saveToDisk();
      return response;
    }
  }

  /**
   * Pauses the Experiment.
   * 
   * Turns all Nodes of this experiment off, if the previous state was running.
   * 
   * If the previous state was stopped the following steps are taken:
   *
   * <li>The PowerSources are turned off</li>
   * <li>The Virtual Machines are started</li>
   * <li>The Config VLans are activated</li>
   * 
   * But only if all Nodes and Ports are currently available. 
   * @param expId
   *          experiment ID from the experiment to pause
   * @return a Response Object with status code and message body, which reports
   *         possible errors
   */
  @PUT
  @Path("/pause")
  @Produces(MediaType.TEXT_PLAIN)
  public Response pauseExp(String expId) {

    Experiment exp = ControllerData.getExpById(expId);

    if (exp == null) {
      String responseString = "404, Experiment not found!";
      return Response.status(404).entity(responseString).build();
    }

    if (ControllerData.getExpById(expId).getStatus().equals(Experiment.PossibleState.stopped)) {

      Response response = exp.firstPause();
      Initialize.saveToDisk();
      return response;
    }

    if (ControllerData.getExpById(expId).getStatus().equals(Experiment.PossibleState.running)) {
      Response response = exp.pause();
      Initialize.saveToDisk();
      return response;
    }

    return Response.status(500).entity("Status was not stopped or running, did nothing")
        .build();
  }

  /**
   * Reloads the config.xml file.
   * 
   * <p>
   * After calling this function new added configurations are available for use.
   * 
   * @return a Response if the reloading was successful
   */
  @GET
  @Path("/reloadConfigs")
  @Produces(MediaType.TEXT_PLAIN)
  public Response reloadConfigs() {
    boolean bool = Initialize.reloadConfigs();
    if (bool) {
      return Response.status(200).entity("Configs were reloaded.").build();
    } else {
      return Response.status(500).entity("Reloading Configs failed!").build();
    }
  }

  /**
   * Reloads the topology.xml.
   * 
   * <p>
   * After calling this the internal representation of the network is updated.
   * @return a Response if the reloading was successful
   */
  @GET
  @Path("/reloadTopology")
  @Produces(MediaType.TEXT_PLAIN)
  public Response reloadTopology() {
    boolean bool = Initialize.reloadTopology();
    if (bool) {
      return Response.status(200).entity("Topology was reloaded.").build();
    } else {
      return Response.status(500).entity("Reloading Topology failed!").build();
    }
  }


  /**
   * Reloads the nodes.xml.
   * 
   * <p>
   * After calling this new added Nodes are available to use.
   * @return a Response if the reloading was successful
   */
  @GET
  @Path("/reloadNodes")
  @Produces(MediaType.TEXT_PLAIN)
  public Response reloadNodes() {
    boolean bool = Initialize.reloadAllNodes();
    if (bool) {
      return Response.status(200).entity("Nodes were reloaded.").build();
    } else {
      return Response.status(500).entity("Reloading Nodes failed!").build();
    }
  }

  /**
   * Reloads the nodes.xml.
   * 
   * <p>
   * After calling this new added WPorts are available to use.
   * @return a Response if the reloading was successful
   */
  @GET
  @Path("/reloadwPorts")
  @Produces(MediaType.TEXT_PLAIN)
  public Response reloadwPorts() {
    boolean bool = Initialize.reloadAllwPorts();
    if (bool) {
      return Response.status(200).entity("wPorts were reloaded.").build();
    } else {
      return Response.status(500).entity("Reloading wPorts failed!").build();
    }
  }

  /**
   * Turns all the components from a given node on.
   *
   * <p>
   * Address of this method: baseuri:port/mm.controller/rest/put/turnOn The node
   * to turn on has to be identified in the message body.
   *
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: All components of the node were turned on. No further data.</li>
   * <li>404: The node was not found. Additional Data in the message body.</li>
   * <li>500: Overwrites a 404 status code. Something else happened and the node
   * could not be turned on. Additional data in the message body.</li>
   * 
   * @param data
   *          Identifier for the node in the message body.
   * @return Response Object with a status code and a possible message body.
   */
  @PUT
  @Path("/turnOn")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response turnNodeOn(String data) {

    System.out.println("TurnOn Node was called");

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
   *
   * <p>
   * Address of this method:
   * baseuri:port/mm.controller/rest/put/turnOn/{component}, where component is
   * a String with the type of the component. The node, to which the component
   * belongs, has to be specified in the message body.
   *
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: The component was turned on. No additional data provided.</li>
   * <li>404: The node or the specified component was not found. Additional Data
   * in the message body.</li>
   * <li>500: Overwrites a 404 status code. Something else happened and the
   * component could not be turned off. Additional data in the message body.</li>
   * 
   * @param data
   *          Identifier for the node in the message body.
   * @param comp
   *          Type/Identifier for the component in the node, specified in the
   *          URI
   * @return a Response Object with a status code and a possible message body.
   */
  @PUT
  @Path("/turnOn/{comp}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response turnCompOn(String data, @PathParam("comp") String comp) {

    System.out.println("TurnOn Node/Component was called");

    NodeObjects node = ControllerData.getNodeById(data);
    String responseString;

    if (node != null && ControllerData.exists(node)) {

      Response response = node.turnOn(comp);

      if (response.getStatus() == 200) {
        responseString = "Component in the Node turned on";
        return Response.status(200).entity(responseString).build();
      } else {

        responseString = "WARNING: Component '" + comp + "' "
            + "was not turned on "
            + "on Node '" + data + "'\n";
        responseString += (String) response.getEntity();
        return Response.status(500).entity(responseString).build();
      }

    } else {
      responseString = "404, Node not found! Node: " + data;
      return Response.status(404).entity(responseString).build();
    }

  }

  /**
   * Turns on all Nodes in the Experiment.
   * 
   * <p>
   * All Nodes in the Experiment are turned on. This is only possible if the Experiment 
   * is in the state "running". 
   * </p>
   * 
   * @param expId  the ID of the experiment which Nodes should be turned on
   * @return  a Response if the turning process was successful
   */
  @PUT
  @Path("/turnOn/exp")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response turnAllNodesOn(String expId) {

    System.out.println("TurnOn Experiment was called");
    String responseString;
    Experiment experiment = ControllerData.getExpById(expId);

    if (experiment == null) {
      return Response.status(404).entity("404, Experiment not found").build();
    }

    if (!experiment.getStatus().equals(Experiment.PossibleState.running)) {
      return Response.status(403).entity("Can not do this on status "
          + experiment.getStatus()).build();
    }

    Response response = experiment.turnOn();

    if (response.getStatus() == 200) {
      responseString = "All Components in the Node turned off";
      return Response.status(200).entity(responseString).build();
    } else {
      responseString = "WARNING: Not all Components were turned off! \n";
      responseString += (String) response.getEntity();
      return Response.status(500).entity(responseString).build();
    }
  }

  /**
   * Turns off all Nodes in the Experiment.
   * 
   * <p>
   * All Nodes in the Experiment are turned off. This is only possible if the Experiment 
   * is in the state "running". 
   * </p>
   * 
   * @param expId  the ID of the experiment which Nodes should be turned on
   * @return  a Response if the turning process was successful
   */
  @PUT
  @Path("/turnOff/exp")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response turnAllNodesOff(String expId) {

    System.out.println("TurnOff Experiment was called");
    String responseString;
    Experiment experiment = ControllerData.getExpById(expId);

    if (experiment == null) {
      return Response.status(404).entity("404, Experiment not found").build();
    }

    if (!experiment.getStatus().equals(Experiment.PossibleState.running)) {
      return Response.status(403).entity("Can not do this on status "
          + experiment.getStatus()).build();
    }

    Response response = experiment.turnOff();

    if (response.getStatus() == 200) {
      responseString = "All Components in the Node turned off";
      return Response.status(200).entity(responseString).build();
    } else {
      responseString = "WARNING: Not all Components were turned off! \n";
      responseString += (String) response.getEntity();
      return Response.status(500).entity(responseString).build();
    }

  }

  /**
   * Turns all the components from a given node off.
   *
   * <p>
   * Address of this method: baseuri:port/mm.controller/rest/put/turnOn The node
   * to turn off has to be identified in the message body.
   *
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: All components of the node were turned off. No further data.
   * <li>404: The node was not found. Additional Data in the message body.
   * <li>500: Overwrites a 404 status code. Something else happened and the node
   * could not be turned off. Additional data in the message body.
   * 
   * @param data
   *          Identifier for the node in the message body.
   * @return Response Object with a status code and a possible message body.
   */
  @PUT
  @Path("/turnOff")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response turnNodeOff(String data) {
    System.out.println("Node turnOff was called");

    NodeObjects node = ControllerData.getNodeById(data);

    String responseString;

    if (node != null && ControllerData.exists(node)) {

      Response response = node.turnOff();

      if (response.getStatus() == 200) {
        responseString = "All Components in the Node turned off";
        return Response.status(200).entity(responseString).build();
      } else {
        responseString = "WARNING: Not all Components were turned off! \n";
        responseString += (String) response.getEntity();
        return Response.status(500).entity(responseString).build();
      }

    } else {
      responseString = "404, Node '" + data + "' not found!";
      return Response.status(404).entity(responseString).build();
    }
  }

  /**
   * Turns the given Component from the given Node off.
   *
   * <p>
   * Address of this method:
   * <code>baseuri:port/mm.controller/rest/put/turnOff/{component}</code>, where
   * component is a String with the type of the component. The node, to which
   * the component belongs, has to be specified in the message body.
   *
   * <p>
   * Possible HTTP status codes:
   * 
   * <li>200: The component was turned off. No additional data provided.
   * <li>404: The node or the specified component was not found. Additional Data
   * in the message body.
   * <li>500: Overwrites a 404 status code. Something else happened and the
   * component could not be turned off. Additional data in the message body.
   * 
   * @param data
   *          Identifier for the node in the message body.
   * @param comp
   *          Type/Identifier for the component in the node, specified in the
   *          URI
   * @return a Response Object with a status code and a possible message body.
   */
  @PUT
  @Path("/turnOff/{comp}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response turnCompOff(String data, @PathParam("comp") String comp) {

    System.out.println("TurnOff Node/Component was called");

    NodeObjects node = ControllerData.getNodeById(data);
    String responseString;

    if (node != null && ControllerData.exists(node)) {

      Response response = node.turnOff(comp);
      System.out.println("ControllerPUT status : " + response.getStatus());

      if (response.getStatus() == 200) {
        responseString = "Component in the Node turned off";
        return Response.status(200).entity(responseString).build();
      } else {

        responseString = "WARNING: Component was not turned off \n";

        responseString += (String) response.getEntity();

        System.out.println("ControllerPut Status " + response.getStatus()
            + " \n Controllerput responseString " + responseString);

        return Response.status(500).entity(responseString).build();
      }

    } else {
      responseString = "404, Node not found! Node: " + data;
      return Response.status(404).entity(responseString).build();
    }
  }

}
