package mm.net.main;


import java.util.LinkedList;

import javax.ws.rs.core.Response;



import mm.net.implementation.NetGearGS108Tv2;

public class Main {
    
    //private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    
      LinkedList<Integer> liste = new LinkedList<Integer>();
      liste.add(1);
      
      NetGearGS108Tv2 netgear = new NetGearGS108Tv2("netgear.tisch", "netgeargs1", liste);
    Response response = null;

    netgear.start();
    
        netgear.setRowStatus(100, 4);
    
    response = netgear.setStaticName(100, "globa laa");
    
    System.out.println("Statuscode: " + response.getStatus());
    System.out.println("Body: " + (String) response.getEntity());
    
    netgear.stop();
    
  /**  NetPut netput = new NetPut();
    
    LinkedList<String> portList = new LinkedList<String>();
    portList.add("NetGear1;1");
    portList.add("NetGear1;7");
    portList.add("NetGear2;1");
    
    VLan vlan = new VLan("createNewVLanTEST", 117, true);
    vlan.setPortList(portList);
    
    String incoming = gson.toJson(vlan);
    
    netput.setTrunkPort(incoming);
    
    LinkedList<Integer> list = new LinkedList<Integer>();
    list.add(1);
    list.add(7); **/
    
   // response = netgear.setTrunkPort(list, 33, "setTrunkPortTestVLan");
    

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
**/
    //netgear.stop();

   
  }

}
