package mm.controller.modeling;

import java.io.UnsupportedEncodingException;
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

    
    private transient Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private String id;
	private LinkedList<NodeObjects> nodes = new LinkedList<NodeObjects>();
	/**
	 * List of VLans in the Experiment
	 */
	private LinkedList<VLan> vlans = new LinkedList<VLan>();
	private LinkedList<VLan> globalVLans = new LinkedList<VLan>();
	private LinkedList<WPort> wports;
	private HashMap<String, Config> nodeConfigs = new HashMap<String, Config>();
	//TODO VMs
    private String user;
    private String name;
	
	/**
	 * running 
	 * stopped
	 * error
	 * paused
	 */ 
	private String status;
	
	

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

	public String getUser(){
		return this.user;
	}
	
	public Experiment(String name, String user) {

		this.name = name;
		this.user = user;
		
		this.id = user + name;
	}
	
	

	public LinkedList<PowerSource> status() throws UnsupportedEncodingException {
	    
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
	
	public void addVLan(VLan vlan){
		vlans.add(vlan);
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
	    for (VLan vlan : vlans) {
            list.add(vlan.getId());
        }
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

	public boolean isNodeActive(NodeObjects node) {
	  //TODO UMSCHREIBEN NODEACTIVE BENUTZT DAS GLEICHE WIE OB NODE FREI IST 
		LinkedList<Component> compList = node.getComponents();

		for (Component component : compList) {
			if (isVLanIdInExperiment(component.getvLanId())) {
				return true;
			}
		}
		
		
		return false;

	}

	private boolean isVLanIdInExperiment(int id) {
		
		for (VLan vlan : vlans) {
			if (vlan.getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Deletes all the data in the network from this experiment. 
	 * 
	 * Deletes the VLans from this experiment and deletes the virtual machines from the sever.
	 *
	 * @return
	 */
	public Response destroy() {
	    System.out.println("Destroying Experiment " + this.id);
	    Response response; 
	    String responseString = "";
	    int responseStatus = 200;
	    
	    for (VLan vlan : this.vlans) {
	        
	            //response = ControllerNetDelete.freeGlobalVlan(vlan.getId());
	            
	            response = ControllerNetPut.removeVlan(vlan);
	            
	            if(response.getStatus() != 200) {
	                
	                System.out.println((String) response.getEntity());
	                // TODO errorhandling
	                responseStatus = 500;
	            }
	       
        }
	    //TODO VMs l�schen
	    return Response.status(responseStatus).entity(responseString).build();
	    
	}
	
	
	/**
	 * Gets a global VLan from the NetService and adds it to the experiment.
	 * 
	 * @return  an outbound Response Object.
	 */
	public Response addGlobalVlan() {
	    
	    Response response = ControllerNetGet.getGlobalVlan();
	    
	    if(response.getStatus() == 200) {
	       VLan vlan = gson.fromJson((String) response.getEntity(), VLan.class);
	       vlan.setName("Global " + this.id + "VLan");
	       vlans.add(vlan);
	       globalVLans.add(vlan);
	       System.out.println("Added VLan " + vlan.getId() + " to experiment " + this.id);
	       return Response.ok().build(); 
	    } else {
	        return response;
	    }
	    
	}
	/**
	 * Deploys all Trunkports for this experiment.
	 * @return
	 */
	public Response deployAllTrunks() {
	    
	    VLan vlan = null;
	    
	    //TODO must be changed accordingly
	    for (VLan vLan: globalVLans) {
	        if(vLan.isGlobal()) {
	            vlan = vLan;
	            break;
	        }
        }
	    
	    LinkedList<String> path;
	    
	    // Requesting the Path for the Nodes and adding the Ports to the VLan
	    for (NodeObjects node : nodes) {
	        path = ControllerData.getPath(node.getTrunk());
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
	
	
	private Response addNodeRunning(NodeObjects node, Config config) {
	    //TODO Check ob verf�gbar
	    node.isAvailable();
	    
	    
	    //TODO globale vlans
	    
	    
	    
	    //TODO letzte vlans
	    //TODO anmachen
	    return null;
	}
	
	private Response addNodePaused(NodeObjects node, Config config) {
        //TODO check of verf�gbar
        node.isAvailable();
	    //TODO globbale vlans
	    //TODO letzte vlans
	    //TODO ausmachen
	    return null;
    }
	
	private Response addNodeStopped(NodeObjects node, Config config) {
	    
	    //TODO globale vlans
	    
	    
	    
	    return null;
	}
	
	
	public Response addNode(NodeObjects node, Config config) {
	    
	    if(!node.isApplicable(config)) {
	        return Response.status(500).entity("Node is not applicable for this Config!").build();
	    }
	    
	    switch (status) {
        case "running":
            return addNodeRunning(node, config);
        case "paused":
            return addNodePaused(node, config);
        case "stopped":
            return addNodeStopped(node, config);
        default:
            return Response.status(500).entity("Could not determine status of experiment").build();
        }
	    
	}
	
	public Response unpause() {
	    //TODO ALLES
	    
	    return null;
	}
	/**
	 * Firstpause describes the state change from "stopped" to "paused".
	 * 
	 * This has the following effects:
	 * 
	 * <li>There is an availability check if all nodes are ready to use</li>
	 * <li>All nodes are connected to the VLans and local VLans are initialized </li>
	 * <li>Virtual machines are started </li>
	 * <li>The power of all nodes is turned off explicitly </li>
	 *
	 * @return  outbound response object
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
	            if(response.getStatus() != 200) {
	                responseString += (String) response.getEntity();
	                status = 500;
	                success = false;
	            }
	        }
	        
	        if(!success) {
	            return Response.status(status).entity(responseString).build();
	        }
	        
	        // Deploying the Config VLans
	        response = deployConfigVlans();
	        
	        if(response.getStatus() != 200) {
	            return response;
	        }
	        
	        // Turning off the power
	        /** for (NodeObjects node : nodes) {
            response = node.turnOff();
          //TODO errohandling
           
            if(response.getStatus() != 200) {
                responseStatus = 500;
                responseString += "Error on stopping Experiment '" + this.id + "'\n" 
                            + (String) response.getEntity();
            }
        } **/
	        
	        //TODO vms starten
	        
	        
	        
	        
	        
	        
	        
	        this.setStatus("paused");
	        return Response.status(status).entity(responseString).build();
	}
	
	
	/**
	 * Pauses the experiment. 
	 * 
	 * <p>
	 * To do this all the nodes are turned off. If something goes wrong 
	 * @return an Outbound Response Object with status code and message body in error case
	 */
	public Response pause() {
	    //TODO TESTEN
	    Response response;
	    int responseStatus = 200;
	    String responseString = "";
	    
	   /**  for (NodeObjects node : nodes) {
           response = node.turnOff();
           if(response.getStatus() != 200) {
               responseStatus = 500;
               responseString += (String) response.getEntity();
           }
	    }
	    
	    this.setStatus("paused");
	    return Response.status(responseStatus).entity(responseString).build();
	     **/
	    return Response.ok().build();
	}
	
	
	/**
	 * Stops an Experiment.
	 * 
	 * To stop an experiment the following actions are taken:
	 * 
	 * <li> The VLans are modified so that the components from the nodes are not connected
	 * anymore and do not belong to any VLan </li>
	 * <li> The virtual machines are stopped </li>
	 * <li> The power for all nodes is turned off</li>
	 
	 * @return  an outbound response object.
	 */
	public Response stop() {
	    
	    //TODO Strom aus
	    //TODO VMs stoppen
	    //TODO Letzten Vlans aufheben
        //TODO errohandling
	    
	    //All Switchports from all Nodes
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
	    
	    
	    for (VLan vLan : vlans) {
            
	        System.out.println(gson.toJson(vLan));
	        
	        vlanports.clear();
	        
	        vlanports.addAll(vLan.getPortList());
	        
	        vlanports.retainAll(switchports);
	        
	        if(vLan.isGlobal()) {
	            vlan = new VLan(vLan.getId(), true);
	        } else {
	            vlan = new VLan(vLan.getId(), false);
	        }
	        
	        vlan.setPortList(vlanports);
	        
	        // Remove the Untagged Switchports from the VLan
	        response = ControllerNetPut.removePort(vlan);
	        
	        if(response.getStatus() != 200) {
	            responseStatus = 500;
	            responseString += "Error on stopping Experiment '" + this.id + "'\n" 
	                        + (String) response.getEntity();
	        } else {
	            // Remove all removed ports from the internal representation
	            vLan.getPortList().removeAll(vlanports);
	        }
	    
	    }
        
        /** for (NodeObjects node : nodes) {
            response = node.turnOff();
          //TODO errohandling
           
            if(response.getStatus() != 200) {
                responseStatus = 500;
                responseString += "Error on stopping Experiment '" + this.id + "'\n" 
                            + (String) response.getEntity();
            }
        } **/
        
        this.setStatus("stopped");
        return Response.status(responseStatus).entity(responseString).build();
	    
	}
	
	/**
	 * Deploys the Config-VLans
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
                
	            if(wire.hasUplink()) {
	                // Global VLan
	                LinkedList<String> portList = new LinkedList<String>();
	                Set<String> endpoints = wire.getEndpoints();
	                endpoints.remove("*");
	                for (String endpoint : endpoints) {
	                    port = node.getPortByRole(endpoint);
	                    portList.add(port);
                    }
	                
	                vlan = globalVLans.getFirst(); //TODO probably changed later on
	                vlan.addPorts(portList);
	                
	                response =  ControllerNetPut.addPort(portList, vlan.getId());
	                
	                if(response.getStatus() != 200) {
	                    responseStatus = 500;
	                    System.out.println("HIER 500 1");
	                    responseString += (String) response.getEntity();
	                }
	            } else {
	                // Local VLan
	                VLan vlan2 = null;
	                response = ControllerNetGet.getLocalVlan(); //TODO changed later on
	                
	                if(response.getStatus() == 200) {
	                   vlan2 = gson.fromJson((String) response.getEntity(), VLan.class);
	                   vlan2.setName("Local " + this.id + "VLan");
	                   System.out.println("Added local VLan: Local " + this.id + "VLan");
	                   vlan2.setPortList(new HashSet<String>());
	                   
	                   
	                   LinkedList<String> portList = new LinkedList<String>();
	                    for (String endpoint : wire.getEndpoints()) {
	                        port = node.getPortByRole(endpoint);
	                        portList.add(port);
	                    }
	                    vlan2.addPorts(portList);
	                    System.out.println("Going to Add VLan: " + portList + " on ID: " + vlan2.getId());
	                    vlans.add(vlan2);
	                    ControllerNetPut.setPort(vlan2);
	                } else {
	                    responseStatus = 500;
	                    System.out.println("HIER 500 2");
	                    
	                    responseString += "Could not get a local VLan for '" + node.getId() 
	                                + "' on experiment '" + this.name +  "'"
	                            + (String) response.getEntity();
	                }
	            }
            }
        }
	    
	    
	    //TODO deploy ports for wports
	    
	    
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
	        if(response.getStatus() != 200) {
	            responseString += (String) response.getEntity();
	            status = 500;
	            success = false;
	        }
        }
	    
	    if(!success) {
	        return Response.status(status).entity(responseString).build();
	    }
	    
	    // Deploying the Config VLans
	    response = deployConfigVlans();
	    
	    if(response.getStatus() != 200) {
	        return response;
	    }
	    
	    //TODO vms starten
	    
	    
	    // Turning the power on
	   /** for (NodeObjects node : nodes) {
            response = node.turnOn();
        }
	    if(response.getStatus() != 200) {
            return response;
        } */
	    
	    this.setStatus("running");
	    return Response.status(status).entity(responseString).build();
	}
}
