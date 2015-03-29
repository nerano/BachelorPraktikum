package mm.controller.modeling;

import java.util.HashSet;
import java.util.Set;

public class Config {

    private String name;
    private Set<Wire> wires;
    private Wire globalWire;
    
    
    public Config(String name, Set<Wire> wires) {
        this.name = name;
        this.wires = wires;
    
        for (Wire wire : wires) {
            
           if (wire.hasUplink()) {
               globalWire = wire;
           } 
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public Set<Wire> getWires() {
        return wires;
    }
    
    public Wire getGlobalWire() {
        return globalWire;
    }
    
    public Set<String> getRoles() {
        
        Set<String> roles = new HashSet<String>();
        Set<String> endpoints;
        for (Wire wire : wires) {
            endpoints = wire.getEndpoints();
            for (String endpoint : endpoints) {
                if(!endpoint.equals("*")){
                    roles.add(endpoint);
                }
            }
        }
        return roles;
    }
    
 
    public String toString() {
        
       StringBuffer sb = new StringBuffer();
       
       sb.append("ConfigName: '").append(name).append("'\n");
       sb.append("Wires: \n");
       
       for (Wire wire : wires) {
        sb.append(wire.toString()).append("\n");
    }
        
       return sb.toString(); 
    }
    
    
}
