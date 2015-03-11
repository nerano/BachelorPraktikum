package mm.net.main;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

import mm.net.implementation.NetGearGS108Tv2;

public class Main {
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    NetGearGS108Tv2 netgear = new NetGearGS108Tv2("netgear.tisch", "netgearGS", 1);
    Response response;

    netgear.start();

   /** System.out.println("GET PVID");
    response = netgear.getPVID(5);

    System.out.println("Statuscode: " + response.getStatus());
    System.out.println("Body: " + (String) response.getEntity());

    System.out.println();

    System.out.println("SET PVID");

    response = netgear.setPVID(5, 101);

    System.out.println("Statuscode: " + response.getStatus());
    System.out.println("Body: " + (String) response.getEntity());

    System.out.println();
    System.out.println("DESTROY VLAN");
**/
    //response = netgear.destroyVLan(111);

   // System.out.println("Statuscode: " + response.getStatus());
   // System.out.println("Body: " + (String) response.getEntity());

    System.out.println();

    System.out.println("SETVLAN VLAN");

    LinkedList<Integer> liste = new LinkedList<Integer>();
    liste.add(5); 
    liste.add(6); 

    response = netgear.setVLan(5, true, 111, "testSetVlan");
    
    

    // System.out.println("Statuscode: " + r.getStatus());
    // System.out.println("Body: " + (String) r.getEntity());

    System.out.println("Statuscode: " + response.getStatus());
    System.out.println("Body: " + (String) response.getEntity());

    System.out.println();
    
    
    response = netgear.getEgressAndUntaggedPorts(111);
    
    System.out.println(((String[]) response.getEntity())[0]);
    System.out.println(((String[]) response.getEntity())[1]);

    //System.out.println(netgear.toString(110));
    
    
    int[] ports = {7, 8};
    boolean[] trunks = {false, true};
    
    response = netgear.addPorts(ports, 111, trunks);
    
    System.out.println("Addports: ");
    System.out.println("Statuscode: " + response.getStatus());
    System.out.println("Body: " + (String) response.getEntity());

    netgear.stop();

   
  }

}
