package mm.server.parser;

import mm.server.instance.Instances;

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

/**
 * This class parses a XML file of configured instances and saves them into a HashMap.
 * @author Benedikt Bakker
 *
 */
public class XmlParser {

  /**
   * private variables for the Document, DocumentBuilder.
   */
  private DocumentBuilder docBuilder;
  private Document doc;
  private DOMSource source;
    
  /**
    * Constructor, creates a new instance of DocumentBuilderFactory and DocuemtnBuilder.
    * Changes the name space to true.
    * @param file the path of the XML file.
    * @throws ParserConfigurationException if the documentBuilder can not be instantiated.
    */
  public XmlParser(String file) {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    docFactory.setNamespaceAware(true);
    try {
      docBuilder = docFactory.newDocumentBuilder();
      doc = docBuilder.parse(file);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
    this.source = new DOMSource(this.doc);
  }
  
  /**
   * This method parse the XML File and saves the entries for an Instance to its reference.
   * @return a HashMap with every configured Instances.
   */
  public HashMap<String,Instances> parse() {
    NodeList nodeList = doc.getElementsByTagName("*");
    HashMap<String, Instances> map = new HashMap<String, Instances>();
    Instances vm = new Instances();
    Node node;
    int counter = 1;
    String id = "";
    boolean first = true;
    
    while (counter < nodeList.getLength()) {
      node = nodeList.item(counter);
      if (node.getNodeName().equals("id")) {
        if (!first) {
          vm.setList();
          map.put(id, vm);
          vm = new Instances();
        } else {
          first = false;
        }
        id = node.getTextContent();
      } else {
        switch (node.getTextContent()) {
          case
            "true" : vm.setBoolean(node.getNodeName(), true);
            break;
          case
            "false" : vm.setBoolean(node.getNodeName(), false);
            break;
          default: vm.setString(node.getNodeName(), node.getTextContent());
            break;
        }
      }
      counter++;
    }
    vm.setList();
    map.put(id, vm);
    return map;
  }
  
  /**
   * Updates the HashMap of instances.
   * @param file the path of the XML file.
   * @return the updated HashMap of instances.
   */
  public HashMap<String, Instances> update(String file) {
    try {
      doc = docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      e.printStackTrace();
    }
    return this.parse();
  }
}