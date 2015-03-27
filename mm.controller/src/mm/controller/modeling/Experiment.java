package mm.controller.modeling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.main.ControllerData;
import mm.controller.net.ControllerNetDelete;
import mm.controller.net.ControllerNetGet;
import mm.controller.net.ControllerNetPut;
import mm.controller.power.ControllerPowerGet;

public class Experiment implements Cloneable {

    private transient Gson          gson        = new GsonBuilder().setPrettyPrinting().create();
    private String                  id;
    private LinkedList<NodeObjects> nodes       = new LinkedList<NodeObjects>();
    private LinkedList<VLan>        localVlans  = new LinkedList<VLan>();
    private VLan                    globalVlan;
    private LinkedList<WPort>       wports;
    private HashMap<String, Config> nodeConfigs = new HashMap<String, Config>();
    // TODO VMs
    private String                  user;
    private String                  name;
    private String                  status;

    public void addNodeConfig(String nodeId, Config config) {
        nodeConfigs.put(nodeId, config);
    }

    public LinkedList<WPort> getWports() {
        return wports;
    }

    public void setWports(LinkedList<WPort> wports) {
        this.wports = wports;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return this.user;
    }

    public Experiment(String name, String user) {

        this.name = name;
        this.user = user;

        this.id = user + name;
    }

    public String getName() {
        return this.name;
    }

    public LinkedList<PowerSource> status() {

        return ControllerPowerGet.status(nodes);

    }

    public void updateNodeStatusPower(LinkedList<PowerSource> statusList) {

        for (NodeObjects nodeObject : nodes) {
            nodeObject.updateNodeStatusPower(statusList);
        }
    }

    public Config getNodeConfig(String nodeId) {
        return nodeConfigs.get(nodeId);
    }

