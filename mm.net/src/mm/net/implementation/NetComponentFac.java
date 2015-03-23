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
        
        NetComponent nc = null;
  
        switch(type) {
        case "NetGearGS108Tv2":
            nc = new NetGearGS108Tv2(id, host, trunks);
            break;
        case "Cisco 2960S":
            break;
        default:
            System.out.println("Could not recognize NetComponent Type " + type);
        }
        return nc;
    }
    
    
    
    
}
