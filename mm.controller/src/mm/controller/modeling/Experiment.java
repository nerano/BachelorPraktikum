package mm.controller.modeling;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;

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
	        
	        if(vlan.isGlobal()) {
	            response = ControllerNetDelete.freeGlobalVlan(vlan.getId());
	        } else {
	            response = ControllerNetDelete.freeLocalVlan(vlan);
	        }
	        
        }
	    //TODO VMs löschen
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
	    
	    for (VLan vLan: vlans) {
	        if(vLan.isGlobal()) {
	            vlan = vLan;
	            break;
	        }
        }
	    
	    LinkedList<String> path;
	    for (NodeObjects node : nodes) {
            
	        path = ControllerData.getPath(node.getTrunk());
	        vlan.addPorts(path);
	        
        }
	
	    Response response = ControllerNetPut.setTrunkPort(vlan);
	    return response;
	
	}
	
	
	private Response addNodeRunning() {
	    //TODO Check ob verfügbar
	    //TODO globale vlans
	    //TODO letzte vlans
	    //TODO anmachen
	    return null;
	}
	
	private Response addNodePaused() {
        //TODO check of verfügbar
	    //TODO globbale vlans
	    //TODO letzte vlans
	    //TODO ausmachen
	    return null;
    }
	
	private Response addNodeStopped() {
	    //TODO globale vlans
	    return null;
	}
	
	
	public Response addNode(NodeObjects node, Config config) {
	    
	    if(!node.isApplicable(config)) {
	        return Response.status(500).entity("Node is not applicable for this Config!").build();
	    }
	    
	    switch (status) {
        case "running":
            return addNodeRunning();
        case "paused":
            return addNodePaused();
        case "stopped":
            return addNodeStopped();
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
	    return Response.status(responseStatus).entity(responseString).build();
	    
	}
	
	public Response stop() {
	    
	    //TODO Strom aus
	    //TODO VMs stoppen
	    //TODO Vlans aufheben
        //TODO errohandling
        
        for (NodeObjects node : nodes) {
            node.turnOff();
          //TODO errohandling
        }
        
        return null;
	    
	}
	
	public Response start() {
	    
	    //TODO letzten vlans
	    //TODO vms starten
	    //TODO strom an
	    
	    return null;
	}
}
