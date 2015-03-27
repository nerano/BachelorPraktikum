package mm.controller.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import mm.controller.modeling.Component;
import mm.controller.modeling.Interface;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.WPort;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import mm.controller.modeling.Config;
import mm.controller.modeling.Wire;

public class XmlParser {

    /**
     * private variables for the Document, DocumentBuilder and DOMSource.
     */
    private DocumentBuilder docBuilder;
    private Document        doc;
    @SuppressWarnings("unused")
    private DOMSource       source;

    /**
     * Constructor, creates a new instance of DocumentBuilderFactory and
     * DocuemtnBuilder. Changes the Namespaceawareness and possible
     * XIncludeawareness to true.
     * 
     * @throws ParserConfigurationException
     *             if the documentBuilder can not be instantiated.
     */
    public XmlParser() {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        // docFactory.setXIncludeAware(true);

        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the committed file. The method uses the DocumentBuilder.parse(File
     * f) method to save the parsed file in a DOM Tree (http://www.w3.org/DOM/)
     * structure. Variable doc contains the root of the DOM Tree.
     * 
     * @param file
     *            contains a String which locates the XML file to be parsed
     * @return HashMap including the NodeID as key and an Node Object as the
     *         value
     */
    public HashMap<String, NodeObjects> parseXml(String file) {

        try {
            doc = docBuilder.parse(file);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        this.source = new DOMSource(this.doc);
        return this.getNodeObjects();
    }
    
    
    public Set<WPort> parseWports() {

        HashSet<WPort> portSet = new HashSet<WPort>();
        NodeList nodeList = doc.getElementsByTagName("*");
        Node node = null;
        int count = 0;
        String id = "";
        String port = "";
        String building = "";
        String room = "";
        String trunk = "";
        
        while (count < nodeList.getLength()) {
            node = nodeList.item(count);
            
            if (node.getNodeName().equals("id")) {
                
                if (!id.equals("")) {
                    portSet.add(new WPort(id, port, building, room, trunk));
                }
                id = node.getTextContent();
            }
            
            if (node.getNodeName().equals("port")) {
                port = node.getTextContent();
            }
            
            if (node.getNodeName().equals("trunk")) {
                trunk = node.getTextContent();
            }
            
            if (node.getNodeName().equals("building")) {
                building = node.getTextContent();
            }
            
            if (node.getNodeName().equals("room")) {
                room = node.getTextContent();
            }
            
            count++;
        }
        portSet.add(new WPort(id, port, building, room, trunk));
        return portSet;
    }

    public Set<Config> parseConfigs() {

        NodeList nodeList = doc.getElementsByTagName("*");
        Node node;
        String[] stringArray = null;
        Set<Config> configSet = new HashSet<Config>();
        Set<Wire> wireSet = new HashSet<Wire>();
        Set<String> endpointSet = new HashSet<String>();

        String name = "";

        int count = 0;
        while (count < nodeList.getLength()) {
            node = nodeList.item(count);

            if (node.getNodeName().equals("config")) {

                if (!name.equals("")) {
                    configSet.add(new Config(name, new HashSet<Wire>(wireSet)));
                    wireSet.clear();

                }

                name = ((Element) node).getAttribute("name");
            }

            if (node.getNodeName().equals("wire")) {
                stringArray = node.getTextContent().split("~");

                for (int i = 0; i < stringArray.length; i++) {
                    endpointSet.add(stringArray[i]);
                }
                wireSet.add(new Wire(new HashSet<String>(endpointSet)));
                endpointSet.clear();
            }

            count++;
        }

        configSet.add(new Config(name, new HashSet<Wire>(wireSet)));

        return configSet;
    }

    public String getStartVertex() {

        String startVertex = "";
        NodeList nodeList = doc.getElementsByTagName("*");
        Node node;
        int count = 0;

        while (count < nodeList.getLength()) {
            node = nodeList.item(count);
            if (node.getNodeName().equals("startVertex")) {
                startVertex = node.getTextContent();
                return startVertex;
            }
            count++;
        }
        return null;

    }

    public LinkedList<String> getVertices() {

        LinkedList<String> list = new LinkedList<String>();
        NodeList nodeList = doc.getElementsByTagName("*");
        Node node;
        String vertex = "";
        int count = 0;

        while (count < nodeList.getLength()) {
            node = nodeList.item(count);

            if (node.getNodeName().equals("vertex")) {
                vertex = node.getTextContent();
                list.add(vertex);
            }
            count++;
        }
        return list;
    }

    public LinkedList<String> getEdges() {

        LinkedList<String> list = new LinkedList<String>();
        NodeList nodeList = doc.getElementsByTagName("*");
        Node node;
        String vertex = "";
        int count = 0;

        while (count < nodeList.getLength()) {
            node = nodeList.item(count);

            if (node.getNodeName().equals("edge")) {
                vertex = node.getTextContent();
                list.add(vertex);
            }
            count++;
        }
        return list;
    }

    public HashMap<String, NodeObjects> getNodeObjects2() {
        int times = 0;
        NodeList nodeList = doc.getElementsByTagName("*");
        HashMap<String, NodeObjects> map = new HashMap<String, NodeObjects>();
        Node node;
        int count = 0;
        int ifc = 0;
        boolean first = true;
        NodeObjects nodeobject = new NodeObjects();
        Component comp = null;
        Interface interf = null;

        String interfaceName = "";
        String interfacePort = "";
        String interfaceRole = "";

        while (count < nodeList.getLength()) {
            node = nodeList.item(count);

            if (node.getNodeName().equals("id")) {
                if (first) {
                    nodeobject.setId(node.getTextContent());
                    first = false;
                } else {
                    map.put(nodeobject.getId(), nodeobject);
                    nodeobject = new NodeObjects(node.getTextContent());
                }
            }

            if (node.getNodeName().equals("type")) {
                nodeobject.setNodeType(node.getTextContent());
            }

            if (node.getNodeName().equals("component")) {

                comp = new Component(((Element) node).getAttribute("type"));
                nodeobject.addComponent(comp);
                System.out.println("ELEMENT NODE TEST :" + ((Element) node).getAttribute("type"));

            }

            if (node.getNodeName().equals("trunk")) {
                comp.setTrunk(node.getTextContent());
            }
            
            if (node.getNodeName().equals("powerSource")) {
                comp.setPowerSource(node.getTextContent());
            }

            if (node.getNodeName().equals("name")) {
                interfaceName = node.getTextContent();
                ifc++;
            }

            if (node.getNodeName().equals("port")) {
                interfacePort = node.getTextContent();
                ifc++;
            }

            if (node.getNodeName().equals("role")) {
                interfaceRole = node.getTextContent();
                ifc++;
            }

            if (ifc % 3 == 0 && ifc != 0) {
                times++;
                System.out.println("TIMES ENTERED: " + times);
                System.out.println("IFC: " + ifc);
                interf = new Interface(interfaceName, interfacePort, interfaceRole);
                comp.addInterface(interf);
                interf = null;
                ifc = 0;
            }

            if (node.getNodeName().equals("building")) {
                nodeobject.setBuilding(node.getTextContent());
            }
            if (node.getNodeName().equals("room")) {
                nodeobject.setRoom(node.getTextContent());
            }

            count++;
        }

        map.put(nodeobject.getId(), nodeobject);

        System.out.println("IFC: " + ifc);

        return map;
    }

    /**
     * Uses DOM Tree out of Variable doc to build Objects for every Node in
     * Tree. Builds a new Object every time when identifier "ID" occurs. Objects
     * are from type NodeObjects and include information for every node. Method
     * uses a node list, which contains every node of the DOM Tree. Nodes will
     * be checked for their name and stored in the respective Variable in an
     * Object.
     * 
     * @return HashMap, includes NodeID as Key and NodeObjects with all the
     *         Information.
     */
    public HashMap<String, NodeObjects> getNodeObjects() {

        NodeList nodeList = doc.getElementsByTagName("*");
        HashMap<String, NodeObjects> map = new HashMap<String, NodeObjects>();
        NodeObjects nodeObjects = new NodeObjects();
        Node node;
        Component comp = null;
        int count = 0;
        boolean first = true;

        while (count < nodeList.getLength()) {
            node = nodeList.item(count);
            if (node.getNodeName().equals("id")) {
                if (first) {
                    nodeObjects.setId(node.getTextContent());
                    first = false;
                } else {
                    map.put(nodeObjects.getId(), nodeObjects);
                    nodeObjects = new NodeObjects(node.getTextContent());
                }
            } else {
                if (node.getNodeName().equals("typeName")) {
                    nodeObjects.setNodeType(node.getTextContent());
                }
                if (node.getNodeName().equals("component")) {
                    nodeObjects.addComponent(comp = new Component(node.getTextContent()));
                }
                if (node.getNodeName().equals("powerSource")) {
                    comp.setPowerSource(node.getTextContent());
                }
                if (node.getNodeName().equals("ports")) {
                    comp.setPort(node.getTextContent());
                }
                if (node.getNodeName().equals("latitude")) {
                    nodeObjects.setLatitude(node.getTextContent());
                }
                if (node.getNodeName().equals("longitude")) {
                    nodeObjects.setLongitude(node.getTextContent());
                }
                if (node.getNodeName().equals("building")) {
                    nodeObjects.setBuilding(node.getTextContent());
                }
                if (node.getNodeName().equals("room")) {
                    nodeObjects.setRoom(node.getTextContent());
                }
            }
            count++;
        }
        map.put(nodeObjects.getId(), nodeObjects);

        return map;
    }
}
