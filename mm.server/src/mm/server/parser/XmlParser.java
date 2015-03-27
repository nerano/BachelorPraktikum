package mm.server.parser;

import mm.server.instance.Template;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class parses a XML file of configured instances and saves them into a
 * HashMap.
 * 
 * @author Benedikt Bakker
 *
 */
public class XmlParser {

  /**
   * private variables for the Document, DocumentBuilder.
   */
  private DocumentBuilder docBuilder;
  private Document doc;
  private String file;

  /**
   * Constructor, creates a new instance of DocumentBuilderFactory and
   * DocuemtnBuilder. Changes the name space to true.
   * 
   * @param path
   *          the path of the XML file
   */
  public XmlParser(String path) {
    this.file = path;
    this.buildDoc();
    this.parse();
  }

  /**
   * Builds the document instance for parsing the XML file.
   */
  private void buildDoc() {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    docFactory.setNamespaceAware(true);
    try {
      docBuilder = docFactory.newDocumentBuilder();
      doc = docBuilder.parse(file);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Parse the XML File and saves the entries for an Instance to its reference.
   * 
   * @return a HashMap with every configured Instances
   */
  public HashMap<String, Template> parse() {
    NodeList nodeList = doc.getElementsByTagName("*");
    HashMap<String, Template> map = new HashMap<String, Template>();
    Template template = new Template();
    Node node;
    int counter = 1;
    String id = "";
    boolean first = true;

    while (counter < nodeList.getLength()) {
      node = nodeList.item(counter);
      if (node.getNodeName().equals("id")) {
        if (!first) {
          template.setLists();
          map.put(id, template);
          template = new Template();
          template.setAttribute("id", id);
        } else {
          first = false;
        }
        id = node.getTextContent();
        template.setAttribute("id", id);
      } else {
        if (node.getTextContent().equals("false")) {
          template.setBoolean(node.getNodeName(), false);
        }
        if (node.getTextContent().equals("true")) {
          template.setBoolean(node.getNodeName(), true);
        } else {
          switch (node.getNodeName()) {
            case "__version__":
              template.setVersion(Integer.parseInt(node.getTextContent()));
              break;
            case "source_shutdown_timeout":
              template.setAttribute("source_shutdown_timeout",
                  Integer.parseInt(node.getTextContent()));
              break;
            case "nic_mode":
              template.setNicMode(node.getTextContent());
              break;
            case "nic_name":
              template.setNicName(node.getTextContent());
              break;
            case "nic_ip":
              template.setNicIp(node.getTextContent());
              break;
            case "nic_mac":
              template.setNicMac(node.getTextContent());
              break;
            case "nic_link":
              template.setNicLink(node.getTextContent());
              break;
            case "nic_network":
              template.setNicNetwork(node.getTextContent());
              break;
            case "disks_mode":
              template.setDisksMode(node.getTextContent());
              break;
            case "disks_size":
              template.setDisksSize(Integer.parseInt(node.getTextContent()));
              break;
            case "tags":
              template.setTags(node.getTextContent());
              break;
            default:
              template.setAttribute(node.getNodeName(), node.getTextContent());
              break;
          }
        }
      }
      counter++;
    }
    template.setLists();
    map.put(id, template);
    return map;
  }

  /**
   * Updates the HashMap of templates by building a new document instance and
   * parse this file.
   * 
   * @return the updated HashMap of templates
   */
  public HashMap<String, Template> update() {
    this.buildDoc();
    return this.parse();
  }
}