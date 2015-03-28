package mm.controller.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mm.controller.modeling.Component;
import mm.controller.modeling.Config;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.Interface;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.WPort;
import mm.controller.parser.XmlParser;

public class Initialize implements ServletContextListener
{

    private static String  NODE_PATH;
    private static String  TOPOLOGY_PATH;
    private static String  CONFIG_PATH;
    private static String  WPORT_PATH;
    private static String  BASE_PATH;
    private static String  STARTUP_PATH;
    private static String  RELOAD_FILE;
    private static boolean RELOAD_ON_STARTUP = true;

    /**
     * !-- Initialize everything for the Controller here --!
     */
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        System.out.println("Initialising mm.controller.");
        ServletContext context = contextEvent.getServletContext();
        XmlParser parser = new XmlParser();

        BASE_PATH = context.getRealPath("/");
        NODE_PATH = context.getRealPath("/NODESV2.xml");
        TOPOLOGY_PATH = context.getRealPath("/topology - 2 netgear.xml");
        CONFIG_PATH = context.getRealPath("/config.xml");
        WPORT_PATH = context.getRealPath("/wports.xml");
        STARTUP_PATH = context.getRealPath("/reloadOnStartup.xml");
        // POWER_TO_COMPONENT = new HashMap<String, Component>();

        /* Parsing Startup Info */
        parser.parseXml(STARTUP_PATH);
        HashMap<String, String> reloadMap = parser.parseStartupInfo();
        System.out.println(reloadMap);
        setStartupInfo(reloadMap);

        /* Parsing all Nodes */
        parser.parseXml(NODE_PATH);
        HashMap<String, NodeObjects> allNodes = parser.getNodeObjects2();

        /* Parsing all wPorts */
        parser.parseXml(WPORT_PATH);
        Set<WPort> portSet = parser.parseWports();

        /* Setting up port_to_interface */
        HashMap<String, Interface> interfaceMap = initPortToInterface(allNodes);

        /* Parsing Topology */
        parser.parseXml(TOPOLOGY_PATH);
        UndirectedGraph<String, DefaultEdge> topology =
                initTopology(parser.getVertices(), parser.getEdges(), allNodes, portSet);
        String startVertex = parser.getStartVertex();

        /* Parsing Configs */
        parser.parseXml(CONFIG_PATH);
        Set<Config> configSet = parser.parseConfigs();

        new ControllerData(allNodes, interfaceMap, topology, startVertex, configSet, portSet);

        if (RELOAD_ON_STARTUP) {
            reloadOnStartup(RELOAD_FILE);
        }

        System.out.println("Finished initialising mm.controller.");

        System.out.println(ControllerData.getPath("NetGear2;1"));

        System.out.println(ControllerData.getAllNodes());

