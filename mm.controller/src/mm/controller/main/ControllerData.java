package mm.controller.main;

import java.util.HashMap;
import java.util.LinkedList;

import mm.controller.modeling.Component;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;

/**
 * Holds a static List with all experiments. Works as the central data point for
 * the experiments with various methods of manipulation
 */
public class ControllerData {

	/* !-- Global List of Experiments --! */
	private static LinkedList<Experiment> EXPERIMENT_LIST = new LinkedList<Experiment>();
	/* !-- Global Mapping from PORTs to COMPONENTs --! */
	private static HashMap<String, Component> PORT_TO_COMPONENT = new HashMap<String, Component>();
	/* !-- Global HashMap of all known Nodes--! */
	private static HashMap<String, NodeObjects> ALL_NODES = new HashMap<String, NodeObjects>();

	public static Component getComponentByPort(String port) {
		return PORT_TO_COMPONENT.get(port);
	}

	protected ControllerData() {
		EXPERIMENT_LIST = new LinkedList<Experiment>();
		PORT_TO_COMPONENT = new HashMap<String, Component>();
		ALL_NODES = new HashMap<String, NodeObjects>();
	}

	protected ControllerData(LinkedList<Experiment> expList) {
		EXPERIMENT_LIST = expList;
		PORT_TO_COMPONENT = new HashMap<String, Component>();

	}

	public static void addNode(NodeObjects node){
		ALL_NODES.put(node.getId(), node);
		
	}
	
	public static HashMap<String, NodeObjects> getAllNodes(){
		return ALL_NODES;
	}
	
	public static LinkedList<NodeObjects> getAllNodesAsList(){
		
		LinkedList<NodeObjects> returnList = new LinkedList<NodeObjects>(ALL_NODES.values());
		
		return returnList;
		
	}
	public static void addPort(String port, Component component){
		PORT_TO_COMPONENT.put(port, component);
	}
	
	
	public LinkedList<Experiment> getExpList() {
		return EXPERIMENT_LIST;
	}

	
	static public NodeObjects getNodeById(String id){
		
		return ALL_NODES.get(id);
	}
	
	static public void setAllNodes(HashMap<String, NodeObjects> map) {
	  ALL_NODES = map;
	}
	
	/**
	 * Returns the Experiment with the given ID.
	 * 
	 * @param id
	 *            ID of the Experiment
	 * @return Experiment with the ID, null if no experiment was found
	 */
	static public Experiment getExpById(String id) {

		Experiment exp = null;
		for (Experiment experiment : EXPERIMENT_LIST) {
			if (experiment.getId().equals(id)) {
				exp = experiment;
			}
		}
		return exp;
	}

	/**
	 * Adds a experiment to the global data.
	 * 
	 * @param exp
	 *            experiment to add
	 */
	static public void addExp(Experiment exp) {
		EXPERIMENT_LIST.add(exp);
	}

	/**
	 * Removes a experiment from the global data.
	 * 
	 * @param exp
	 *            experiment to remove
	 * @return bool true if experiment was in the list and was removed, false if
	 *         experiment was not in the list
	 */
	public static boolean removeExp(Experiment exp) {

		boolean bool = false;
		for (Experiment experiment : EXPERIMENT_LIST) {
			if (exp.equals(experiment)) {
				EXPERIMENT_LIST.remove(experiment);
				bool = true;
			}
		}
		return bool;
	}

	/**
	 * Removes a experiment from the global data
	 * 
	 * @param id
	 *            ID of the experiment to remove
	 * @return bool true if experiment was in the list and was removed, false if
	 *         experiment was not in the list
	 */
	public static boolean removeExp(String id) {
		boolean bool = false;

		for (Experiment experiment : EXPERIMENT_LIST) {
			if (experiment.getId().equals(id)) {
				EXPERIMENT_LIST.remove(experiment);
				bool = true;
			}
		}

		return bool;
	}
	/**
	 * Returns the List containing all experiments.
	 * 
	 * @return LinkedList<Experiment> 
	 */
	public static LinkedList<Experiment> getAllExp(){
		return EXPERIMENT_LIST;
	}
	
	/**
	 * Checks if a node with the given Node Object exists.
	 * <p>
	 * @param node  node Object to check
	 * @return boolean
	 */
	public static boolean exists(NodeObjects node){
		
		if( ALL_NODES.get(node.getId()) == null) {
			return false;
		} else {
			return true;
		}
		
	}
	/**
	 * Checks if a node with the given node ID exists.
	 * <p>
	 * @param id  node ID to check
	 * @return  boolean
	 */
	public static boolean existsNode(String id){
		
		if( ALL_NODES.get(id) == null) {
			return false;
		} else {
			return true;
		}
		
	}
	/**
	 * Checks if a experiment with the given ID exists.
	 * <p>
	 * @param id  experiment id to check
	 * @return  boolean
	 */
	public static boolean existsExp(String id){
		
		for (Experiment experiment : EXPERIMENT_LIST) {
			if(experiment.getId().equals(id)){
				return true;
			}
		}
	
		return false;
	}
	
	/**
	 * Returns if a experiment object exists in the global data.
	 * 
	 * @param id
	 *            experiment ID to look for
	 * @return false if the experiment does not exist, true if it does
	 */
	public static boolean exists(Experiment exp) {
		
		String id = exp.getId();
		
		boolean bool = false;

		for (Experiment experiment : EXPERIMENT_LIST) {
			if (experiment.getId().equals(id)) {
				bool = true;
			}
		}
		return bool;
	}

}
