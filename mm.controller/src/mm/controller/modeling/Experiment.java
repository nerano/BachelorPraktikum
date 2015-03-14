package mm.controller.modeling;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mm.controller.net.ControllerNetDelete;
import mm.controller.net.ControllerNetGet;
import mm.controller.power.ControllerPowerGet;
import mm.controller.power.ControllerPowerPut;

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
	
	//TODO VMs
    private String user;
	
	/**
	 * running 
	 * stopped
	 * error
	 * paused
	 */
	private String status;
	
	
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

	public void updateNodeStatusVLan(LinkedList<VLan> vlanList) {

		for (NodeObjects nodeObject : nodes) {
			nodeObject.updateNodeStatusVLan(vlanList);
		}
	}

	public LinkedList<VLan> getVLans() {
		return vlans;
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

	public void addNode(NodeObjects node) {
		nodes.add(node);
	}

	public void setNodeList(LinkedList<NodeObjects> list) {
		this.nodes = list;
	}

	public LinkedList<NodeObjects> getList() {
		return nodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Experiment clone() throws CloneNotSupportedException {
		Experiment cloned = (Experiment) super.clone();
		cloned.setNodeList((LinkedList<NodeObjects>) cloned.getList().clone());

		cloned.setId(new String(cloned.getId()));

		cloned.setVlans((LinkedList<VLan>) cloned.getVLans().clone());
	
		
		return cloned;
	
	
	
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
	
	public Response destroy() {
	    System.out.println("Destroying Experiment " + this.id);
	    
	    for (VLan vlan : this.vlans) {
	        ControllerNetDelete.freeGlobalVLan(vlan.getId());
        }
	    
	    
	    
	    
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
	       vlan.setName("Global: " + this.id + "VLan");
	       vlans.add(vlan);
	       System.out.println("Added VLan " + vlan.getId() + " to experiment " + this.id);
	       return Response.ok().build(); 
	    } else {
	        return response;
	    }
	    
	}
	
	
	public Response addNode() {
	    //TODO CHECK ob node verfügbar
	    //TODO entsprechend handeln
	    
	    return null;
	    
	}
	
	public Response pause() {
	    //TODO Strom aus
	    //TODO errohandling
	    
	    for (NodeObjects node : nodes) {
            node.turnOff();
          //TODO errohandling
	    }
	    
	    
	    
	    return null;
	    
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
	    return null;
	}
}
