package mm.controller.main;

import java.util.*;

import mm.controller.modeling.Config;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.Interface;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.WPort;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;

/**
 * Holds a static List with all experiments. Works as the central data point for
 * the experiments with various methods of manipulation
 */
public class ControllerData {

  /* !-- Global List of Experiments --! */
  private static LinkedList<Experiment>                       EXPERIMENT_LIST   = new LinkedList<Experiment>();
  /* !-- Global Mapping from PORTs to COMPONENTs --! */
  private static HashMap<String, Interface>                   PORT_TO_INTERFACE = new HashMap<String, Interface>();
  /* !-- Global HashMap of all known Nodes--! */
  private static HashMap<String, NodeObjects>                 ALL_NODES         = new HashMap<String, NodeObjects>();
  /* !-- Global List of WPorts --! */
  private static Set<WPort>                                   ALL_WPORTS;
  private static String                                       START_VERTEX;

  private static Set<Config>                                  ALL_CONFIGS;

  private static UndirectedGraph<String, DefaultEdge>         TOPOLOGY;                                              // =
                                                                                                                      // new
                                                                                                                      // SimpleGraph<String,
                                                                                                                      // DefaultEdge>
                                                                                                                      // (DefaultEdge.class);
  private static BellmanFordShortestPath<String, DefaultEdge> BFSP;

  ControllerData(HashMap<String, NodeObjects> allNodes,
      HashMap<String, Interface> portToInterface,
      UndirectedGraph<String, DefaultEdge> topology, String startVertex,
      Set<Config> configSet,
      Set<WPort> portSet) {

    setPortToInterface(portToInterface);

    setAllNodes(allNodes);
    setwPorts(portSet);
    setConfigs(configSet);
    setTopology(topology, startVertex);

  }

  private static void setPortToInterface(HashMap<String, Interface> portToInterface) {
    PORT_TO_INTERFACE = portToInterface;
  }

  protected static void setTopology(UndirectedGraph<String, DefaultEdge> topology,
      String startVertex) {

    TOPOLOGY = topology;
    START_VERTEX = startVertex;

    BFSP = new BellmanFordShortestPath<String, DefaultEdge>(TOPOLOGY, startVertex);
  }

  protected static void setwPorts(Set<WPort> portSet) {
    ALL_WPORTS = portSet;
  }

  public static Interface getInterfaceByPort(String port) {
    return PORT_TO_INTERFACE.get(port);
  }