    public void addVLan(VLan vlan) {
        localVlans.add(vlan);
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

    public LinkedList<Integer> getAllVlanIds() {
        LinkedList<Integer> list = new LinkedList<Integer>();
        for (VLan vlan : localVlans) {
            list.add(vlan.getId());
        }
        
        System.out.println(gson.toJson(this));
        if(globalVlan == null) {
            System.out.println("GLOBAL VLAN IS NULL ON GET ALL VLAN IDS");
        }
        list.add(globalVlan.getId());
        
       
        
        
        return list;
    }

    public boolean contains(NodeObjects node) {

        boolean bool = false;
        String nodeId = node.getId();
        for (NodeObjects nodeObjects : nodes) {
            if (nodeObjects.getId().equals(nodeId)) {
                bool = true;
            }
        }

        return bool;
    }

    public boolean contains(String nodeId) {

        boolean bool = false;

        for (NodeObjects nodeObjects : nodes) {
            if (nodeObjects.getId().equals(nodeId)) {
                bool = true;
            }
        }
        return bool;

    }

    public NodeObjects getById(String nodeId) {

        NodeObjects node = null;

        for (NodeObjects nodeObjects : nodes) {
            if (nodeObjects.getId().equals(nodeId)) {
                node = nodeObjects;
            }
        }

        return node;
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
        response = ControllerNetPut.removeVlan(globalVlan);

        if (response.getStatus() != 200) {
            System.out.println((String) response.getEntity());
            responseString += (String) response.getEntity();
            responseStatus = 500;
        } else {
            response = ControllerNetDelete.freeGlobalVlan(globalVlan.getId());
        }

        for (VLan vlan : this.localVlans) {

            response = ControllerNetPut.removeVlan(vlan);

            if (response.getStatus() != 200) {
                System.out.println((String) response.getEntity());
                responseString += (String) response.getEntity();
                responseStatus = 500;
            }

            ControllerNetDelete.freeLocalVlan(vlan.getId());

        }

        // TODO VMs l�schen
        return Response.status(responseStatus).entity(responseString).build();

    }

    /**
     * Gets a global VLan from the NetService and adds it to the experiment.
     * 
     * @return an outbound Response Object.
     */
    public Response addGlobalVlan() {

        Response response = ControllerNetGet.getGlobalVlan();

        if (response.getStatus() == 200) {
            VLan vlan = gson.fromJson((String) response.getEntity(), VLan.class);
            vlan.setName(this.id);
            // localVlans.add(vlan);
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

            for (String role : wire.getEndpoints()) {
                targetTrunkPorts.add(node.getTrunkPortByRole(role));
            }
        }

        // Calculating the Path to each target trunk port
        for (String targetTrunkPort : targetTrunkPorts) {
            path = ControllerData.getPath(targetTrunkPort);
            vlan.addPorts(path);
        }

        // Requesting the Path for the wPorts and adding the Ports to the VLan
        for (WPort wPort : wports) {
            path = ControllerData.getPath(wPort.getTrunk());
            vlan.addPorts(path);
        }

        // Setting the TrunkPorts for all Nodes and wPorts
        Response response = ControllerNetPut.setTrunkPort(vlan);
        return response;

    }

    private Response addNodePaused(NodeObjects node, Config config) {
        Response response = node.isAvailable();

        // Check if node is available
        if (response.getStatus() != 200) {
            return response;
        }
        // TODO globbale vlans
        // TODO letzte vlans
        // TODO ausmachen
        nodeConfigs.put(node.getId(), config);
        return null;
    }

    private Response addNodeStopped(NodeObjects node, Config config) {

        // TODO globale vlans

        nodeConfigs.put(node.getId(), config);
        return null;
    }

    public Response addNode(NodeObjects node, Config config) {

        if (!node.isApplicable(config)) {
            return Response.status(500).entity("Node is not applicable for this Config!").build();
        }

        switch (status) {
        case "running":
            return Response.status(400).entity("Can not do this in status " + status).build();
        case "paused":
            return addNodePaused(node, config);
        case "stopped":
            return addNodeStopped(node, config);
        default:
            return Response.status(500).entity("Could not determine status of experiment").build();
        }

    }

    /**
     * Unpauses the experiment from the state "paused".
     * 
     * Simply turns on all nodes in the experiment.
     * 
     * @return an outbound response object with status code and possible error
     *         messages
     */
    public Response unpause() {
        Response response = this.turnOn();
        return response;
    }

    /**
     * Firstpause describes the state change from "stopped" to "paused".
     * 
     * This has the following effects:
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

        // Check availability of nodes
        for (NodeObjects node : nodes) {
            response = node.isAvailable();
            if (response.getStatus() != 200) {
                responseString += (String) response.getEntity();
                status = 500;
                success = false;
            }
        }

        if (!success) {
            return Response.status(status).entity(responseString).build();
        }

        // Deploying the Config VLans
        response = deployConfigVlans();

        if (response.getStatus() != 200) {
            return response;
        }

        // Turning off the power
        // response = this.turnOff();
        // TODO errorhandling

        // TODO vms starten

        this.setStatus("paused");
        return Response.status(status).entity(responseString).build();
    }

    /**
     * Pauses the experiment.
     * 
     * <p>
     * To do this all the nodes are turned off. If something goes wrong
     * 
     * @return an Outbound Response Object with status code and message body in
     *         error case
     */
    public Response pause() {
        Response response;
        int responseStatus = 200;
        String responseString = "";

        /**
         * for (NodeObjects node : nodes) { response = node.turnOff();
         * if(response.getStatus() != 200) { responseStatus = 500;
         * responseString += (String) response.getEntity(); } }
         * 
         * this.setStatus("paused"); return
         * Response.status(responseStatus).entity(responseString).build();
         **/
        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Stops an Experiment.
     * 
     * To stop an experiment the following actions are taken:
     * 
     * <li>The VLans are modified so that the components from the nodes are not
     * connected anymore and do not belong to any VLan</li> <li>The virtual
     * machines are stopped</li> <li>The power for all nodes is turned off</li>
     * 
     * @return an outbound response object.
     */
    public Response stop() {

        // TODO VMs stoppen
        // TODO Letzten Vlans aufheben
        // TODO errohandling

        // All Switchports from all Nodes
        Set<String> switchports = new HashSet<String>();
        Set<String> vlanports = new HashSet<String>();
        VLan vlan;
        Response response;
        String responseString = "";
        int responseStatus = 200;

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
        
        System.out.println(gson.toJson(vlan));
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
                localVlans.remove(vLan);
            }
            ControllerNetDelete.freeLocalVlan(vLan.getId());
        }

        // Turning off the Power
        // response = this.turnOff();
        //    if(response.getStatus() != 200) {
         //       responseString += (String) response.getEntity();
         //       responseStatus = 500;
         //   }

        this.setStatus("stopped");
        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * Deploys the Config-VLans
     * 
     * @return
     */
    private Response deployConfigVlans() {

        Config config;
        VLan vlan = null;
        String port;
        int responseStatus = 200;
        String responseString = "";

        Response response;

        for (NodeObjects node : nodes) {

            config = nodeConfigs.get(node.getId());

            for (Wire wire : config.getWires()) {

                if (wire.hasUplink()) {
                    // Global VLan
                    LinkedList<String> portList = new LinkedList<String>();
                    Set<String> endpoints = wire.getEndpoints();
                    endpoints.remove("*");
                    for (String endpoint : endpoints) {
                        port = node.getPortByRole(endpoint);
                        portList.add(port);
                    }

                    for (WPort wport : wports) {
                        portList.add(wport.getPort());
                    }
                    
                    vlan = globalVlan;
                    vlan.addPorts(portList);

                    response = ControllerNetPut.addPort(portList, vlan.getId());

                    if (response.getStatus() != 200) {
                        responseStatus = 500;
                        responseString += (String) response.getEntity();
                    }
                    
                } else {
                    // Local VLan
                    VLan vlan2 = null;
                    response = ControllerNetGet.getLocalVlan(); 
                    if (response.getStatus() == 200) {
                      
                        
                        vlan2 = gson.fromJson((String) response.getEntity(), VLan.class);
                        vlan2.setName(this.id);
                        System.out.println("Added local VLan: Local " + this.id + "VLan");
                        vlan2.setPortList(new HashSet<String>());

                        LinkedList<String> trunkPortList = new LinkedList<String>();
                        LinkedList<String> switchPortList = new LinkedList<String>();

                        boolean first = true;
                        String firstEndpoint = null;
                        // Calculating the Path from one local Port to all the other local ports
                        for (String endpoint : wire.getEndpoints()) {
                           
                            if(first) {
                                firstEndpoint = node.getTrunkPortByRole(endpoint);;
                                first = false;
                            } else {
                                System.out.println("TRUNKPORT LIST ADD " + ControllerData.getPath(firstEndpoint, node.getTrunkPortByRole(endpoint)));
                                trunkPortList.addAll(
                                        ControllerData.getPath(firstEndpoint, node.getTrunkPortByRole(endpoint)));
                            }
                            System.out.println("SWITCHPORT LIST ADD " + node.getPortByRole(endpoint));
                            switchPortList.add(node.getPortByRole(endpoint));
                        }
                        
                        vlan2.addPorts(trunkPortList);
                        String outString = gson.toJson(vlan2);
                        System.out.println("SWITCHPORTLIST " + switchPortList);
                        System.out.println("TRUNKPORTLIST " + trunkPortList);
                        System.out.println("VLAN2 DIE ERSTE TRUNKS: " + outString);
                        
                        if(!trunkPortList.isEmpty()) {
                            ControllerNetPut.setTrunkPort(vlan2);
                            vlan2.clear();
                        
                            vlan2.addPorts(switchPortList);
                            ControllerNetPut.addPort(vlan2);
                            
                            vlan2.addPorts(trunkPortList);
                            vlan2.addPorts(switchPortList);
                            localVlans.add(vlan2);
                        
                        } else {
                            vlan2.addPorts(switchPortList);
                            ControllerNetPut.setPort(vlan2);
                        
                            vlan2.addPorts(switchPortList);
                            localVlans.add(vlan2);
                        }
                        
                         outString = gson.toJson(vlan2);
                        System.out.println("VLAN2 DIE ZWEITE SWITCHPORTS: " + outString);
                        System.out.println("Going to Add VLan: " + trunkPortList + switchPortList + " on ID: "
                                + vlan2.getId());
                    
                    } else {
                        responseStatus = 500;
                        responseString += "Could not get a local VLan for '" + node.getId()
                                + "' on experiment '" + this.name + "'"
                                + (String) response.getEntity();
                    }
                }
            }
        }

        System.out.println("DEPLOY RESPONSE STATUS: " + responseStatus);

        return Response.status(responseStatus).entity(responseString).build();
    }

    /**
     * 
     * @return
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

        if (!success) {
            return Response.status(status).entity(responseString).build();
        }

        // Deploying the Config VLans
        response = deployConfigVlans();

        if (response.getStatus() != 200) {
            return response;
        }

        // TODO vms starten

        // Turning the power on
        /**
         * for (NodeObjects node : nodes) { response = node.turnOn(); }
         * if(response.getStatus() != 200) { return response; }
         */

        this.setStatus("running");
        return Response.status(status).entity(responseString).build();
    }

    public String isConsistent() {

        String returnString;
        switch (status) {
        case "running":
            returnString = ControllerNetGet.isConsistent(globalVlan);
            for (VLan vLan : localVlans) {
                returnString+= ControllerNetGet.isConsistent(vLan);
            }
            return returnString;
        case "paused":
            returnString = ControllerNetGet.isConsistent(globalVlan);
            for (VLan vLan : localVlans) {
                returnString+= ControllerNetGet.isConsistent(vLan);
            }
            return returnString;
        case "stopped":
            return ControllerNetGet.isConsistent(globalVlan);
        default:
            return null;
        }
    }
    // TODO consistency check auch auf ports
    // TODO: testen mit mehrern lokalen vlans
}
