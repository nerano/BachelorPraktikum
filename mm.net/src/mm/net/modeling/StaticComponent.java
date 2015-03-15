package mm.net.modeling;

import mm.net.main.NetData;

public class StaticComponent {


    String id;
    String port;
    String type;
    
    public StaticComponent (String name, String port, String type) {
        id = name;
        this.port = port;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }


    public void setStaticVLan() {
        
        String[] strg = port.split(";");
        
        System.out.println("NET: " + strg[0]);
        System.out.println("PORT: " + strg[1]);
        
        String ncId = strg[0];
        int ncPort = Integer.parseInt(strg[1]);
        int vlanId;
        
        NetComponent nc = NetData.getNetComponentById(ncId);
        
        if(nc == null) {
            System.out.println("The NetComponent '" + ncId + "' does not exist");
        } else { 
            nc.start();
        switch (type) {
        case "power":
            vlanId = NetData.getPOWER_VLAN_ID();
            System.out.println("Setting Power VLan for " + this.id );
            
            nc.addPort(ncPort, vlanId);
            
            break;
        case "management":
            vlanId = NetData.getMANAGE_VLAN_ID();
            System.out.println("Setting Manage VLan for " + this.id );
           
            nc.addPort(ncPort, vlanId);
            
            break;
        default:
            break;
        }
            nc.stop();
        }
        
    }
}
