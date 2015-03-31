package mm.power.modeling;

import mm.power.implementation.AeHome;

public class PowerSupplyFac {

    
    /**
     * Returns a PowerSupply specified by the parameter Type.
     *
     * <p>
     * After you implemented the Interface PowerSupply.java you can add here
     * your new Constructor or overload the createPowerSupply method for new
     * fields. 
     * </p>
     * 
     * Type can be one of the following:
     * 
     * <li>AeHome</li>
     * 
     * 
     * @param type
     *          type of the PowerSupply to create
     * @param id
     *          id of the PowerSupply
     * @param host
     *          host of the PowerSupply
     * @return a PowerSupply of the specified type with the given id, host and
     *         trunks
     */
    public static PowerSupply createPowerSupply(String type, String id, String host) {

      switch (type) {
        case "AeHome":
            return new AeHome(id, type, host);
        default:
          System.out.println("Could not recognize NetComponent Type " + type);
          return null;
      }
    }
}
