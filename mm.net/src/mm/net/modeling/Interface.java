package mm.net.modeling;

public class Interface {

    String name;
    String switchport;
    String role;
    String failure;
    int vlanId;
    
    
    public Interface(String name, String switchport, String role) {
        this.name = name;
        this.switchport = switchport;
        this.role = role;
    }
    
    public Interface(String port, int vlanId, String failure) {
        this.switchport = port;
        this.vlanId = vlanId;
        this.failure = failure;
    }
    
    
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getSwitchport() {
        return switchport;
    }


    public void setSwitchport(String switchport) {
        this.switchport = switchport;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public int getVlanId() {
        return vlanId;
    }

    
    public void setVlanId(int vlanId) {
        this.vlanId = vlanId;
    }
    

    public String toString() {
        
        StringBuffer sb = new StringBuffer(); 
        
        sb.append("InterfaceName: '").append(name).append("' \n");
        sb.append("InterfaceSwitchPort: '").append(switchport).append("' \n");
        sb.append("InterfaceRole: '").append(role).append("' \n");
        
        return sb.toString();
        
    }
    
    
}
