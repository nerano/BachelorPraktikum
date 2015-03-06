package mm.controller.parser;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import mm.controller.modeling.Component;
import mm.controller.modeling.NodeObjects;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser {

  /**
   * private variables for the Document, DocumentBuilder,
   *  TransformerFactory, Transformer and DOMSource.
   */
  private DocumentBuilder docBuilder;
  private Document doc;
  @SuppressWarnings("unused")
  private DOMSource source;

  /**
   * Constructor, creates a new instance of DocumentBuilderFactory and
   * DocuemtnBuilder. Changes the Namespace- and XIncludeawareness to true.
   * 
   * @throws ParserConfigurationException
   *           if the documentBuilder can not be instantiated.
   */
  public XmlParser() {

    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    docFactory.setNamespaceAware(true);
    //docFactory.setXIncludeAware(true);

    try {
      docBuilder = docFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Parses the committed file. The method uses the DocumentBuilder.parse(File
   * f) method to save the parsed file in a DOM Tree (http://www.w3.org/DOM/)
   * structure. Variable doc contains the root of the DOM Tree.
   * 
   * @param file
   *          contains a String which locates the XML file to be parsed
   * @return true if the file was parsed, else false
   * @throws SAXException
   *           or IOException if the committed file can not be found.
   */
  public HashMap<String, NodeObjects> parseXml(String file) {

    try {
      doc = docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.source = new DOMSource(this.doc);
    return this.getNodeObjects();
  }

  /**
   * Uses DOM Tree out of Variable doc to build Objects for every Node in Tree.
   * Builds a new Object every time when identifier "ID" occurs. Objects are
   * from type NodeObjects and include information for every node. Method uses a
   * node list, which contains every node of the DOM Tree. Nodes will be checked
   * for their name and stored in the respective Variable in an Object.
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
