package mm.controller.modeling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import mm.controller.main.ControllerData;
import mm.controller.net.ControllerNetDelete;
import mm.controller.net.ControllerNetGet;
import mm.controller.net.ControllerNetPut;
import mm.controller.power.ControllerPowerGet;

public class Experiment {

    private String                  id;
    private LinkedList<NodeObjects> nodes       = new LinkedList<NodeObjects>();
    private LinkedList<VLan>        localVlans  = new LinkedList<VLan>();
    private VLan                    globalVlan;
    private LinkedList<WPort>       wports;
    private HashMap<String, Config> nodeConfigs = new HashMap<String, Config>();
    // TODO VMs
    private String                  user;
    private String                  name;

    public static enum PossibleState {
        @SerializedName("stopped")
        stopped,
        @SerializedName("running")
        running,
        @SerializedName("paused")
        paused
    };

    private PossibleState status;
    private Config        defaultConfig;

    /**
     * Creates a new Experiment with a Name, a User and a default Config
     * 
     * <p>
     * The ID of the experiment is set to user+name
     * </p>
     * 
     * @param name
     *            the name of the experiment
     * @param user
     *            the owner of the experiment
     * @param defaultConfig
     *            the default config of the expeirment
     */
    public Experiment(String name, String user, Config defaultConfig) {

        this.name = name;
        this.user = user;
        this.defaultConfig = defaultConfig;

        this.id = user + name;
    }

