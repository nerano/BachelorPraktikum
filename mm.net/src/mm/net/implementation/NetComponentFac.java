package mm.net.implementation;

import java.util.LinkedList;

import mm.net.modeling.NetComponent;

public class NetComponentFac {

  /**
   * Returns a NetComponent specified by the parameter Type.
   *
   * <p>
   * After you implemented the Interface NetComponent.java you can add here
   * your new Constructor or overload the createNetComponent method for new
   * fields. 
   * </p>
   * 
   * Type can be one of the following:
   * 
   * <li>NetGearGS108Tv2</li>
   * 
   * 
   * @param type
   *          type of the NetComponent to create
   * @param id
   *          id of the NetComponent
   * @param host
   *          host of the NetComponent
   * @param trunks
   *          list of trunks of the NetComponent
   * @return a NetComponent of the specified type with the given id, host and
   *         trunks
   */
  public static NetComponent createNetComponent(String type, String id, String host,
      LinkedList<Integer> trunks) {

    switch (type) {
      case "NetGearGS108Tv2":
       NetComponent nc = new NetGearGS108Tv2(id, host, trunks);
       System.out.println(nc.toString());
          return new NetGearGS108Tv2(id, host, trunks);
      case "Cisco 2960S":
        return null;
      default:
        System.out.println("Could not recognize NetComponent Type " + type);
        return null;
    }
  }

}
