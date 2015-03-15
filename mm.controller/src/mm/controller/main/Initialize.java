package mm.controller.main;

import java.util.HashMap;
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
   
    /**
    * !-- Initialize everything for the Controller here --!
    */
    public void contextInitialized(ServletContextEvent contextEvent) 
    {   
        System.out.println("Initialising mm.controller.");
        ServletContext context = contextEvent.getServletContext();
        XmlParser parser = new XmlParser();
        
        String nodePath = context.getRealPath("/NODESV2.xml");
        String topologyPath = context.getRealPath("/topology - 2 netgear.xml");
        String configPath = context.getRealPath("/config.xml");
        String wportPath = context.getRealPath("/wports.xml");
       
        //  POWER_TO_COMPONENT = new HashMap<String, Component>();
       
       /* Parsing all Nodes */
       parser.parseXml(nodePath);
       //ControllerData.setAllNodes(parser.getNodeObjects());
       HashMap<String, NodeObjects> allNodes = parser.getNodeObjects2();
       
       /* Parsing all wPorts */
       parser.parseXml(wportPath);
       Set<WPort> portSet = parser.parseWports();
       
       /* Setting up port_to_interface */
       HashMap <String, Interface> interfaceMap = initPortToInterface(allNodes);
       
       /* Parsing Topology */
       parser.parseXml(topologyPath);
       UndirectedGraph<String, DefaultEdge> topology = 
             initTopology(parser.getVertices(), parser.getEdges());
       String startVertex = parser.getStartVertex();
       
       /* Parsing Configs */
       parser.parseXml(configPath);
       Set<Config> configSet = parser.parseConfigs();
       
       new ControllerData(allNodes, interfaceMap, topology, startVertex, configSet, portSet);
       
       addExpExample();
      
       System.out.println("Finished initialising mm.controller.");
    
       
       System.out.println(ControllerData.getPath("NetGear2;1"));
       
       System.out.println(ControllerData.getAllNodes());
    
    }

    
    public void contextDestroyed(ServletContextEvent arg0) 
    {
             
    }//end constextDestroyed method

    
    public static void addExpExample(){
        
        exp1();
        
        
    }
    
    
    private static UndirectedGraph<String, DefaultEdge> initTopology
                    (LinkedList<String> vertices, LinkedList<String> edges) {
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
        
        initImplicitEdges(graph, vertices);
        System.out.println("Finished Initialising Topology");
        System.out.println("Topology : " + graph.toString());
        return graph;
        
    }
    
    private static void initImplicitEdges(UndirectedGraph<String, DefaultEdge> graph,
                            LinkedList<String> vertices) {
        
        System.out.println("Initialising implicit edges");
        String[] vertex1Array;
        String[] vertex2Array;
        String v1;
        String v2;
        String v1c;
        String v2c;
        
        
        for (String vertex : vertices) {
            
            vertex1Array = vertex.split(";");
            v1 = vertex1Array[0];
            
            for (String vertex2 : vertices) {
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
        
      
        Experiment exp = new Experiment("EXPERIMENT123");
        exp.addNode(ControllerData.getNodeById("Node A"));
        exp.addNode(ControllerData.getNodeById("Node B"));
        
        VLan vlan1 = new VLan(125);
        VLan vlan2 = new VLan(124);
        
        exp.setUser("testUser");
        
       exp.addVLan(vlan1);
       exp.addVLan(vlan2);
        
        exp.setStatus("stopped");
        
        
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