  /**
   * Returns a list of ports which define a path from the startVertex to the
   * given vertex, empty list if no path was found.
   * 
   * 
   * @param vertex
   *          to which the path should be calculated
   * @return
   */
  public static LinkedList<String> getPath(String vertex) {
    try {
      Set<String> set = new HashSet<String>();

      if (vertex.equals(START_VERTEX)) {
        set.add(vertex);
        return new LinkedList<String>(set);
      }

      List<DefaultEdge> edgeList = BFSP.getPathEdgeList(vertex);

      for (DefaultEdge edge : edgeList) {
        set.add(TOPOLOGY.getEdgeSource(edge));
        set.add(TOPOLOGY.getEdgeTarget(edge));
      }
      System.out.println("PATH : ");
      System.out.println(new LinkedList<String>(set));

      return new LinkedList<String>(set);
    } catch (NullPointerException e) {
      e.printStackTrace();
      return null;
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * Returns a Path between two given vertices in the network.
   * 
   * <p>
   * The path is made out of trunkports in the network, described in the
   * topology.xml. If no path is found an empty list is returned.
   * 
   * @param v1
   *          first vertex
   * @param v2
   *          second vertex
   * @return a path between those two vertices, which is made of trunkports in
   *         the network
   */
  public static LinkedList<String> getPath(String v1, String v2) {
    try {

      List<DefaultEdge> edgeList = BellmanFordShortestPath.findPathBetween(TOPOLOGY, v1, v2);

      Set<String> set = new HashSet<String>();

      for (DefaultEdge edge : edgeList) {
        set.add(TOPOLOGY.getEdgeSource(edge));
        set.add(TOPOLOGY.getEdgeTarget(edge));
      }
      System.out.println("PATH : ");
      System.out.println(new LinkedList<String>(set));

      return new LinkedList<String>(set);
    } catch (IllegalArgumentException e) {
      return new LinkedList<String>();
    } catch (Exception e) {
      return new LinkedList<String>();
    }

  }

  /**
   * Sets the List of all Configs, but also calculates for every node the
   * applicable configs. ALL_NODES has to be initialized.
   * 
   * @param configs  Set of Configs to set.
   */
  protected static void setConfigs(Set<Config> configs) {

    ALL_CONFIGS = configs;
    calcConfigsForAllNodes();
  }

  /**
   * Calculates for all Nodes in ALL_NODES the applicable configs.
   */
  protected static void calcConfigsForAllNodes() {
    for (NodeObjects node : ALL_NODES.values()) {
      node.calcApplicableConfigs();
    }
  }

  public static Set<Config> getAllConfigs() {
    return ALL_CONFIGS;
  }

  public static Config getConfig(String name) {
    for (Config config : ALL_CONFIGS) {
      if (config.getName().equals(name)) {
        return config;
      }
    }
    return null;
  }

  public static WPort getWportById(String id) {
    for (WPort wport : ALL_WPORTS) {
      if (wport.getId().equals(id)) {
        return wport;
      }
    }
    return null;
  }

  public static LinkedList<WPort> getAllWPorts() {
    return new LinkedList<WPort>(ALL_WPORTS);
  }


  protected static void setAllExperiments(LinkedList<Experiment> expList) {
    EXPERIMENT_LIST = expList;
  }

  public static HashMap<String, NodeObjects> getAllNodes() {
    return ALL_NODES;
  }

  /**
   * Returns all known Nodes as a list.
   * @return  all known node
   */
  public static LinkedList<NodeObjects> getAllNodesAsList() {

    LinkedList<NodeObjects> returnList = new LinkedList<NodeObjects>(ALL_NODES.values());
    return returnList;

  }

  public LinkedList<Experiment> getExpList() {
    return EXPERIMENT_LIST;
  }

  public static NodeObjects getNodeById(String id) {

    return ALL_NODES.get(id);
  }

  public static void setAllNodes(HashMap<String, NodeObjects> map) {
    ALL_NODES = map;
  }

  /**
   * Returns the Experiment with the given ID.
   * 
   * @param id
   *          ID of the Experiment
   * @return Experiment with the ID, null if no experiment was found
   */
  public static Experiment getExpById(String id) {

    for (Experiment experiment : EXPERIMENT_LIST) {
      if (experiment.getId().equals(id)) {
        return experiment;
      }
    }
    return null;
  }

  /**
   * Returns the experiment with is affiliated with the given VLan ID.
   * 
   * @param id  VLAN ID to ask for
   * @return  experiment which owns the VLAN ID or null if none was found
   */
  public static Experiment getExpByVlanId(int id) {
    LinkedList<Integer> idList;
    for (Experiment experiment : EXPERIMENT_LIST) {
      idList = experiment.getAllVlanIds();
      if (idList.contains(id)) {
        return experiment;
      }
    }
    return null;
  }

  /**
   * Returns a list of all experiments of a user.
   *
   * <p>
   * Returns a list of all currently existing experiments for a user. No input
   * Parameters are required. T
   * </p>
   * 
   * @return a LinkedList with alle experiments from the given user can be null
   *         if there is no such experiment
   */
  public static LinkedList<Experiment> getExpByUser(String user) {
    LinkedList<Experiment> temp = new LinkedList<Experiment>();

    for (Experiment exp : getAllExp()) {
      if (exp.getUser().equals(user)) {
        temp.add(exp);
      }
    }
    return temp;
  }

  /**
   * Adds a experiment to the global data.
   * 
   * @param exp
   *          experiment to add
   */
  public static void addExp(Experiment exp) {

    if (!ControllerData.existsExp(exp.getId())) {
      EXPERIMENT_LIST.add(exp);
    }
  }

  /**
   * Removes a experiment from the global data.
   * 
   * @param exp
   *          experiment to remove
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
   * Removes a experiment from the global data.
   * 
   * @param id
   *          ID of the experiment to remove
   * @return bool true if experiment was in the list and was removed, false if
   *         experiment was not in the list
   */
  public static boolean removeExp(String id) {

    for (Experiment experiment : EXPERIMENT_LIST) {
      if (experiment.getId().equals(id)) {
        EXPERIMENT_LIST.remove(experiment);
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the List containing all experiments.
   * 
   * @return LinkedList<Experiment>
   */
  public static LinkedList<Experiment> getAllExp() {
    return EXPERIMENT_LIST;
  }

  /**
   * Checks if a node with the given Node Object exists.
   *
   * @param node
   *          node Object to check
   * @return boolean
   */
  public static boolean exists(NodeObjects node) {

    if (ALL_NODES.get(node.getId()) == null) {
      return false;
    } else {
      return true;
    }

  }

  /**
   * Checks if a node with the given node ID exists.
   * 
   * @param id
   *          node ID to check
   * @return boolean
   */
  public static boolean existsNode(String id) {

    if (ALL_NODES.get(id) == null) {
      return false;
    } else {
      return true;
    }

  }

  /**
   * Checks if a experiment with the given ID exists.
   * 
   * @param id
   *          experiment id to check
   * @return boolean
   */
  public static boolean existsExp(String id) {

    for (Experiment experiment : EXPERIMENT_LIST) {
      if (experiment.getId().equals(id)) {
        return true;
      }
    }

    return false;
  }



}
