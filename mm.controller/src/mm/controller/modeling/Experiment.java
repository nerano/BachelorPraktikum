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
	/**
	 * Unique ID of an Experiment
	 */
	private String id;
	/**
	 * List of Nodes in the Experiment
	 */
	private LinkedList<NodeObjects> nodes;
	/**
	 * List of VLans in the Experiment
	 */
	private LinkedList<VLan> vlans;
	private LinkedList<VLan> globalVLans = new LinkedList<VLan>();
	private LinkedList<WPort> wports;
	private HashMap<String, Config> nodeConfigs = new HashMap<String, Config>();
	//TODO VMs
    private String user;
	
	/**
	 * running 
	 * stopped
	 * error
	 * paused
	 */
	private String status;
	
	
	public void setUser(String user) {
        this.user = user;
    }


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
	
	public Experiment(String id) {

		this.id = id;
		this.nodes = new LinkedList<NodeObjects>();
		this.vlans = new LinkedList<VLan>();

	}
	
	public Experiment(String id, String user){
		this.id = id;
		this.nodes = new LinkedList<NodeObjects>();
		this.vlans = new LinkedList<VLan>();
		this.user = user;
	}

	public Experiment(String id, LinkedList<NodeObjects> nodes) {

		this.id = id;
		this.nodes = nodes;
		this.vlans = new LinkedList<VLan>();

	}

	public Experiment(String id, LinkedList<NodeObjects> nodes,
			LinkedList<VLan> vlans) {

		this.id = id;
		this.nodes = nodes;
		this.vlans = vlans;

	}

	public Experiment(String id, NodeObjects node) {

		this.id = id;
		this.nodes = new LinkedList<NodeObjects>();
		nodes.add(node);

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
	

	private void setVlans(LinkedList<VLan> list) {
		this.vlans = list;
	}

	public void addVLan(VLan vlan){
		vlans.add(vlan);
	}
	
	private void setId(String id) {
		this.id = id;
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
	    for (VLan vlan : this.vlans) {
	        
	            response = ControllerNetDelete.freeGlobalVlan(vlan.getId());
	       
        }
	    //TODO VMs l�schen
	    return Response.ok().build();
	    
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
	    for (VLan vLan: vlans) {
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
	
	/**
	 * Pauses the experiment. 
	 * 
	 * <p>
	 * To do this all the nodes are turned off. If something goes wrong 
	 * @return an Outbound Response Object with status code and message body in error case
	 */
	public Response pause() {
	    
	    Response response;
	    int responseStatus = 200;
	    String responseString = "";
	    
	    for (NodeObjects node : nodes) {
           response = node.turnOff();
           if(response.getStatus() != 200) {
               responseStatus = 500;
               responseString += (String) response.getEntity();
           }
	    }
	    
	    this.setStatus("paused");
	    return Response.status(responseStatus).entity(responseString).build();
	    
	}
	
	public Response stop() {
	    
	    //TODO Strom aus
	    //TODO VMs stoppen
	    //TODO Vlans aufheben
        //TODO errohandling
	    
	    
	    for (VLan vLan : vlans) {
            
	        System.out.println(gson.toJson(vLan));
	        
	        if(vLan.isGlobal()) {
	            // Global VLan
	            Set<String> allNodes = new HashSet<String> (vLan.getPortList());
	            Set<String> nodesNotToRemove = new HashSet<String>();
	            LinkedList<String> path;
	            
	            // Requesting the Path for the Nodes and adding the Ports to the VLan
	            for (NodeObjects node : nodes) {
	                path = ControllerData.getPath(node.getTrunk());
	                nodesNotToRemove.addAll(path);
	            }
	            
	            // Requesting the Path for the wPorts and adding the Ports to the VLan
	            for (WPort wPort : wports) {
	                path = ControllerData.getPath(wPort.getTrunk());
	                nodesNotToRemove.addAll(path);
	            }
	            
	            allNodes.removeAll(nodesNotToRemove);
	            
	            
	            VLan vlan = new VLan(vLan.getId(), true);
	            vlan.setPortList(allNodes);
	            ControllerNetPut.removePort(vlan);
	            
	        } else {
	            // Local VLan
	            
	            ControllerNetDelete.freeLocalVlan(vLan.getId());
	            
	        }
	        
	        
	        
        }
        
        /** for (NodeObjects node : nodes) {
            node.turnOff();
          //TODO errohandling
        } **/
        
        
        this.setStatus("stopped");
        return Response.ok().build();
	    
	}
	
	
	private Response deployConfigVlans() {
	    
	    Config config;
	    VLan vlan = null;
	    String port;
	    int status = 200;
	    String responseString = "";
	    
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
	                
	                ControllerNetPut.addPort(portList, vlan.getId());
	                
	            } else {
	               VLan vlan2 = null;
	                // Local VLan
	                Response response = ControllerNetGet.getLocalVlan(); //TODO changed later on
	                
	                if(response.getStatus() == 200) {
	                   vlan2 = gson.fromJson((String) response.getEntity(), VLan.class);
	                   
	                   vlan2.setName("Local " + this.id + "VLan");
	                   System.out.println("ADDED LOCAL LAN " + this.id + "VLan");
	                   vlan2.setPortList(new HashSet<String>());
	                }
	                
	                
	                LinkedList<String> portList = new LinkedList<String>();
	                
	                for (String endpoint : wire.getEndpoints()) {
	                    port = node.getPortByRole(endpoint);
	                    portList.add(port);
                    }
	                vlan2.addPorts(portList);
	                System.out.println("Going to Add VLan: " + portList + " on ID: " + vlan2.getId());
	                vlans.add(vlan2);
	                ControllerNetPut.setPort(vlan2);
	            }
	            
            }
	        
        }
	    
	   return Response.status(status).entity(responseString).build();
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
	    
	    // Setting the Config VLans
	    response = deployConfigVlans();
	    
	    //TODO vms starten
	    
	    
	    // Turning the power on
	   /** for (NodeObjects node : nodes) {
            node.turnOn();
        } */
	    
	    
	    
	    this.setStatus("running");
	    return Response.status(status).entity(responseString).build();
	}
}
