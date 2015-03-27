package mm.power.parser;

import mm.power.implementation.AEHome;
import mm.power.modeling.PowerSupply;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

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
    * Constructor, creates a new instance of DocumentBuilderFactory and DocuemtnBuilder.
    * Changes the Namespace- and XIncludeawareness to true.
    */
  public XmlParser() {
    
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    docFactory.setNamespaceAware(true);
    docFactory.setXIncludeAware(true);
    
    try {
      docBuilder = docFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
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
   */
  public boolean parseXml(String file) {
    
    boolean parse = true;
    
    try {
      doc = docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      parse = false;
      e.printStackTrace();
    }
    this.source = new DOMSource(this.doc);
    return parse;
  }
  
  /**
   * This Method.
   * @return
   */
  public HashMap<String, PowerSupply> getPowerSupply() {
    NodeList nodeList = doc.getElementsByTagName("*");
    HashMap<String, PowerSupply> map = new HashMap<String, PowerSupply>();
    Node node;
    String id = ""; 
    String type = ""; 
    String host = "";
    int counter = 0;
    
    while (counter < nodeList.getLength()) {
      node = nodeList.item(counter);
      if (node.getNodeName().equals("id")) {
        switch (type) {
          case "AeHome": map.put(id, new AEHome(id, type, host)); break;
          //case "SteckdosePro": map.put(id, new ); break;
          default: break;
        }
        id = node.getTextContent();
      } else {
        if (node.getNodeName().equals("type")) {
          type = node.getTextContent();
        }
        if (node.getNodeName().equals("host")) {
          host = node.getTextContent();
        }
      }
      counter++;
    }
    
    switch (type) {
      case "AeHome": map.put(id, new AEHome(id, type, host)); break;
      //case "SteckdosenPro": map.put(id, new ); break;
      default: break;
    }
    return map;
  }
}
