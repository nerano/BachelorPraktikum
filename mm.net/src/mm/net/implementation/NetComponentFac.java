package mm.net.implementation;

import java.util.LinkedList;

import mm.net.modeling.NetComponent;

public class NetComponentFac {

    
    /**
     * Returns a NetComponent specified by the parameter Type
     * 
     * Type can be one of the following:
     * 
     * <li>NetGearGS108Tv2</li>
     * <li>Cisco 2960S</li>
     * 
     * 
     * @param type
     * @param id
     * @param host
     * @param trunks
     * @return
     */
    public static NetComponent createNetComponent (String type, String id, String host, 
                         LinkedList<Integer> trunks) {
  
        switch(type) {
        case "NetGearGS108Tv2":
            return new NetGearGS108Tv2(id, host, trunks);
        case "Cisco 2960S":
            return null;
        default:
            System.out.println("Could not recognize NetComponent Type " + type);
            return null;
        }
    }
    
    
    
    
}
