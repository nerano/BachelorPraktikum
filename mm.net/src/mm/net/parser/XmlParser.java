package mm.net.parser;

import mm.net.implementation.NetComponentFac;
import mm.net.implementation.NetGearGS108Tv2;
import mm.net.modeling.NetComponent;
import mm.net.modeling.StaticComponent;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

public class XmlParser {

  /**
   * private variables for the Document, DocumentBuilder, TransformerFactory,
   * Transformer and DOMSource.
   */
  private DocumentBuilder    docBuilder;
  private Document           doc;
  @SuppressWarnings("unused")
  private TransformerFactory factory = TransformerFactory.newInstance();
  @SuppressWarnings("unused")
  private Transformer        transformer;
  @SuppressWarnings("unused")
  private DOMSource          source;

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
    docFactory.setXIncludeAware(true);

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
   *          contains a String which locates the XML file to be parsed
   * @return true if the file was parsed, else false
   * @throws SAXException
   *           or IOException if the committed file can not be found.
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

  public LinkedList<StaticComponent> getStaticComponents() {

    LinkedList<StaticComponent> scList = new LinkedList<StaticComponent>();

    String id = "";
    String port = "";
    String type = "";

    Node node;
    NodeList nodeList = doc.getElementsByTagName("*");
    int counter = 0;

    while (counter < nodeList.getLength()) {

      node = nodeList.item(counter);

      if (node.getNodeName().equals("id")) {

        if (!type.equals("")) {
          scList.add(new StaticComponent(id, port, type));
        }
        // System.out.println("Port: " + port);
        // System.out.println("ID: " + id);
        id = node.getTextContent();
      } else {
        if (node.getNodeName().equals("port")) {
          port = node.getTextContent();
        }
        if (node.getNodeName().equals("type")) {
          type = node.getTextContent();
        }
      }

      counter++;
    }

    scList.add(new StaticComponent(id, port, type));

    return scList;
  }

  public int[] getVLanInfo() {

    Node node;
    int[] vlanInfo = new int[6];
    NodeList nodeList = doc.getElementsByTagName("*");
    int counter = 0;

    while (counter < nodeList.getLength()) {

      node = nodeList.item(counter);

      if (node.getNodeName().equals("globalRangeMin")) {
        System.out.println("Global Range Minimun: " + node.getTextContent());
        vlanInfo[0] = Integer.parseInt(node.getTextContent());
      }
      if (node.getNodeName().equals("globalRangeMax")) {
        System.out.println("Global Range Maximum: " + node.getTextContent());
        vlanInfo[1] = Integer.parseInt(node.getTextContent());
      }

      if (node.getNodeName().equals("localRangeMin")) {
        System.out.println("Local Range Minimun: " + node.getTextContent());
        vlanInfo[2] = Integer.parseInt(node.getTextContent());
      }

      if (node.getNodeName().equals("localRangeMax")) {
        System.out.println("Local Range Maximum: " + node.getTextContent());
        vlanInfo[3] = Integer.parseInt(node.getTextContent());
      }

      if (node.getNodeName().equals("power")) {
        System.out.println("Power VLan: " + node.getTextContent());
        vlanInfo[4] = Integer.parseInt(node.getTextContent());
      }

      if (node.getNodeName().equals("manage")) {
        System.out.println("Management VLan: " + node.getTextContent());
        vlanInfo[5] = Integer.parseInt(node.getTextContent());

      }
      counter++;
    }

    return vlanInfo;
  }

  /**
   * 
   * @return
   */
  public HashMap<String, NetComponent> getNetComponents() {
    NodeList nodeList = doc.getElementsByTagName("*");
    HashMap<String, NetComponent> map = new HashMap<String, NetComponent>();
    Node node;
    String id = "";
    String type = "";
    String host = "";
    String[] trunkArray;
    LinkedList<Integer> trunks = null;
    int counter = 0;
    boolean first = true;
    while (counter < nodeList.getLength()) {
      node = nodeList.item(counter);
      if (node.getNodeName().equals("id")) {
        if(first) {
            id = node.getTextContent(); 
            first = false;
        } else {
              map.put(id, NetComponentFac.createNetComponent(type, id, host, trunks));
              id = node.getTextContent();
          }
      } else {

        if (node.getNodeName().equals("trunk")) {
          System.out.println("TRUNKS");
          trunkArray = node.getTextContent().split(";");
          trunks = new LinkedList<Integer>();
          for (int i = 0; i < trunkArray.length; i++) {
            System.out.println(trunkArray[i]);
            trunks.add(Integer.parseInt(trunkArray[i]));
          }
        }

        if (node.getNodeName().equals("type")) {
          type = node.getTextContent();
        }
        if (node.getNodeName().equals("host")) {
          host = node.getTextContent();
        }
      }
      counter++;
    }

    map.put(id, NetComponentFac.createNetComponent(type, id, host, trunks));
    return map;
  }
}
