package mm.controller.modeling;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlParser {
  
  /**
   * private variables for the Document, DocumentBuilder,
   *  TransformerFactory, Transformer and DOMSource.
   */
  private DocumentBuilder docBuilder;
  private Document doc;
  private TransformerFactory factory = TransformerFactory.newInstance();
  private Transformer transformer;
  private DOMSource source;
    
  /**
    * Constructor, creates a new instance of DocumentBuilderFactory and DocuemtnBuilder.
    * Changes the Namespace- and XIncludeawareness to true.
    * @throws ParserConfigurationException if the documentBuilder can not be instantiated.
    */
  public XmlParser() {
    
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    docFactory.setNamespaceAware(true);
    docFactory.setXIncludeAware(true);
    
    try {
      docBuilder = docFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
    
  /**
   * Parses the committed file. 
   * The method uses the DocumentBuilder.parse(File f) method to save the parsed file in a DOM Tree (http://www.w3.org/DOM/) structure.
   * Variable doc contains the root of the DOM Tree.
   * 
   * @param file contains a String which locates the XML file to be parsed
   * @return true if the file was parsed, else false
   * @throws SAXException or IOException if the committed file can not be found.
   */
  public boolean parseXml(String file) {
    
    boolean parse = true;
    
    try {
      doc = docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      // TODO Auto-generated catch block
      parse = false;
      e.printStackTrace();
    }
    this.source = new DOMSource(this.doc);
    return parse;
  }
  
  /**
   * Parses the given File at directory read and saves the DOM as XML.
   * Uses the classes TransformerFactory and Transformer to build a XML form DOM. 
   * 
   * @param read contains a String which locates the XML file to be parsed
   * @param save contains a File where the XML should be saved
   * @return true, if the file is parsed and saved. Else false
   * @throws TransformerConfigurationException if the transformer Object can not be generated
   * @throws SAXException or IOException if the committed file can not be found
   * @throws TransformerException if the XML file can not be saved
   */
  public boolean parseAndSaveXml(String read, File save) throws TransformerConfigurationException {
    boolean saved = true;  
    
    try {
      doc = docBuilder.parse(read);
    } catch (SAXException | IOException e) {
      // TODO Auto-generated catch block
      saved = false;
      e.printStackTrace();
    }
    this.transformer = this.factory.newTransformer();
    DOMSource source = new DOMSource(this.doc);
    StreamResult result = new StreamResult(save);
      
    try {
      transformer.transform(source, result);
    } catch (TransformerException e) {
      // TODO Auto-generated catch block
      saved = false;
      e.printStackTrace();
    }
      
    return saved;
  }
  
  /**
   * Uses DOM Tree out of Variable doc to build Objects for every Node in Tree. 
   * Builds a new Object every time when identifier "ID" occurs.
   * Objects are from type NodeObjects and include information for every node.
   * Method uses a node list, which contains every node of the DOM Tree.
   * Nodes will be checked for their name and stored in the respective Variable in an Object.
   * 
   * @return HashMap, includes NodeID as Key and NodeObjects with all the Information.
   */
  public HashMap<String, NodeObjects> getNodeObjects() {
    
    NodeList nodeList = doc.getElementsByTagName("*");
    HashMap<String, NodeObjects> map = new HashMap<String, NodeObjects>();
    NodeObjects nodeObjects = new NodeObjects();
    Node node;
    int count = 0;
    @SuppressWarnings("unused")
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
        if (node.getNodeName().equals("components")) {
          nodeObjects.addComponent(new Component(node.getTextContent()));
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
  
  /**
   * Same as getNodeObjects, just saves VMs.
   * This method just saves VM (virtual machine) objects instead of node Objects.
   * The method uses the same equal pattern.
   * 
   * @return HashMap, includes VmID as key and VmObjects with all the information
   */
  public HashMap<String, VmObjects> getVmObjects() {
    
    NodeList nodeList = doc.getElementsByTagName("*");
    HashMap<String, VmObjects> map = new HashMap<String, VmObjects>();
    VmObjects vmObjects = new VmObjects();
    Node node;
    int count = 0;
    @SuppressWarnings("unused")
	boolean first = true;
    
    while (count < nodeList.getLength()) {
      node = nodeList.item(count);
      if (node.getNodeName().equals("vID")) {
        if (first = true) {
          vmObjects.setvId(node.getTextContent());
          first = false;
        } else {
          map.put(vmObjects.getvId(), vmObjects);
          vmObjects = new VmObjects(node.getTextContent());
        }
      } else {
        if (node.getNodeName().equals("vID")) {
          vmObjects.setvId(node.getTextContent());
        }
        if (node.getNodeName().equals("name")) {
          vmObjects.setName(node.getTextContent());
        }
      }
      count++;
    }
    map.put(vmObjects.getvId(), vmObjects);
    
    return map;
  }
  
  /**
   * Builds a String of the DOM Tree. 
   * The returned String is formed like a XML File
   * 
   * @return String, formed like a XML File
   * @throws TransformerException if the Transformer can not be created
   */
  public String getDomString() throws TransformerException {
    this.transformer = this.factory.newTransformer();
    this.transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    
    // create String from xml DOM Tree
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    this.transformer.transform(this.source, result);
    
    return sw.toString();
  }
  
  /**
   * Creates a JSON object from a XML file.
   * Template of this method by https://github.com/douglascrockford/JSON-java.
   * All methods including JSONObject are related to http://www.json.org/java/index.html.
   * 
   * @param path File path to a xml file. File should be converted into a JSON object.
   * @return JSONObject for the xml file at path file.
   * @throws Exception more specific IOException, FileNotFoundException, JSONException
   */
  public JSONObject getJsonString(String path) throws Exception {
    JSONObject fileString = new JSONObject();
    
    try {
      FileReader fr = new FileReader(new File(path));
      BufferedReader br = new BufferedReader(fr);
      
      String currentLine = null;
      String temp = "";
      
      while ((currentLine = br.readLine()) != null) {
        temp += currentLine;
      }
      fr.close();
      
      fileString = XML.toJSONObject(temp);
      
    } catch (JSONException je) {
      System.out.println(je.toString());
    }
    
    return fileString;
  }
}
