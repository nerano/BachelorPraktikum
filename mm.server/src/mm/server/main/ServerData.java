package mm.server.main;

import mm.server.instance.Template;

import java.util.HashMap;

public class ServerData {

  private static HashMap<String, Template> SERVER_MAP;

  /**
   * Initialize the HashMap of instances.
   * @param serverList the given HashMap.
   */
  public ServerData(HashMap<String, Template> serverList) {
    SERVER_MAP = serverList;
  }

  /**
   * Returns the template list of instances.
   * @return a HashMap wit all instances of a XML file.
   */
  public static HashMap<String, Template> getTemplateList() {
    return SERVER_MAP;
  }
}