    /**
     * Turns on all nodes in the experiment. Gathers the responses and if
     * something fails returns a 500 status code. If everything went right HTTP
     * status code 200(OK) is returned
     * 
     * @return an outbound response object with status code and possible error
     *         in the message body
     */
    public Response turnOn() {
        Response response;

        String responseString = "";
        int responseStatus = 200;

        for (NodeObjects node : nodes) {
            response = node.turnOn();

            if (response.getStatus() != 200) {
                responseStatus = 500;
                responseString += (String) response.getEntity();
            }
        }
        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Turns off all nodes in the experiment. Gathers the responses and if
     * something fails returns a 500 status code. If everything went right HTTP
     * status code 200(OK) is returned
     * 
     * @return an outbound response object with status code and possible error
     *         in the message body
     */
    public Response turnOff() {

        Response response;

        String responseString = "";
        int responseStatus = 200;

        for (NodeObjects node : nodes) {
            response = node.turnOff();

            if (response.getStatus() != 200) {
                responseStatus = 500;
                responseString += (String) response.getEntity();
            }
        }
        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Deletes all the data in the network from this experiment.
     * 
     * Deletes the VLans from this experiment and deletes the virtual machines
     * from the sever.
     *
     * @return
     */
    public Response destroy() {
        System.out.println("Destroying Experiment " + this.id);
        Response response;
        String responseString = "";
        int responseStatus = 200;

        // Destroy and free global VLAN
        response = destroyGlobalVlan();

        if (response.getStatus() != 200) {
            System.out.println((String) response.getEntity());
            responseString += (String) response.getEntity();
            responseStatus = 500;
        }

        // TODO VMs l�schen
        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Destroys the global VLan of this experiment.
     * 
     * <p>
     * The VLan is removed from all affiliated NetComponents and then returned
     * to the NetService for further use.
     * 
     * @return an outbound response with status code and possible error message
     */
    private Response destroyGlobalVlan() {

        String responseString = "";
        int responseStatus = 200;
        Response response = ControllerNetPut.removeVlan(globalVlan);

        if (response.getStatus() != 200) {
            System.out.println((String) response.getEntity());
            responseString += (String) response.getEntity();
            responseStatus = 500;
        } else {
            response = ControllerNetDelete.freeGlobalVlan(globalVlan.getId());
        }

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Gets a global VLan from the NetService and adds it to the experiment.
     * 
     * @return an outbound Response Object.
     */
    public Response addGlobalVlan() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Response response = ControllerNetGet.getGlobalVlan();

        if (response.getStatus() == 200) {
            VLan vlan = gson.fromJson((String) response.getEntity(), VLan.class);
            vlan.setName(this.id);
            globalVlan = vlan;
            System.out.println("Added VLan " + vlan.getId() + " to experiment " + this.id);
            return Response.ok().build();
        } else {
            return response;
        }

    }

    /**
     * Deploys all Trunkports for this experiment.
     * 
     * <p>
     * Calculates a path from the trunkport of the VM Server to all trunk ports
     * of each component of each node in this experiment. Then advises the
     * NetService to put all calculated trunkports on the global VLAN.
     * </p>
     * 
     * @return an outbound response object with status code 200(OK) or
     *         500(Internal Server Error) if something went wrong
     */
    public Response deployAllTrunks() {

        VLan vlan = globalVlan;
        Set<String> targetTrunkPorts = new HashSet<String>();
        LinkedList<String> path;

        // Calculating the target Trunkports for each Component for each Node
        for (NodeObjects node : nodes) {

            Config config = nodeConfigs.get(node.getId());
            Wire wire = config.getGlobalWire();
            if (wire != null) {
                for (String role : wire.getEndpoints()) {
                    targetTrunkPorts.add(node.getTrunkPortByRole(role));
                }
            }
        }

        // Calculating the Path to each target trunk port
        for (String targetTrunkPort : targetTrunkPorts) {
            path = ControllerData.getPath(targetTrunkPort);
            if (path == null) {
                System.out.println("PATH EQUALS NULL");
                return Response.status(403).entity("Could not determine path to Trunk '"
                        + targetTrunkPort).build();
            }

            vlan.addPorts(path);
        }

        // Requesting the Path for the wPorts and adding the Ports to the VLan
        for (WPort wport : wports) {
            path = ControllerData.getPath(wport.getTrunk());
            if (path == null) {
                return Response.status(500)
                        .entity(Entity.text("Could not determine path to Trunk '"
                                + wport.getTrunk())).build();
            }
            vlan.addPorts(path);
        }

        // Setting the TrunkPorts for all Nodes and wPorts
        Response response = ControllerNetPut.setTrunkPort(vlan);
        return response;
    }

    /**
     * Removes all VLans from the Switchports of a Node.
     * 
     * Gets all VLans which are currently active on the Node and then proceeds
     * to free all of the Switchports.
     *
     * @param node
     *            node to free
     * @return a Response with status code and message body, 200 if everytingh
     *         went right and 500 if something went wrong
     */
    private Response freeSwitchPortsFromNode(NodeObjects node) {

        Response response;
        String responseString = "";
        int responseStatus = 200;

        for (Integer vlanId : node.getVlanIds()) {

            if (vlanId == globalVlan.getId()) {
                response = ControllerNetPut.removePort(
                        new LinkedList<String>(node.getAllSwitchPorts()),
                        vlanId,
                        globalVlan.getName());

                if (response.getStatus() != 200) {
                    responseString += (String) response.getEntity();
                    responseStatus = 500;
                }
            } else {
                for (VLan vLan : localVlans) {
                    if (vLan.getId() == vlanId) {
                        response = ControllerNetPut.removeVlan(vLan);
                        if (response.getStatus() != 200) {
                            responseString += (String) response.getEntity();
                            responseStatus = 500;
                        }

                        response = ControllerNetDelete.freeLocalVlan(vLan.getId());
                        if (response.getStatus() != 200) {
                            responseString += (String) response.getEntity();
                            responseStatus = 500;
                        }
                    }
                }
            }
        }

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Adds a Node when the experiment is paused.
     * 
     * <p>
     * If the node is already member of this experiment the Config VLans are
     * removed and the new Config is applied. At last the Node is turned off.
     * </p>
     * 
     * <p>
     * If the node is new to the experiment first the availability is checked.
     * If the node is available the trunk ports for the config are set and then
     * the config VLans themselves are set. At last the Node is turned off.
     * </p>
     * 
     * @param node
     *            the node to add
     * @param config
     *            the config of the node
     * @return
     */
    private Response addNodePaused(NodeObjects node, Config config) {

        String responseString = "";
        int responseStatus = 200;
        boolean available = true;
        boolean alreadyContains = false;
        Response response;

        for (NodeObjects nodeobject : nodes) {
            if (nodeobject.getId().equals(node.getId())) {
                alreadyContains = true;
            }
        }

        if (alreadyContains) {
            // Node is already in the Experiment, free the Switchports
            freeSwitchPortsFromNode(node);

        } else {
            // Check if node is available, only if new to the experiment
            response = node.isAvailable();
            if (response.getStatus() != 200) {
                responseString += (String) response.getEntity();
                available = false;
                System.out.println("NODE ADD PAUSED IS NOT AVAILABLE");
            }

            if (!available) {
                return Response.status(500).entity(responseString).build();
            }
        }

        addNodeStopped(node, config);

        LinkedList<NodeObjects> nodeList = new LinkedList<NodeObjects>();
        nodeList.add(node);

        nodeConfigs.put(node.getId(), config);

        response = deployLocalVlans(nodeList);

        response = deployGlobalVlan(nodeList, new LinkedList<WPort>());

        response = node.turnOff();

        if (response.getStatus() != 200) {
            responseString += (String) response.getEntity();
        }

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Adds a Node in the experiment state "stopped".
     * 
     * <p>
     * Calculates the new path to the trunkports and checks if the globalVlan is
     * already configured there. If it is the global VLan is destroyed and a new
     * global VLan is added to the experiment. This new global VLan must be free
     * on all NetComponents.
     * </p>
     * 
     * <p>
     * Then, or if the global VLan is free on the NetComponents, the path to the
     * trunkports is set and the Node added to the internal representation of
     * the experiment.
     * 
     * @param node
     *            node to add
     * @param config
     *            config for the to be added node
     * @return
     */
    private Response addNodeStopped(NodeObjects node, Config config) {
        Response response;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Set<String> portsToAdd = new HashSet<String>();
        Set<String> trunkPortsToAdd = new HashSet<String>();

        // Getting all trunkports which could possibly be added
        for (Wire wire : config.getWires()) {
            for (String role : wire.getEndpoints()) {
                portsToAdd.add(node.getTrunkPortByRole(role));
            }
        }

        for (String port : portsToAdd) {
            trunkPortsToAdd.addAll(ControllerData.getPath(port));
        }

        trunkPortsToAdd.removeAll(globalVlan.getPortList());
        System.out.println("PORTS TO ADD IN ADDNODE " + trunkPortsToAdd);

        // Check if VLAN is available on the new NetComponents
        if (trunkPortsToAdd.isEmpty()) {
            nodes.add(node);
            nodeConfigs.put(node.getId(), config);
            return Response.ok("Added Node " + node.getId()).build();
        }

        boolean available = ControllerNetGet.isFreeOnNc(trunkPortsToAdd, globalVlan.getId());

        if (available) {
            // global VLan is available at the new NetComponents
            response = ControllerNetPut.setTrunkPort(trunkPortsToAdd, globalVlan.getId(),
                    globalVlan.getName());

            if (response.getStatus() != 200) {
                return Response.status(500).entity("Failed adding Node "
                        + node.getId() + "because of \n "
                        + (String) response.getEntity()).build();
            }

            globalVlan.addPorts(trunkPortsToAdd);
            nodes.add(node);
            nodeConfigs.put(node.getId(), config);

            return Response.ok("Added Node " + node.getId()).build();
        } else {
            // global VLan is not available at the new NetComponents
            nodes.add(node);
            nodeConfigs.put(node.getId(), config);
            destroyGlobalVlan();
            this.addGlobalVlan();
            response = deployAllTrunks();
        }

        return response;
    }

    /**
     * Adds a Node to the experiment and sets the VLans depending on the state
     * of the experiment and the config chosen for the node.
     * 
     * @param node
     *            node to add
     * @param config
     *            config for the node
     * @return outbound response with status code and message body
     */
    public Response addNode(NodeObjects node, Config config) {

        if (!node.isApplicable(config)) {
            return Response.status(500).entity("Node is not applicable for this Config!").build();
        }
        switch (status) {
        case running:
            return Response.status(400).entity("Can not do this in status " + status.toString())
                    .build();
        case paused:
            return addNodePaused(node, config);
        case stopped:
            return addNodeStopped(node, config);
        default:
            return Response.status(500).entity("Could not determine status of experiment").build();
        }

    }

    /**
     * Unpauses the experiment from the state "paused".
     * 
     * <p>
     * Simply turns on all nodes in the experiment.
     * </p>
     * 
     * @return an outbound response object with status code and possible error
     *         messages
     */
    public Response unpause() {
        Response response = this.turnOn();
        status = PossibleState.running;
        return response;
    }

    /**
     * Firstpause describes the state change from "stopped" to "paused".
     * 
     * <p>
     * This has the following effects:
     * </p>
     * 
     * <li>There is an availability check if all nodes are ready to use</li> <li>
     * All nodes are connected to the VLans and local VLans are initialized</li>
     * <li>Virtual machines are started</li> <li>The power of all nodes is
     * turned off explicitly</li>
     *
     * @return outbound response object
     * 
     */
    public Response firstPause() {

        boolean success = true;
        int status = 200;
        String responseString = "";
        Response response = null;

        // Check availability of all nodes
        for (NodeObjects node : nodes) {
            response = node.isAvailable();
            if (response.getStatus() != 200) {
                responseString += (String) response.getEntity();
                status = 500;
                success = false;
            }
        }

        // Check availability of all WPorts
        for (WPort wPort : wports) {
            response = wPort.isAvailable();
            if (response.getStatus() != 200) {
                responseString += (String) response.getEntity();
                status = 500;
                success = false;
            }
        }

        if (!success) {
            return Response.status(status).entity(responseString).build();
        }

        // Turning off the power
        response = this.turnOff();
        if (response.getStatus() != 200) {
            return response;
        }
        if (response.getStatus() != 200) {
            return response;
        }

        // Deploying the Config VLans
        response = deployConfigVlans();

        if (response.getStatus() != 200) {
            return response;
        }

        // TODO vms starten

        this.setStatus(PossibleState.paused);
        return Response.status(status).entity(responseString).build();
    }

    /**
     * Pauses the experiment.
     * 
     * <p>
     * To do this all the nodes are turned off. If something goes wrong the
     * Response object holds a specified error message.
     * 
     * @return an Outbound Response Object with status code and message body in
     *         error case
     */
    public Response pause() {
        Response response;
        int responseStatus = 200;
        String responseString = "";
        boolean success = true;

        for (NodeObjects node : nodes) {
            response = node.turnOff();
            if (response.getStatus() != 200) {
                responseStatus = 500;
                responseString += (String) response.getEntity();
                success = false;
            }
        }

        if (!success) {
            return Response.status(responseStatus).entity(responseString).build();
        }
        this.setStatus(PossibleState.paused);

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Stops an Experiment.
     * 
     * <p>
     * To stop an experiment the following actions are taken:
     * </p>
     * 
     * <li>The VLans are modified so that the components from the nodes are not
     * connected anymore and do not belong to any VLan</li> <li>The virtual
     * machines are stopped</li> <li>The power for all nodes is turned off</li>
     * 
     * @return an outbound response object.
     */
    public Response stop() {

        // TODO VMs stoppen

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Set<String> switchports = new HashSet<String>();
        Set<String> vlanports = new HashSet<String>();
        VLan vlan;
        Response response = null;
        String responseString = "";
        int responseStatus = 200;

        // Turning the power off
        for (NodeObjects node : nodes) {
            response = node.turnOff();
        }
        if (response.getStatus() != 200) {
            return response;
        }

        // Adding all Switchports from all Nodes and WPorts
        for (NodeObjects node : nodes) {
            switchports.addAll(node.getAllSwitchPorts());
        }

        for (WPort wPort : wports) {
            switchports.add(wPort.getPort());
        }

        vlanports.addAll(globalVlan.getPortList());

        vlanports.retainAll(switchports);

        vlan = new VLan(globalVlan.getId(), true);

        vlan.setPortList(vlanports);

        response = ControllerNetPut.removePort(vlan);

        if (response.getStatus() != 200) {
            responseStatus = 500;
            responseString += "Error on stopping Experiment '" + this.id + "'\n"
                    + (String) response.getEntity();
        } else {
            // Remove all removed ports from the internal representation
            globalVlan.removePorts(vlanports);
            System.out.println("Removed from Experiment : " + vlanports);
        }

        LinkedList<VLan> localVlansToRemove = new LinkedList<VLan>();
        for (VLan vLan : localVlans) {

            System.out.println(gson.toJson(vLan));

            // Remove the local VLAN
            response = ControllerNetPut.removeVlan(vLan);

            if (response.getStatus() != 200) {
                responseStatus = 500;
                responseString += "Error on stopping Experiment '" + this.id + "'\n"
                        + (String) response.getEntity();
            } else {
                // Remove all removed ports from the internal representation
                localVlansToRemove.add(vLan);
            }
            ControllerNetDelete.freeLocalVlan(vLan.getId());
        }

        localVlans.removeAll(localVlansToRemove);

        this.setStatus(PossibleState.stopped);
        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Deploys all Switchports from the nodes and ports.
     * 
     * Used when starting an experiment to connect all Nodes and WPorts. Does
     * not deploy any Trunks, because they are deployed earlier on the creation
     * of the experiment in deployAllTrunks().
     *
     * <p>
     * All Switchports from the WPorts are connected and every Switchport from a
     * Node which Role is a member of the global VLan in the config of the node
     * is connected.
     * 
     * @return an outbound response object
     */
    private Response deployGlobalVlan(LinkedList<NodeObjects> nodeList, LinkedList<WPort> wportList) {

        Config config;
        VLan vlan = null;
        LinkedList<String> portList = new LinkedList<String>();
        int responseStatus = 200;
        String responseString = "";
        String port;
        Response response;

        // Get Switchports from the WPorts
        for (WPort wport : wportList) {
            portList.add(wport.getPort());
        }

        // Add the Switchports from the Components which are member of the
        // global
        // Experiment VLan
        for (NodeObjects node : nodeList) {

            config = nodeConfigs.get(node.getId());

            for (Wire wire : config.getWires()) {

                if (wire.hasUplink()) {
                    Set<String> endpoints = wire.getEndpoints();
                    endpoints.remove("*");
                    for (String endpoint : endpoints) {
                        port = node.getPortByRole(endpoint);
                        portList.add(port);
                    }
                }
            }
        }

        vlan = globalVlan;
        vlan.addPorts(portList);

        response = ControllerNetPut.addPort(portList, vlan.getId(), vlan.getName());

        if (response.getStatus() != 200) {
            responseStatus = 500;
            responseString += (String) response.getEntity();
        }

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Deploys the Config-VLans.
     * 
     * Deploys all the Config VLans for this experiment. These are
     * 
     * <li>The Switchports which should be member of the global VLan</li> <li>
     * The local VLans for each node, determined by the chosen configuration</li>
     * 
     * @return an outbound response object
     */
    private Response deployConfigVlans() {

        int responseStatus = 200;
        String responseString = "";
        Response response;

        // Deploy the Config Ports for the Global VLan
        response = deployGlobalVlan(nodes, wports);

        if (response.getStatus() != 200) {
            responseStatus = 500;
            responseString += (String) response.getEntity();
        }

        // Deploy the local VLans for each node
        response = deployLocalVlans(nodes);

        if (response.getStatus() != 200) {
            responseStatus = 500;
            responseString += (String) response.getEntity();
        }

        System.out.println("DEPLOY RESPONSE STATUS: " + responseStatus);

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * 
     * @param nodeList
     * @return
     */
    private Response deployLocalVlans(LinkedList<NodeObjects> nodeList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        VLan vlan;
        Config config;
        int responseStatus = 200;
        String responseString = "";

        Response response;

        for (NodeObjects node : nodeList) {

            config = nodeConfigs.get(node.getId());

            for (Wire wire : config.getWires()) {

                if (!wire.hasUplink()) {
                    // Local VLan
                    response = ControllerNetGet.getLocalVlan();

                    if (response.getStatus() == 200) {

                        vlan = gson.fromJson((String) response.getEntity(), VLan.class);
                        vlan.clear();
                        vlan.setName(this.id);
                        System.out.println("Added local VLan: Local " + this.id + "VLan");

                        LinkedList<String> trunkPortList = new LinkedList<String>();
                        LinkedList<String> switchPortList = new LinkedList<String>();

                        boolean first = true;
                        String firstEndpoint = null;
                        // Calculating the Path from one local Port to all the
                        // other local ports
                        for (String endpoint : wire.getEndpoints()) {

                            if (first) {
                                firstEndpoint = node.getTrunkPortByRole(endpoint);
                                first = false;
                            } else {
                                System.out.println("TRUNKPORT LIST ADD "
                                        + ControllerData.getPath(firstEndpoint,
                                                node.getTrunkPortByRole(endpoint)));
                                trunkPortList.addAll(
                                        ControllerData.getPath(firstEndpoint,
                                                node.getTrunkPortByRole(endpoint)));
                            }
                            System.out.println("SWITCHPORT LIST ADD "
                                    + node.getPortByRole(endpoint));
                            switchPortList.add(node.getPortByRole(endpoint));
                        }

                        vlan.addPorts(trunkPortList);
                        String outString = gson.toJson(vlan);
                        System.out.println("SWITCHPORTLIST " + switchPortList);
                        System.out.println("TRUNKPORTLIST " + trunkPortList);
                        System.out.println("VLAN2 DIE ERSTE TRUNKS: " + outString);

                        if (!trunkPortList.isEmpty()) {
                            ControllerNetPut.setTrunkPort(vlan);
                            vlan.clear();

                            vlan.addPorts(switchPortList);
                            ControllerNetPut.addPort(vlan);

                            vlan.addPorts(trunkPortList);
                            vlan.addPorts(switchPortList);
                            localVlans.add(vlan);

                        } else {
                            vlan.addPorts(switchPortList);
                            ControllerNetPut.setPort(vlan);

                            vlan.addPorts(switchPortList);
                            localVlans.add(vlan);
                        }

                        outString = gson.toJson(vlan);
                        System.out.println("VLAN2 DIE ZWEITE SWITCHPORTS: " + outString);
                        System.out.println("Going to Add VLan: " + trunkPortList + switchPortList
                                + " on ID: "
                                + vlan.getId());

                    } else {
                        responseStatus = 500;
                        responseString += "Could not get a local VLan for '" + node.getId()
                                + "' on experiment '" + this.name + "'"
                                + (String) response.getEntity();
                    }
                }
            }
        }

        return Response.status(responseStatus).entity(responseString).build();

    }

    /**
     * Starts the experiment.
     * 
     * <p>
     * To start the experiment first the availability of all nodes in the
     * experiment is checked. If at least one node is not available currently,
     * the starting process is aborted and this will be responded.
     * </p>
     * 
     * <p>
     * If all nodes are available all switchports from the WPorts and for the
     * global VLAN are set. Also the Config VLANs are fetched from the
     * NetService and set for every Node in the experiment. At last all nodes
     * are powered on.
     * </p>
     * 
     * @return an outbound response object with status code and possible error
     *         string
     */
    public Response start() {

        boolean success = true;
        int status = 200;
        String responseString = "";
        Response response = null;

        // Check availability of nodes
        for (NodeObjects node : nodes) {
            response = node.isAvailable();
            if (response.getStatus() != 200) {
                responseString += (String) response.getEntity();
                status = 500;
                success = false;
            }
        }

        for (WPort wPort : wports) {
            response = wPort.isAvailable();
            if (response.getStatus() != 200) {
                responseString += (String) response.getEntity();
                status = 500;
                success = false;
            }
        }

        if (!success) {
            return Response.status(status).entity(responseString).build();
        }

        // Turning the power on
        for (NodeObjects node : nodes) {
            response = node.turnOn();
        }
        if (response.getStatus() != 200) {
            return response;
        }

        // Deploying the Config VLans
        response = deployConfigVlans();

        if (response.getStatus() != 200) {
            return response;
        }

        // TODO vms starten

        this.setStatus(PossibleState.running);
        return Response.status(status).entity(responseString).build();
    }

    /**
     * Checks if the VLan is consistent and returns the Consistency Report
     * 
     * <p>
     * What the check includes depends on the state of the Experiment:
     * 
     * <li>running: The Global and all of the Local VLans are checked.</li>
     * <li>paused: The Global and all of the Local VLans are checked</li>
     * <li>stopped: The Global VLan is checked, because there are no local VLans
     * at this point </li>
     * 
     * @return  a consitency report as a String
     */
    public String isConsistent() {

        String returnString;
        switch (status) {
        case running:
            returnString = ControllerNetGet.isConsistent(globalVlan);
            for (VLan vLan : localVlans) {
                returnString += ControllerNetGet.isConsistent(vLan);
            }
            return returnString;
        case paused:
            returnString = ControllerNetGet.isConsistent(globalVlan);
            for (VLan vLan : localVlans) {
                returnString += ControllerNetGet.isConsistent(vLan);
            }
            return returnString;
        case stopped:
            return ControllerNetGet.isConsistent(globalVlan);
        default:
            return null;
        }
    }

    /**
     * Adds a WPort to the experiment.
     * 
     * 
     * @param removePorts
     * @return
     */
    public Response addPorts(Set<WPort> removePorts) {
        switch (status) {
        case paused:
            return addPortsPaused(removePorts);
        case stopped:
            return addPortsStopped(removePorts);
        default:
            return Response.status(500).entity("Can not do that in state " + status.toString())
                    .build();
        }
    }

    private Response addPortsPaused(Set<WPort> addPorts) {

        Response response;
        String responseString = "";
        int responseStatus = 200;
        boolean available = true;

        // Check if ports are available
        for (WPort wport : addPorts) {
            response = wport.isAvailable();

            if (response.getStatus() != 200) {
                responseString += (String) response.getEntity();
                available = false;
            }
        }
        if (!available) {
            return Response.status(500).entity(responseString).build();
        }

        addPortsStopped(addPorts);

        LinkedList<String> portList = new LinkedList<String>();

        for (WPort wport : wports) {
            portList.add(wport.getPort());
        }

        response = ControllerNetPut.addPort(portList, globalVlan.getId(), globalVlan.getName());

        if (response.getStatus() != 200) {
            responseStatus = 500;
            responseString += (String) response.getEntity();
        } else {
            globalVlan.addPorts(portList);
        }

        return Response.status(responseStatus).entity(responseString).build();

    }

    /**
     * Adds a Set of WPorts to the experiment in the stopped state.
     * 
     * <p>
     * A path is calculated to the switchport of every WPort. The method checks
     * if the global VLAN ID is free on alle the NetComponents on the path. If
     * yes it adds the ports to the global VLAN.
     * 
     * If no the global VLAN is destroyed and a new is fetched from the
     * NetService. This VLAN has to be free on all NetComponents. So it is safe
     * to add all Trunkports to this VLAN.
     * 
     * @param addedPorts
     *            a Set of WPorts to add to this experiment
     * @return an outbound response with status code and possible error string
     */
    private Response addPortsStopped(Set<WPort> addedPorts) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Set<WPort> addPortList = new HashSet<WPort>();

        for (WPort wPort : addedPorts) {
            addPortList.add(ControllerData.getWportById(wPort.getId()));
        }

        Set<String> trunkPortsToAdd = new HashSet<String>();

        for (WPort wPort : addPortList) {

            System.out.println(gson.toJson(wPort));
            trunkPortsToAdd.addAll(ControllerData.getPath(wPort.getTrunk()));

        }

        System.out.println("TRUNKPORTSTOADD: " + gson.toJson(trunkPortsToAdd));
        trunkPortsToAdd.removeAll(globalVlan.getPortList());

        System.out.println("TRUNKPORTSTOADD: " + gson.toJson(trunkPortsToAdd));
        Response response;

        boolean isFree = ControllerNetGet.isFreeOnNc(trunkPortsToAdd, globalVlan.getId());

        if (isFree) {
            response = ControllerNetPut.setTrunkPort(trunkPortsToAdd, globalVlan.getId(),
                    globalVlan.getName());

            if (response.getStatus() != 200) {
                return response;
            }

            wports.addAll(addPortList);
        } else {
            wports.addAll(addPortList);
            response = destroyGlobalVlan();
            if (response.getStatus() != 200) {
                return response;
            }

            response = this.addGlobalVlan();
            if (response.getStatus() != 200) {
                return response;
            }

            response = deployAllTrunks();
            if (response.getStatus() != 200) {
                return response;
            }
        }

        return Response.ok().build();
    }

    public Response removeNodes(Set<NodeObjects> removeNodes) {
        switch (status) {
        case paused:
            return removeNodesPaused(removeNodes);
        case stopped:
            return removeNodesStopped(removeNodes);
        default:
            return Response.status(500).entity("Can not do that in state " + status.toString())
                    .build();
        }
    }

    /**
     * Removes a Set of Nodes from this experiment in the state "paused".
     * 
     * <p>
     * To do this all Switchports on the Node are freed from the VLan they are
     * currently on, but only if the experiment self allocated it.
     * </p>
     * 
     * Returns following status codes.
     * 
     * <li>200: If everything went as expected</li> <li>500: If something went
     * wrong, a specified error message is in the message body</li>
     * 
     * @param removeNodes
     *            the Set of nodes to be removed
     * @return a response with status code and message body
     */
    private Response removeNodesPaused(Set<NodeObjects> removeNodes) {

        Response response;
        String responseString = "";
        int responseStatus = 200;

        for (NodeObjects node : removeNodes) {
            response = freeSwitchPortsFromNode(node);

            if (response.getStatus() != 200) {
                responseString += (String) response.getEntity();
                responseStatus = 500;
            }
        }

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Removes a Node from this experiment in the state "stopped".
     * 
     * <p>
     * To remove a node in stopped state the node is removed from the internal
     * representation. It is removed from the node list and the key:value pair
     * from the hashmap nodeConfigs is also removed.
     * </p>
     *
     * @param removeNodes
     *            set of nodes to remove from this experiment
     * @return an outbound response with status code and possible error string
     */
    private Response removeNodesStopped(Set<NodeObjects> removeNodes) {

        LinkedList<NodeObjects> removeNodeList = new LinkedList<NodeObjects>();

        for (NodeObjects node : removeNodes) {
            for (NodeObjects nodeToRemove : nodes) {
                if (nodeToRemove.getId().equals(node.getId())) {
                    removeNodeList.add(nodeToRemove);
                    nodeConfigs.remove(nodeToRemove.getId());
                }
            }
        }

        nodes.removeAll(removeNodeList);
        return Response.ok().build();
    }

    /**
     * Removes the set of WPorts from this experiment.
     * 
     * <p>
     * What to do to removing the WPorts depends on the state of the experiment.
     * </p>
     * 
     * <p>
     * In the stopped state there is little to do, the port is removed from the
     * internal list of ports. The already set Trunkports can stay on the
     * hardware, they are deleted on the deleting the whole experiment.
     * </p>
     * 
     * <p>
     * In the paused state the switchport from the WPort is freed and the
     * switchports are removed from the globalVlan, also the port is removed
     * from the internal representation.
     * </p>
     * 
     * @param removePorts
     *            Set of WPorts to remove from this experiment
     * @return an outbound response with status code and possible error string
     */
    public Response removePorts(Set<WPort> removePorts) {
        switch (status) {
        case paused:
            return removePortsPaused(removePorts);
        case stopped:
            return removePortsStopped(removePorts);
        default:
            return Response.status(500).entity("Can not do that in state " + status.toString())
                    .build();
        }
    }

    /**
     * Removes the Set of Ports, when the experiment is stopped.
     * 
     * @param removePorts
     * @return an outbound response with status code and possible error string
     */
    private Response removePortsStopped(Set<WPort> removePorts) {

        LinkedList<WPort> removePortList = new LinkedList<WPort>();

        for (WPort wPort : removePorts) {
            for (WPort wport : wports) {
                if (wport.getId().equals(wPort.getId())) {
                    removePortList.add(wport);
                }
            }
        }

        wports.removeAll(removePortList);
        return Response.ok().build();

    }

    /**
     * Removes the Set of Ports, when the experiment is paused.
     * 
     * @param removePorts
     * @return an outbound response with status code and possible error string
     */
    private Response removePortsPaused(Set<WPort> removePorts) {

        Response response;
        LinkedList<String> switchportsToRemove = new LinkedList<String>();
        LinkedList<WPort> removePortList = new LinkedList<WPort>();

        for (WPort wPort : removePorts) {
            switchportsToRemove.add(wPort.getPort());

            for (WPort wport : wports) {
                if (wport.getId().equals(wPort.getId())) {
                    removePortList.add(wport);
                }
            }

        }

        response = ControllerNetPut.removePort(switchportsToRemove, globalVlan.getId(),
                globalVlan.getName());

        if (response.getStatus() != 200) {
            return response;
        } else {
            wports.removeAll(removePortList);
            globalVlan.removePorts(switchportsToRemove);
            return Response.ok().build();
        }
    }

    /**
     * Returns a list of PowerSources with live data for all nodes in the
     * experiment.
     * 
     * @return a list of PowerSources with live power data.
     */
    public LinkedList<PowerSource> status() {
        return ControllerPowerGet.status(nodes);
    }

    /**
     * Updates the status fields in the nodes of this experiment with the
     * provided PowerSource data.
     * 
     * @param statusList
     *            power status data
     */
    public void updateNodeStatusPower(LinkedList<PowerSource> statusList) {
        for (NodeObjects nodeObject : nodes) {
            nodeObject.updateNodeStatusPower(statusList);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkedList<NodeObjects> getNodes() {
        return this.nodes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultConfig(Config config) {
        this.defaultConfig = config;
    }

    public void addNodeConfig(String nodeId, Config config) {
        nodeConfigs.put(nodeId, config);
    }

    public Config getDefaultConfig() {
        return this.defaultConfig;
    }

    public LinkedList<WPort> getWports() {
        return wports;
    }

    public void setWports(LinkedList<WPort> wports) {
        this.wports = wports;
    }

    public PossibleState getStatus() {
        return status;
    }

    public void setStatus(PossibleState status) {
        this.status = status;
    }

    public String getUser() {
        return this.user;
    }

    public String getName() {
        return this.name;
    }

    public Config getNodeConfig(String nodeId) {
        return nodeConfigs.get(nodeId);
    }

    public String getId() {
        return this.id;
    }

    public void setNodeList(LinkedList<NodeObjects> list) {
        this.nodes = list;
    }

    public LinkedList<NodeObjects> getList() {
        return nodes;
    }
    /**
     * Returns all VLan IDs which are owned by this experiment.
     * @return  a list of VLan IDs
     */
    public LinkedList<Integer> getAllVlanIds() {
        LinkedList<Integer> list = new LinkedList<Integer>();
        for (VLan vlan : localVlans) {
            list.add(vlan.getId());
        }

        list.add(globalVlan.getId());

        return list;
    }
}
