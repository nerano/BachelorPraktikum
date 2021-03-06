package mm.net.modeling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;

import mm.net.main.NetData;

public class VLan {

    String               name;
    private int          id;
    private HashSet<String>  portList = new HashSet<String>();
    private boolean      global;

    public VLan(int id, boolean global) {

        this.id = id;
        this.global = global;

    }

    public VLan(int id) {
        this.id = id;
    }

    public VLan(String name, int id, boolean global) {
        this.name = name;
        this.id = id;
        this.global = global;
    }

    public void addPorts(LinkedList<String> list) {

        portList.addAll(list);

    }

    public void clear() {
        this.name = "";
        this.portList = new HashSet<String>();
    }

    public LinkedList<String> getPortList() {
        return new LinkedList<String>(portList);
    }

    public void setPortList(LinkedList<String> portList2) {
        for (String port : portList2) {
            portList.add(port);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }
    
    public boolean isGlobal() {
        return global;
    }
    
    /**
     * Performs a consistency check on the VLan.
     * 
     * <p>
     * Creates the configuration which should be present on the NetComponents in the VLAN
     * and then checks if the expected configuration is actually present.
     * </p>
     * 
     * <p>
     * To create the expected configuration all NetComponents, which are part of this
     * VLan are fetched and for each the expected configuration is calculated.
     * </p>
     * 
     * <p> 
     * First all ports in the VLan are set as egress ports and all ports which are egress
     * ports and at the same time specified as trunks in the NetComponents.xml are set
     * as tagged egress ports. The remaining ports are untagged ports. For each port 
     * the PVID is created. If a port is an egress/untagged port the PVID should be the 
     * same as the VLan ID, if a port is an egress/tagged port the PVID should be 1.
     * A port which is not an egress port gets the invalid (= not to check) PVID of -1.
     * </p>
     * 
     * <p>
     * After creating this configuration the actual configuration is fetched from the
     * NetComponents and both are compared. The results are gathered and then returned.
     * 
     * @return  the status report of the consistency check
     */
    public String isConsistent() {
        NetComponent nc;
        StringBuilder returnBuilder = new StringBuilder("Consistency check on ID " + id + "\n");
        
        HashMap<String, LinkedList<Integer>> map = portListToHashMap(portList);

        for (Entry<String, LinkedList<Integer>> entry : map.entrySet()) {
          
            String ncId = entry.getKey();
            nc = NetData.getNetComponentById(ncId);
            LinkedList<Integer> portList = entry.getValue();
           
            boolean consistency = true;
            int[] pvids = null;
            int[] consistPvids = new int[nc.getPorts()];
            
            StringBuilder egressBuilder = new StringBuilder();
            StringBuilder untaggedBuilder = new StringBuilder();
            
            // Calculating the port configurations that should be present
            for (int i = 1; i <= nc.getPorts(); i++) {
                    egressBuilder.append("0");
                    untaggedBuilder.append("1");
            }
            
            for (Integer port : portList) {
                egressBuilder.setCharAt(port-1, '1');
            
                if(nc.getTrunks().contains(port)) {
                    untaggedBuilder.setCharAt(port-1, '0');
                }
            
            }
            
            String consistentEgress = egressBuilder.toString();
            String consistentUntagged = untaggedBuilder.toString();
            
            // Calculating the PVIDs that should be present
            for (int i = 0; i < consistPvids.length; i++) {
                if(consistentEgress.charAt(i) == '1' &&  consistentUntagged.charAt(i) == '1' ) {
                    consistPvids[i] = id;
                } 
                
                if(consistentEgress.charAt(i) == '1' &&  consistentUntagged.charAt(i) == '0' ) {
                    consistPvids[i] = 1;
                } 
                
                if(consistentEgress.charAt(i) == '0') {
                    consistPvids[i] = -1;
                } 
                
            }
            
            String egress = "noSuchInstance";
            String untagged = "noSuchInstance";
            
            nc.start();
            Response response = nc.getEgressAndUntaggedPorts(id);
            
            if (response.getStatus() != 200) {
                returnBuilder.append(((String[]) response.getEntity())[0]);
            } else {
                String[] sa = (String[]) response.getEntity();
                egress = sa[0];
                untagged = sa[1];
            }
            
            response = nc.getAllPvids();
            
            if (response.getStatus() != 200) {
                returnBuilder.append((String) response.getEntity());
            } else {
                pvids = (int[]) response.getEntity();
            }
            
            nc.stop();
            
            // Checking if expected configuration is actually present
            for (int i = 0; i < consistPvids.length; i++) {
                if(consistPvids[i] != -1) {
                    if(consistPvids[i] != 1 && pvids[i] != consistPvids[i]) {
                        consistency = false;
                    }
                }
            }
            
            
            if (egress.equals(consistentEgress) && untagged.equals(consistentUntagged)) {
            } else {
                consistency = false;
            }
        
            if(!consistency) {
                returnBuilder.append("Not consistent on NetComponent '")
                               .append(nc.getId()).append("' \n");
                returnBuilder.append("Should be: \n");
                returnBuilder.append("Egress: " + consistentEgress +  "\n");
                returnBuilder.append("Untagged: " + consistentUntagged +  "\n");
                returnBuilder.append("PVIDs: " + Arrays.toString(consistPvids) +  "\n");
                
                returnBuilder.append("But is: \n");
                returnBuilder.append("Egress: " + egress +  "\n");
                returnBuilder.append("Untagged: " + untagged +  "\n");
                returnBuilder.append("PVIDs: " + Arrays.toString(pvids) +  "\n\n");
            } else {
                returnBuilder.append("Is consistent on NetComponent '")
                .append(nc.getId()).append("' \n");
        }
            
        }
            return returnBuilder.toString();
        
    }
    
    /**
     * Checks if a VLAN ID is free (= does not exist) on all NetComponents.
     * 
     * @return  true if VLAN ID is free on all NetComponents, false otherwise
     */
    public boolean isFree() {
        LinkedList<NetComponent> ncList = NetData.getAllNetComponents();
        
        System.out.println(Arrays.asList(ncList).toString());
        
        for (NetComponent nc : ncList) {
            nc.start();
            System.out.println("NCID " + nc.getId());
            System.out.println("vlanId " + id);
            if(!nc.isFree(id)) {
                System.out.println("On " + nc.getId() +"vlan " + id + " is " + nc.isFree(id));
                nc.stop();
                return false;
            }
            nc.stop();
        }
        return true;
    }
    
    /**
     * Checks if a VLAN ID is free (= does not exist) on the list of NetComponents which are in the
     * portList of this VLAN.
     * 
     * @return true if VLAN ID is free on all NetComponents, false otherwise
     */
    public boolean isFreeOnNC() {
        HashMap<String, LinkedList<Integer>> map = portListToHashMap(portList);
        NetComponent nc;
        
        for (Entry<String, LinkedList<Integer>> entry : map.entrySet()) {
            
            String ncId = entry.getKey();
            nc = NetData.getNetComponentById(ncId);
            nc.start();
            System.out.println("NCID " + nc.getId());
            System.out.println("vlanId " + id);
            if(!nc.isFree(id)) {
                System.out.println("On " + nc.getId() +"vlan " + id + " is " + nc.isFree(id));
                nc.stop();
                return false;
            }
            nc.stop();
        }
        return true;
    }
    
    /**
     * Transforms a List of NetComponent;Port pairs to a HashMap, where the keys
     * are the IDs of the NetComponents and the value is a List of Ports.
     * 
     * The map contains the same NetComponents and Ports, but in a different
     * format.
     * 
     * @param portList
     * @return
     */
    private HashMap<String, LinkedList<Integer>> portListToHashMap(HashSet<String> portList) {

        String[] portArray;
        String nc;
        String portnumber;

        LinkedList<Integer> list = new LinkedList<Integer>();

        HashMap<String, LinkedList<Integer>> map = new HashMap<String, LinkedList<Integer>>();

        for (String port : portList) {

            portArray = port.split(";");
            nc = portArray[0];
            portnumber = portArray[1];

            list = map.get(nc);

            if (list != null) {
                list.add(Integer.parseInt(portnumber));
            } else {
                list = new LinkedList<Integer>();
                list.add(Integer.parseInt(portnumber));
                map.put(nc, list);
            }
        }
        return map;

    }
    

}
