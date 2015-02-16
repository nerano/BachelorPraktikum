package mm.controller.modeling;

import java.util.Iterator;
import java.util.LinkedList;

public class Experiment implements Cloneable {

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

	private String user;
	private boolean running;
	
	
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
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

	public void setList(LinkedList<NodeObjects> list) {
		this.nodes = list;
	}

	public LinkedList<NodeObjects> getList() {
		return nodes;
	}

	@Override
	public Experiment clone() throws CloneNotSupportedException {
		Experiment cloned = (Experiment) super.clone();
		cloned.setList((LinkedList<NodeObjects>) cloned.getList().clone());

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

}
