package mm.net.main;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

import mm.net.implementation.NetGearGS108Tv2;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    
        NetGearGS108Tv2 netgear = new NetGearGS108Tv2("netgear.tisch", "192.168.178.94", 1);
        Response r;
        
        netgear.start();
    
        System.out.println("GET PVID");
        r = netgear.getPVID(5);
        
        System.out.println("Statuscode: " + r.getStatus());
        System.out.println("Body: " + (String) r.getEntity());
        
        System.out.println();
        
        System.out.println("SET PVID");
        
        r = netgear.setPVID(5, 101);
        
        System.out.println("Statuscode: " + r.getStatus());
        System.out.println("Body: " + (String) r.getEntity());
        
        System.out.println();
        System.out.println("DESTROY VLAN");
        
        
         r = netgear.destroyVLan(110);
        
        System.out.println("Statuscode: " + r.getStatus());
        System.out.println("Body: " + (String) r.getEntity()); 

       
        System.out.println();
        
        System.out.println("SETVLAN VLAN");
        
        LinkedList<Integer> liste = new LinkedList<Integer>();
        liste.add(5);
        liste.add(6);
        
        
        r =  netgear.setVLan(liste, false, 110, "testSetVlan10");
        
        //System.out.println("Statuscode: " + r.getStatus());
        // System.out.println("Body: " + (String) r.getEntity()); 
       
        System.out.println("Statuscode: " + r.getStatus());
        System.out.println("Body: " + (String) r.getEntity()); 
        
        System.out.println();
        
        System.out.println(netgear.toString(110));
       
        netgear.stop();
    
        
        LinkedList<Integer> ports = new LinkedList<Integer>();
        ports.add(1);
        ports.add(3);
        ports.add(5);
        ports.add(7);
        
        String egressPorts = "";
        
       
        for (int i = 1; i <= 8; i++) {
            if (ports.contains(i)) {
                egressPorts = egressPorts + "1";
            } else {
                egressPorts = egressPorts + "0";
            }
        }
        
        
        egressPorts = String.format("%02x", Integer.parseInt(egressPorts, 2));
    
    
    
       
        
        
    
    }

}
