package mm.server.main;

import mm.server.instance.Instances;

import java.util.HashMap;

public class ServerData {

  private static HashMap<String, Instances> SERVER_MAP;

  /**
   * Initialize the HashMap of instances.
   * @param serverList the given HashMap.
   */
  public ServerData(HashMap<String, Instances> serverList) {
    SERVER_MAP = serverList;
  }

  /**
   * Returns the template list of instances.
   * @return a HashMap wit all instances of a XML file.
   */
  public static HashMap<String, Instances> getTemplateList() {
    return SERVER_MAP;
  }
}