package mm.controller.modeling;

import java.util.Set;

public class Wire {

    Set<String> endpoints;
    boolean uplink;
    
    
    public Wire(Set<String> endpoints) {
        this.endpoints = endpoints;
    
        if(endpoints.contains("*")) {
           uplink = true;
           endpoints.remove("*");
        }
    
    }
    
    public boolean hasUplink() {
        return uplink;
    }
    
    
    public Set<String> getEndpoints() {
        return endpoints;
    }
    
    public String toString() {
        return endpoints.toString();
    }


}