        System.out.println(ControllerData.getPath("NetGear2;3", "NetGear2;6"));

    }

    public void contextDestroyed(ServletContextEvent arg0)
    {

    }// end constextDestroyed method

    private static void setStartupInfo(HashMap<String, String> map) {

        if (map == null) {
            System.out.println("MAP IS NULL");
        }

        if (map.get("reloadOnStartup").equals("0")) {
            RELOAD_ON_STARTUP = false;
        } else {
            RELOAD_ON_STARTUP = true;

            if (map.get("reloadFile") != "") {
                RELOAD_FILE = map.get("reloadFile");
            } else {
                RELOAD_FILE = "expList";
            }
        }
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

    /**
     * Reloads the nodes.xml file and calculates all applicable configs again
     * for all nodes in ALL_NODES
     * 
     * @return boolean if the reload was successful or not
     */
    public static boolean reloadAllNodes() {
        try {
            XmlParser parser = new XmlParser();
            parser.parseXml(NODE_PATH);
            ControllerData.setAllNodes(parser.getNodeObjects2());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        ControllerData.calcConfigsForAllNodes();
        return true;
    }

    public static boolean reloadTopology() {

        try {
            XmlParser parser = new XmlParser();

            parser.parseXml(NODE_PATH);
            HashMap<String, NodeObjects> allNodes = parser.getNodeObjects2();

            parser.parseXml(WPORT_PATH);
            Set<WPort> portSet = parser.parseWports();

            parser.parseXml(TOPOLOGY_PATH);

            UndirectedGraph<String, DefaultEdge> topology =
                    initTopology(parser.getVertices(), parser.getEdges(), allNodes, portSet);
            String startVertex = parser.getStartVertex();

            ControllerData.setTopology(topology, startVertex);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean reloadAllwPorts() {
        try {
            XmlParser parser = new XmlParser();
            parser.parseXml(WPORT_PATH);
            ControllerData.setwPorts(parser.parseWports());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        reloadTopology();
        return true;
    }

    public static boolean reloadOnStartup(String fileName) {

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            System.out.println("Path to read " + BASE_PATH + fileName);
            byte[] encoded = Files.readAllBytes(Paths.get(BASE_PATH + fileName));
            String expListAsString = new String(encoded, StandardCharsets.UTF_8);

            Type type = new TypeToken<LinkedList<Experiment>>() {
            }.getType();

            LinkedList<Experiment> expList = gson.fromJson(expListAsString, type);

            ControllerData.setAllExperiments(expList);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveToDisk() {
        PrintWriter pw = null;
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String saveString = gson.toJson(ControllerData.getAllExp());
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
            
            System.out.println(timeStamp);
            System.out.println("SAVE PATH: " + BASE_PATH + timeStamp + "expList");

            pw = new PrintWriter(BASE_PATH + timeStamp + "expList");
            pw.println(saveString);
            pw.close();
           
            pw = new PrintWriter(BASE_PATH + "expList");
            pw.println(saveString);
            pw.close();
           

        } catch (FileNotFoundException e) {
            pw.close();
            e.printStackTrace();
            return false;
        }
        return true;

    }

    private static UndirectedGraph<String, DefaultEdge> initTopology
            (LinkedList<String> vertices, LinkedList<String> edges,
                    HashMap<String, NodeObjects> allNodes,
                    Set<WPort> wports) {
        System.out.println("Initialising Topology");
        String[] edge;
        UndirectedGraph<String, DefaultEdge> graph =
                new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        for (String vertex : vertices) {
            graph.addVertex(vertex);
        }

        for (String e : edges) {
            edge = e.split("~");
            String v1 = vertices.get(vertices.indexOf(edge[0]));
            String v2 = vertices.get(vertices.indexOf(edge[1]));
            graph.addEdge(v1, v2);
        }

        initImplicitEdges(graph, vertices, allNodes, wports);
        System.out.println("Finished Initialising Topology");
        System.out.println("Topology : " + graph.toString());
        return graph;

    }

    private static void initImplicitEdges(UndirectedGraph<String, DefaultEdge> graph,
            LinkedList<String> vertices,
            HashMap<String, NodeObjects> allNodes,
            Set<WPort> wports) {

        Set<String> allVertices = new HashSet<String>();
        allVertices.addAll(vertices);

        // Add SwitchPorts from Nodes
        for (NodeObjects node : allNodes.values()) {
            // Eventually add Trunk from Node
            for (Component component : node.getComponents()) {
                allVertices.add(component.getTrunkport());
                graph.addVertex(component.getTrunkport());
                for (Interface inf : component.getInterfaces()) {
                    allVertices.add(inf.getSwitchport());
                    graph.addVertex(inf.getSwitchport());
                }
            }
        }

        // Adding all SwitchPorts and Trunkports from the wPorts to the Topology
        for (WPort wport : wports) {
            allVertices.add(wport.getTrunk());
            allVertices.add(wport.getPort());

            graph.addVertex(wport.getTrunk());
            graph.addVertex(wport.getPort());
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
                System.out.println("VERTEX1 " + v1);
                System.out.println("VERTEX2 " + v2);
                if (v1.equals(v2) && !(vertex1Array[1].equals(vertex2Array[1]))) {

                    v1c = v1 + ";" + vertex1Array[1];
                    v2c = v2 + ";" + vertex2Array[1];

                    if (graph.addEdge(v1c, v2c) != null) {
                        System.out.println("Added implicit Edge: " + v1c + " - " + v2c);
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
}