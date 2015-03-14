package mm.controller.modeling;

import java.util.Set;

public class Wire {

    Set<String> endpoints;
    
    
    public Wire(Set<String> endpoints) {
        this.endpoints = endpoints;
    }
    
    public Set<String> getEndpoints() {
        return endpoints;
    }
    
    public String toString() {
        return endpoints.toString();
    }


}
