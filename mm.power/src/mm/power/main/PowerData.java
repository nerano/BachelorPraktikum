package mm.power.main;

import java.util.HashMap;

import mm.power.modeling.PowerSupply;

public class PowerData {

  private static HashMap<String, PowerSupply> POWERSUPPLY_MAP = new HashMap<String, PowerSupply>();

  PowerData(HashMap<String, PowerSupply> powerMap) {
    setPowerSupplyMap(powerMap);
  }

  private static void setPowerSupplyMap(HashMap<String, PowerSupply> map) {
    POWERSUPPLY_MAP = map;
  }

  /**
   * Returns the PowerSupply with the given ID.
   * 
   * @param id
   *          ID of the PowerSupply
   * @return PowerSupply with the ID, null if no PowerSupply was found
   */
  public static PowerSupply getById(String id) {
    PowerSupply ps;
    ps = POWERSUPPLY_MAP.get(id);
    return ps;
  }

  /**
   * Adds a PowerSupply to the global data.
   * 
   * @param ps
   *          PowerSupply to add
   */
  public static void addPs(PowerSupply ps) {
    POWERSUPPLY_MAP.put(ps.getId(), ps);
  }

  /**
   * Removes a PowerSupply from the global data.
   * 
   * @param ps
   *          PowerSupply to remove
   * @return boolean true if PowerSupply was in the list and was removed, false
   *         if PowerSupply was not in the list
   */
  public static boolean removePs(PowerSupply ps) {

    String id = ps.getId();

    PowerSupply ps1 = POWERSUPPLY_MAP.remove(id);

    if (ps1 == null) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Removes a PowerSupply from the global data.
   * 
   * @param id
   *          ID of the PowerSupply to remove
   * @return bool true if PowerSupply was in the list and was removed, false if
   *         PowerSupply was not in the list
   */
  public static boolean removePs(String id) {

    PowerSupply ps = POWERSUPPLY_MAP.remove(id);

    if (ps == null) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Returns if a PowerSupply with a given ID exists in the global data.
   * 
   * @param id
   *          PowerSupply ID to look for
   * @return false if the PowerSupply does not exist, true if it does
   */
  public static boolean exists(String id) {

    if (POWERSUPPLY_MAP.get(id) != null) {
      return true;
    } else {
      return false;
    }
  }

}
