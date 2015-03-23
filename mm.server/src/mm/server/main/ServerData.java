package mm.server.main;

import mm.server.instance.Template;
import mm.server.parser.XmlParser;

import java.util.HashMap;

public class ServerData {

  private static HashMap<String, Template> SERVER_MAP;
  private static XmlParser PARSER;

  /**
   * Initialize the HashMap of instances.
   * @param pars parser for parsing the HashMap of instances.
   */
  public ServerData(XmlParser pars) {
    PARSER = pars;
    SERVER_MAP = PARSER.parse();
  }

  /**
   * Returns the template list of instances.
   * @return a HashMap wit all instances of a XML file.
   */
  public static HashMap<String, Template> getTemplateList() {
    return SERVER_MAP;
  }
  
  /**
   * Updates the HashMap of templates when there are changes in the XML file.
   * @return the HasMap of templates.
   */
  public static HashMap<String, Template> updateTemplateList() {
    SERVER_MAP = PARSER.update();
    return SERVER_MAP;
  }
}