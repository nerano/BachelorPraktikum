package mm.controller.modeling;

import java.util.Set;

public class Config {

    String name;
    Set<Wire> wires;
    
    
    public Config(String name, Set<Wire> wires) {
        this.name = name;
        this.wires = wires;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Set<Wire> getWires() {
        return wires;
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
