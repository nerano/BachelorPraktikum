package mm.controller.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import mm.controller.modeling.Component;
import mm.controller.modeling.Config;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.Interface;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.VLan;
import mm.controller.modeling.WPort;
import mm.controller.parser.XmlParser;



public class Initialize implements ServletContextListener
    {
   
    static String NODE_PATH;
    static String TOPOLOGY_PATH;
    static String CONFIG_PATH;
    static String WPORT_PATH;
  
  
  
    /**
    * !-- Initialize everything for the Controller here --!
    */
    public void contextInitialized(ServletContextEvent contextEvent) 
    {   
        System.out.println("Initialising mm.controller.");
        ServletContext context = contextEvent.getServletContext();
        XmlParser parser = new XmlParser();
        
        NODE_PATH = context.getRealPath("/NODESV2.xml");
        TOPOLOGY_PATH = context.getRealPath("/topology - 2 netgear.xml");
        CONFIG_PATH = context.getRealPath("/config.xml");
        WPORT_PATH = context.getRealPath("/wports.xml");
       
        //  POWER_TO_COMPONENT = new HashMap<String, Component>();
       
       /* Parsing all Nodes */
       parser.parseXml(NODE_PATH);
       HashMap<String, NodeObjects> allNodes = parser.getNodeObjects2();
       
       /* Parsing all wPorts */
       parser.parseXml(WPORT_PATH);
       Set<WPort> portSet = parser.parseWports();
       
       /* Setting up port_to_interface */
       HashMap <String, Interface> interfaceMap = initPortToInterface(allNodes);
       
       /* Parsing Topology */
       parser.parseXml(TOPOLOGY_PATH);
       UndirectedGraph<String, DefaultEdge> topology = 
             initTopology(parser.getVertices(), parser.getEdges(), allNodes);
       String startVertex = parser.getStartVertex();
       
       /* Parsing Configs */
       parser.parseXml(CONFIG_PATH);
       Set<Config> configSet = parser.parseConfigs();
       
       new ControllerData(allNodes, interfaceMap, topology, startVertex, configSet, portSet);
       
       addExpExample();
      
       System.out.println("Finished initialising mm.controller.");
    
       
       System.out.println(ControllerData.getPath("NetGear2;1"));
       
       System.out.println(ControllerData.getAllNodes());
       
       
       System.out.println(ControllerData.getPath("NetGear2;3", "NetGear2;6"));
       
       
    
    }

    
    public void contextDestroyed(ServletContextEvent arg0) 
    {
             
    }//end constextDestroyed method

    
    public static void addExpExample(){
        
        exp1();
        
        
    }
    
    public static boolean reloadConfigs() {
      try {
      XmlParser parser = new XmlParser();
      parser.parseXml(CONFIG_PATH);
      ControllerData.setConfigs(parser.parseConfigs()); 
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      return true;
    }
    
    public static boolean reloadAllNodes() {
       try{
        XmlParser parser = new XmlParser();
        parser.parseXml(NODE_PATH);
        ControllerData.setAllNodes(parser.getNodeObjects2()); 
       } catch(Exception e) {
           e.printStackTrace();
           return false;
       }
        return true;
    }
    
    public static boolean reloadTopology() {
        
        try{
         XmlParser parser = new XmlParser();
         
         parser.parseXml(NODE_PATH);
         HashMap<String, NodeObjects> allNodes = parser.getNodeObjects2();
         
         parser.parseXml(TOPOLOGY_PATH);
         
         UndirectedGraph<String, DefaultEdge> topology = 
         initTopology(parser.getVertices(), parser.getEdges(), allNodes);
         String startVertex = parser.getStartVertex();

           ControllerData.setTopology(topology, startVertex);
           
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
         return true;
     }
    
    public static boolean reloadAllwPorts() {
        try{
         XmlParser parser = new XmlParser();
         parser.parseXml(WPORT_PATH);
         ControllerData.setwPorts(parser.parseWports());
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
         return true;
     }
    
    private static UndirectedGraph<String, DefaultEdge> initTopology
                    (LinkedList<String> vertices, LinkedList<String> edges,
                            HashMap<String, NodeObjects> allNodes) {
        System.out.println("Initialising Topology");
        String[] edge;
        UndirectedGraph<String, DefaultEdge> graph = 
                new SimpleGraph<String, DefaultEdge> (DefaultEdge.class);
        
        for (String vertex : vertices) {
            graph.addVertex(vertex);
        }
        
        for (String e : edges) {
            edge = e.split("~");
            String v1 = vertices.get(vertices.indexOf(edge[0]));
            String v2 = vertices.get(vertices.indexOf(edge[1]));
            graph.addEdge(v1, v2);
        }
        
        initImplicitEdges(graph, vertices, allNodes);
        System.out.println("Finished Initialising Topology");
        System.out.println("Topology : " + graph.toString());
        return graph;
        
    }
    
    private static void initImplicitEdges(UndirectedGraph<String, DefaultEdge> graph,
                            LinkedList<String> vertices,
                            HashMap<String, NodeObjects> allNodes) {
        
        Set<String> allVertices = new HashSet<String>();
        allVertices.addAll(vertices);
        
        // Add SwitchPorts from Nodes and wPorts to list of all vertices
        // TODO SwitchPorts from wPorts
        for (NodeObjects node : allNodes.values()) {
            // Eventually add Trunk from Node
            for (Component component : node.getComponents()) {
                for (Interface inf : component.getInterfaces()) {
                    allVertices.add(inf.getSwitchport());
                    graph.addVertex(inf.getSwitchport());
                }
            }
        }
        
        System.out.println("Initialising implicit edges");
        String[] vertex1Array;
        String[] vertex2Array;
        String v1;
        String v2;
        String v1c;
        String v2c;
        
        
        for (String vertex : allVertices) {
            
            vertex1Array = vertex.split(";");
            v1 = vertex1Array[0];
            
            for (String vertex2 : allVertices) {
                vertex2Array = vertex2.split(";");
                v2 = vertex2Array[0];
                if(v1.equals(v2) && !(vertex1Array[1].equals(vertex2Array[1]))) {
                   
                    v1c = v1 + ";"+ vertex1Array[1];
                    v2c = v2 + ";"+ vertex2Array[1];
                   
                    if(graph.addEdge(v1c, v2c) != null) {
                        System.out.println("Added implicit Edge: " + v1c + " - "  + v2c);
                    }
                }
            }
        }
    }
    
    
    private static HashMap<String, Interface> initPortToInterface(HashMap<String, NodeObjects> nodes) {
        
        LinkedList<NodeObjects> allNodes = new LinkedList<NodeObjects>(nodes.values());
        
        HashMap<String, Interface> map = new HashMap<String, Interface>();
        String switchport;
        LinkedList<Interface> ifList;
        LinkedList<Component> componentList;
        
        for (NodeObjects nodeObject : allNodes) {
           componentList = nodeObject.getComponents();
            for (Component component : componentList) {
                ifList = component.getInterfaces();
                for (Interface inf : ifList) {
                    switchport = inf.getSwitchport();
                    map.put(switchport, inf);
                }
            }
        }
        
        return map;
    }
    
    public static void exp1(){
        
      /**Experiment exp = new Experiment("EXPERIMENT1");
      Experiment exp2 = new Experiment("EXPERIMENT2");
      Experiment exp3 = new Experiment("EXPERIMENT3");
      Experiment exp4 = new Experiment("EXPERIMENT4");
      Experiment exp5 = new Experiment("EXPERIMENT5");
      exp.addNode(ControllerData.getNodeById("Node A"), new Config());
      exp.addNode(ControllerData.getNodeById("Node B"));
      exp2.addNode(ControllerData.getNodeById("Node A"));
      exp2.addNode(ControllerData.getNodeById("Node B"));
      exp3.addNode(ControllerData.getNodeById("Node B"));
      
      for (int i = 0; i < 1000; i++) {
 exp2.addNode(ControllerData.getNodeById("Node A"));
}
      
      VLan vlan1 = new VLan(125);
      VLan vlan2 = new VLan(124);
      
      exp.addVLan(vlan1);
      exp.addVLan(vlan2);
      exp2.addVLan(vlan2);
      exp3.addVLan(vlan2);
      exp4.addVLan(vlan1);
      exp5.addVLan(vlan1);
      
      exp.setStatus("paused");
      exp2.setStatus("running");
      exp3.setStatus("stopped");
      exp4.setStatus("error");
      exp5.setStatus("asdf");
      
      ControllerData.addExp(exp);
      ControllerData.addExp(exp2);
      ControllerData.addExp(exp3);
      ControllerData.addExp(exp4);
      ControllerData.addExp(exp5);**/
      
    /**    String porta1 = "NetComponentA.1";
 	   String porta2 = "NetComponentA.2";
 	   
 	   String portf7 = "NetzKomponenteF.7";
 	   String portf8 = "NetzKomponenteF.8";
    	
       String powerSource1 = "AeHome1;1";
       String powerSource2 = "AeHome1;2";
       String powerSource3 = "AeHome1;3";
 	   
    	Component c1 = new Component("WARP");
        Component c2 = new Component("APU");
        
        Component c3 = new Component("Komponente X");
        Component c4 = new Component("Komponente Z");
        
       /**  c1.setvLanId(0);
        c2.setvLanId(0);
        
        c3.setvLanId(0);
        c4.setvLanId(0); **/
     /**   
        c1.setPowerSource(powerSource1);
        c2.setPowerSource(powerSource2);
        c3.setPowerSource(powerSource3);
        c4.setPowerSource(powerSource1);
        
        c1.setPort(porta1);
        c2.setPort(porta2);
        c3.setPort(portf7);
        c4.setPort(portf8);
        
        NodeObjects node1 = new NodeObjects("Knoten A");
        NodeObjects node2 = new NodeObjects("Knoten B");
        
        
        node1.addComponent(c1);
        node1.addComponent(c2);
        
        node2.addComponent(c3);
        node2.addComponent(c4);
        
        node1.setBuilding("Haus 1");
        node2.setBuilding("Haus 2");
        
        node1.setRoom("Raum Rechts");
        node2.setRoom("Raum Links"); **/
        
        NodeObjects node1 = ControllerData.getNodeById("Node A");
        //NodeObjects node2 = ControllerData.getNodeById("Node B");
        
        LinkedList<NodeObjects> nodeList = new  LinkedList<NodeObjects>();
        
        nodeList.add(node1);
       // nodeList.add(node2);
        
        Experiment exp = new Experiment("EXPERIMENT123", "testUser");
        
        exp.setNodeList(nodeList);
        
        VLan vlan1 = new VLan(125);
        VLan vlan2 = new VLan(124);
        
        
       exp.addVLan(vlan1);
       exp.addVLan(vlan2);
        
        exp.setStatus("paused");
        
        
        ControllerData.addExp(exp);
        
        

      /**  ControllerData.addPort(porta1, c1);
       ControllerData.addPort(porta2, c2);
       ControllerData.addPort(portf7, c3);
       ControllerData.addPort(portf8, c4);
     **/
 /** 	   ControllerData.addNode(node1);
 	   ControllerData.addNode(node2); **/
    
    }   
 }