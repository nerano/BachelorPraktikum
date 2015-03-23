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
  private String file;
    
  /**
    * Constructor, creates a new instance of DocumentBuilderFactory and DocuemtnBuilder.
    * Changes the name space to true.
    * @param path the path of the XML file.
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
   * @return a HashMap with every configured Instances.
   */
  public HashMap<String,Template> parse() {
    NodeList nodeList = doc.getElementsByTagName("*");
    HashMap<String, Template> map = new HashMap<String, Template>();
    Template vm = new Template();
    Node node;
    int counter = 1;
    String id = "";
    boolean first = true;
    
    while (counter < nodeList.getLength()) {
      node = nodeList.item(counter);
      if (node.getNodeName().equals("id")) {
        if (!first) {
          map.put(id, vm);
          vm = new Template();
          vm.setId(id);
        } else {
          first = false;
        }
        id = node.getTextContent();
        vm.setId(id);
      } else {
        switch (node.getNodeName()) {
          case 
            "disk_template": vm.setDisk_template(node.getTextContent());
            break;
          case
            "file_driver": vm.setFile_driver(node.getTextContent());
            break;
          case
            "file_storage_dir": vm.setFile_storage_dir(node.getTextContent());
            break;
          case
            "hypervisor": vm.setHypervisor(node.getTextContent());
            break;
          case
            "iallocator": vm.setIallocator(node.getTextContent());
            break;
          case
            "instance_name": vm.setInstance_name(node.getTextContent());
            break;
          case
            "mode": vm.setMode(node.getTextContent());
            break;
          case
            "os_type": vm.setOs_type(node.getTextContent());
            break;
          case
            "pnode": vm.setPnode(node.getTextContent());
            break;
          case
            "pnode_uuid": vm.setPnode_uuid(node.getTextContent());
            break;  
          case
            "source_instance_name": vm.setSource_instance_name(node.getTextContent());
            break;  
          case
            "source_x509_ca": vm.setSource_x509_ca(node.getTextContent());
            break;
          case
            "src_node": vm.setSrc_node_uuid(node.getTextContent());
            break;
          case
            "src_node_uuid": vm.setSrc_node_uuid(node.getTextContent());
            break;
          case
            "src_path": vm.setSrc_path(node.getTextContent());
            break;
          case
            "__version__": vm.set__version__(Integer.parseInt(node.getTextContent()));
            break;
          case
            "source_shutdown_timeout": vm.setSource_shutdown_timeout(Integer.parseInt(
                                           node.getTextContent()));
            break;
          case
            "nic_mode": vm.setNicMode(node.getTextContent());
            break;
          case
            "nic_link": vm.setNicLink(node.getTextContent());
            break;
          case
            "nic_ip": vm.setNicIp(node.getTextContent());
            break;
          case
            "nic_mac": vm.setNicMac(node.getTextContent());
            break;
          case
            "nic_name": vm.setNicName(node.getTextContent());
            break;
          case
            "nic_network": vm.setNicNetwork(node.getTextContent());
            break;
          case
            "disks_mode": vm.setDisksMode(node.getTextContent());
            break;
          case
            "disks_size": vm.setDisksSize(Integer.parseInt(node.getTextContent()));
            break;
          default: break;
        }
        switch (node.getTextContent()) {
          case
            "false": vm.setBoolean(node.getNodeName(), false);
            break;
          case
            "true": vm.setBoolean(node.getNodeName(), true);
            break;
          default: break;
        }
      }
      counter++;
    }
    map.put(id, vm);
    return map;
  }
  
  /**
   * Updates the HashMap of templates by building a new document instance and parse this file.
   * @return the updated HashMap of templates.
   */
  public HashMap<String, Template> update() {
    this.buildDoc();
    return this.parse();
  }
}