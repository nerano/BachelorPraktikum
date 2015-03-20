package mm.net.main;

import java.util.Arrays;
import java.util.LinkedList;

import javax.ws.rs.core.Response;

import mm.net.modeling.NetComponent;
import mm.net.modeling.StaticComponent;
import mm.net.modeling.VLan;

public class ConsistencyCheck {

  
   
    /**
     * Checks the actual consistency of the net with the given name.
     * 
     * @param net  Can be "management" or "power"
     */
    public static Response checkStaticConsistency(String net) {

        System.out.println("START MANAGEMENT CONSISTENCY CHECK");

        LinkedList<StaticComponent> scList = NetData.getStaticComponents();
        LinkedList<NetComponent> ncList = NetData.getAllNetComponents();
        
        
        Response response;
        int responseStatus = 200;
        String responseString = "";
        StringBuilder returnBuilder = new StringBuilder();
        returnBuilder.append(net + "Net - CONSISTENCY CHECK");
        
        int vlanId;
        
        switch (net) {
        case "management":
            vlanId = NetData.getMANAGE_VLAN_ID();
            break;
        case "power":
            vlanId = NetData.getPOWER_VLAN_ID();
            break;
        default:
            return Response.status(500).entity("Could not find the net " + net).build();
        }

        for (NetComponent netComponent : ncList) {

            StringBuilder egressBuilder = new StringBuilder();
            StringBuilder untaggedBuilder = new StringBuilder();

            String consistEgress = "";
            String consistUntagged = "";
            String egress = null;
            String untagged = null;
            int[] consistPvids = new int[netComponent.getPorts()];
            int[] pvids = null;
            boolean consistency = true;

            LinkedList<Integer> trunks = netComponent.getTrunks();

            
            // Calculating the configuration that should be present
            for (int i = 1; i <= netComponent.getPorts(); i++) {

                if (trunks.contains(i)) {
                    egressBuilder.append("1");
                    untaggedBuilder.append("0");
                } else {
                    egressBuilder.append("0");
                    untaggedBuilder.append("1");
                }
            }
            // Calculating the configuration that should be present
            for (StaticComponent staticComponent : scList) {

                if (staticComponent.getType().equals(net)) {

                    String nc = staticComponent.getNetComponent();

                    if (nc.equals(netComponent.getId())) {

                        egressBuilder.setCharAt(staticComponent.getSwitchport() - 1,
                                '1');
                        untaggedBuilder.setCharAt(staticComponent.getSwitchport() - 1,
                                '1');

                    }

                }
            }

            consistEgress = egressBuilder.toString();
            consistUntagged = untaggedBuilder.toString();

            // Calculating the PVIDs that should be present
            for (int i = 0; i < consistPvids.length; i++) {
                if(consistEgress.charAt(i) == '1' && consistUntagged.charAt(i) == '1' ) {
                    consistPvids[i] = vlanId;
                } 
                
                if(consistEgress.charAt(i) == '1' && consistUntagged.charAt(i) == '0' ) {
                    consistPvids[i] = 1;
                } 
                
                if(consistEgress.charAt(i) == '0') {
                    consistPvids[i] = -1;
                } 
                
            }
            
            netComponent.start();
            response = netComponent.getEgressAndUntaggedPorts(vlanId);
            
            if (response.getStatus() != 200) {
                // TODO errorhandling
            } else {
                String[] sa = (String[]) response.getEntity();
                egress = sa[0];
                untagged = sa[1];
            }
            
            response = netComponent.getAllPvids();
            
            if (response.getStatus() != 200) {
                // TODO errorhandling
            } else {
                pvids = (int[]) response.getEntity();
            }
            
            netComponent.stop();
           
            System.out.println(response.getStatus());
          
            for (int i = 0; i < consistPvids.length; i++) {
                if(consistPvids[i] != -1) {
                    if(consistPvids[i] != 1 && pvids[i] != consistPvids[i]) {
                        consistency = false;
                    }
                }
            }
            
            System.out.println("EGRESS: " + egress);
            System.out.println("CONSIST EGRESS " + consistEgress);
            System.out.println("UNTAGGED: " + untagged);
            System.out.println("CONSIST UNTAGGED " + consistUntagged);
            System.out.println("CONSIST PVID " + Arrays.toString(consistPvids));
            System.out.println("PVID " + Arrays.toString(pvids));
            
            if (egress.equals(consistEgress) && untagged.equals(consistUntagged)) {
                System.out.println("Consistent");
            } else {
                System.out.println("Not Consistent");
                consistency = false;
            }
            
            if(!consistency) {
                
                returnBuilder.append(net).append("Net not consistent on NetComponent '")
                               .append(netComponent.getId()).append("' \n");
                
                returnBuilder.append("Should be: \n");
                returnBuilder.append("Egress: " + consistEgress +  "\n");
                returnBuilder.append("Untagged: " + consistUntagged +  "\n");
                returnBuilder.append("PVIDs: " + Arrays.toString(consistPvids) +  "\n");
                
                returnBuilder.append("But is: \n");
                returnBuilder.append("Egress: " + egress +  "\n");
                returnBuilder.append("Untagged: " + untagged +  "\n");
                returnBuilder.append("PVIDs: " + Arrays.toString(pvids) +  "\n");
            } else {
                returnBuilder.append(net).append("Net is consistent on NetComponent '")
                .append(netComponent.getId()).append("' \n");
            }
            
        }
       
        responseString = returnBuilder.toString();
        return Response.status(responseStatus).entity(responseString).build();

    }
    
    /**
     * 
     * @param vlan
     */
    public static void consistencyCheck(VLan vlan) {
        
        
    }

}
