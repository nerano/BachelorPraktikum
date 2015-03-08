package mm.server.main;

import mm.server.instance.Instances;

import java.util.HashMap;

public class ServerData {

  private static HashMap<String, Instances> SERVER_MAP;

  public ServerData(HashMap<String, Instances> powerList) {
    SERVER_MAP = powerList;
  }

  public static HashMap<String, Instances> getServerList() {
    return SERVER_MAP;
  }

  /**
   * Returns the PowerSupply with the given ID.
   * 
   * @param id
   *          ID of the PowerSupply
   * @return PowerSupply with the ID, null if no PowerSupply was found
   */
/*  public static PowerSupply getById(String id) {

    PowerSupply ps;

    ps = SERVER_MAP.get(id);

    return ps;
  }
*/
  /**
   * Adds a PowerSupply to the global data.
   * 
   * @param ps
   *          PowerSupply to add
   */
/*  public static void addPs(PowerSupply ps) {
    SERVER_MAP.put(ps.getId(), ps);
  }
*/
  /**
   * Removes a PowerSupply from the global data.
   * 
   * @param ps
   *          PowerSupply to remove
   * @return boolean true if PowerSupply was in the list and was removed, false if
   *         PowerSupply was not in the list
   */
/*  public static boolean removePs(PowerSupply ps) {

    String id = ps.getId();

    PowerSupply ps1 = SERVER_MAP.remove(id);

    if (ps1 == null) {
      return false;
    } else {
      return true;
    }
  }
*/
  /** Removes a PowerSupply from the global data.
   * 
   * @param id
   *          ID of the PowerSupply to remove
   * @return bool true if PowerSupply was in the list and was removed, false if
   *         PowerSupply was not in the list
   */
/*  public static boolean removePs(String id) {

    PowerSupply ps = SERVER_MAP.remove(id);

    if (ps == null) {
      return false;
    } else {
      return true;
    }
  }
*/
  /** Returns if a PowerSupply with a given ID exists in the global data.
   * 
   * @param id
   *          PowerSupply ID to look for
   * @return false if the PowerSupply does not exist, true if it does
   */
/*  public static boolean exists(String id) {

    if (SERVER_MAP.get(id) != null) {
      return true;
    } else {
      return false;
    }

  }
*/
}
