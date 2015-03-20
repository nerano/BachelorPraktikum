package mm.net.implementation;

import java.util.LinkedList;

import mm.net.modeling.NetComponent;

public class NetComponentFac {

    
    
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
