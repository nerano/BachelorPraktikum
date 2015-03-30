package mm.net.main;

import java.util.HashMap;
import java.util.LinkedList;

import mm.net.modeling.NetComponent;
import mm.net.modeling.StaticComponent;
import mm.net.modeling.VLan;

public class NetData {

    private static HashMap<String, NetComponent> ALL_NETCOMPONENT      = new HashMap<String, NetComponent>();

    private static LinkedList<StaticComponent>   STATIC_COMPONENTS;

    private static LinkedList<VLan>              GLOBAL_VLAN_LIST;
    private static LinkedList<VLan>              LOCAL_VLAN_LIST;

    private static LinkedList<VLan>              USED_GLOBAL_VLAN_LIST = new LinkedList<VLan>();
    private static LinkedList<VLan>              USED_LOCAL_VLAN_LIST  = new LinkedList<VLan>();

    private static int                           GLOBAL_VLAN_RANGE_MAX;
    private static int                           GLOBAL_VLAN_RANGE_MIN;

    private static int                           LOCAL_VLAN_RANGE_MAX;
    private static int                           LOCAL_VLAN_RANGE_MIN;

    private static int                           POWER_VLAN_ID;
    private static int                           MANAGE_VLAN_ID;

    private static String                        POWER_VLAN_NAME       = "StaticPowerVLan";
    private static String                        MANAGE_VLAN_NAME      = "StaticManagementVLan";

    protected NetData(HashMap<String, NetComponent> list, int[] vlanInfo,
            LinkedList<VLan> globalVlans, LinkedList<VLan> localVlans,
            LinkedList<StaticComponent> scList) {

        // Set all NetComponents //
        setAllNetComponents(list);

        // Set the VLan ranges for local and global //
        setGlobalVlanRanges(vlanInfo);
        setLocalVlanRanges(vlanInfo);

        // Set the VLan IDs for the power net and the management net //
        setPowerVlanId(vlanInfo[4]);
        setManageVlanId(vlanInfo[5]);

        // Set the lists of global and local VLans //
        setGlobalVlanList(globalVlans);
        setLocalVlanList(localVlans);

        // Set all StaticComponents //
        setStaticComponents(scList);
    }

    /**
     * Returns a free global VLAN.
     * 
     * <p>
     * Does NOT check if the VLan is free on all NetComponents.
     * </p>
     * 
     * If the list of global VLANs is empty it returns null. The returned global
     * VLAN is removed from this list and added to the list of used global VLANs
     * and is not returned anymore until it is freed and added back to the list.
     * 
     * @return a VLan from the list of global VLans.
     */
    public static VLan getFreeGlobalVlan() {

        VLan vlan = GLOBAL_VLAN_LIST.peekLast();

        if (vlan == null) {
            int[] info = { GLOBAL_VLAN_RANGE_MIN, GLOBAL_VLAN_RANGE_MAX };
            GLOBAL_VLAN_LIST = Initialize.createLocalVlans(info);
        }
        GLOBAL_VLAN_LIST.remove(vlan);
        USED_GLOBAL_VLAN_LIST.add(vlan);

        System.out.println("NetData allocated VLan " + vlan.getId());

        return vlan;
    }

    public static boolean freeGlobalVlan(int id) {
        for (VLan vLan : USED_GLOBAL_VLAN_LIST) {
            if (vLan.getId() == id) {
                USED_GLOBAL_VLAN_LIST.remove(vLan);
                vLan.clear();
                GLOBAL_VLAN_LIST.add(vLan);
                System.out.println("NetData freed VLan " + id);
                return true;
            }
        }
        return false;
    }

    public static VLan getFreeLocalVlan() {

        VLan vlan = LOCAL_VLAN_LIST.peekFirst();
        if (vlan == null) {
            int[] info = { LOCAL_VLAN_RANGE_MIN, LOCAL_VLAN_RANGE_MAX };
            LOCAL_VLAN_LIST = Initialize.createLocalVlans(info);
        }
        LOCAL_VLAN_LIST.remove(vlan);
        USED_LOCAL_VLAN_LIST.add(vlan);

        System.out.println("NetData allocated local VLan " + vlan.getId());

        return vlan;
    }

    public static boolean freeLocalVlan(int id) {
        for (VLan vLan : USED_LOCAL_VLAN_LIST) {
            if (vLan.getId() == id) {
                USED_LOCAL_VLAN_LIST.remove(vLan);
                vLan.clear();
                LOCAL_VLAN_LIST.add(vLan);
                System.out.println("NetData freed local VLan " + id);
                return true;
            }
        }
        return false;
    }

    public static LinkedList<StaticComponent> getStaticComponents() {
        return STATIC_COMPONENTS;
    }

    public static NetComponent getNetComponentById(String id) {

        NetComponent nc = ALL_NETCOMPONENT.get(id);

        return nc;

    }

    public static VLan getStaticVlan(String net) {

        switch (net) {
        case "power":
            return getPowerVlan();
        case "management":
            return getManagementVlan();
        default:
            return null;
        }

    }

    private static VLan getPowerVlan() {
        VLan vlan = new VLan(POWER_VLAN_ID);
        LinkedList<String> portList = new LinkedList<String>();

        for (NetComponent nc : new LinkedList<NetComponent>(ALL_NETCOMPONENT.values())) {

            for (Integer port : nc.getTrunks()) {
                portList.add(nc.getId() + ";" + port);
            }
        }

        for (StaticComponent sc : STATIC_COMPONENTS) {
            if (sc.getType().equals("power")) {
                portList.add(sc.getPort());
            }
        }
        vlan.addPorts(portList);
        return vlan;
    }

    private static VLan getManagementVlan() {
        VLan vlan = new VLan(MANAGE_VLAN_ID);
        LinkedList<String> portList = new LinkedList<String>();

        for (NetComponent nc : new LinkedList<NetComponent>(ALL_NETCOMPONENT.values())) {

            for (Integer port : nc.getTrunks()) {
                portList.add(nc.getId() + ";" + port);
            }
        }

        for (StaticComponent sc : STATIC_COMPONENTS) {
            if (sc.getType().equals("management")) {
                portList.add(sc.getPort());
            }
        }
        vlan.addPorts(portList);
        return vlan;
    }

    public static int getGLOBAL_VLAN_RANGE_MAX() {
        return GLOBAL_VLAN_RANGE_MAX;
    }

    public static int getGLOBAL_VLAN_RANGE_MIN() {
        return GLOBAL_VLAN_RANGE_MIN;
    }

    public static int getLOCAL_VLAN_RANGE_MAX() {
        return LOCAL_VLAN_RANGE_MAX;
    }

    public static int getLOCAL_VLAN_RANGE_MIN() {
        return LOCAL_VLAN_RANGE_MIN;
    }

    public static int getPOWER_VLAN_ID() {
        return POWER_VLAN_ID;
    }

    public static int getMANAGE_VLAN_ID() {
        return MANAGE_VLAN_ID;
    }

    public static String getMANAGE_VLAN_NAME() {
        return MANAGE_VLAN_NAME;
    }

    public static String getPOWER_VLAN_NAME() {
        return POWER_VLAN_NAME;
    }

    public static LinkedList<NetComponent> getAllNetComponents() {
        return new LinkedList<NetComponent>(ALL_NETCOMPONENT.values());
    }

    private void setAllNetComponents(HashMap<String, NetComponent> list) {
        ALL_NETCOMPONENT = list;
    }

    private void setGlobalVlanRanges(int[] vlanInfo) {
        GLOBAL_VLAN_RANGE_MIN = vlanInfo[0];
        GLOBAL_VLAN_RANGE_MAX = vlanInfo[1];
    }

    private void setLocalVlanRanges(int[] vlanInfo) {
        LOCAL_VLAN_RANGE_MIN = vlanInfo[2];
        LOCAL_VLAN_RANGE_MAX = vlanInfo[3];
    }

    private void setPowerVlanId(int id) {
        POWER_VLAN_ID = id;
    }

    private void setManageVlanId(int id) {
        MANAGE_VLAN_ID = id;
    }

    private void setGlobalVlanList(LinkedList<VLan> globalVlans) {
        GLOBAL_VLAN_LIST = globalVlans;
    }

    private void setLocalVlanList(LinkedList<VLan> localVlans) {
        LOCAL_VLAN_LIST = localVlans;
    }

    private void setStaticComponents(LinkedList<StaticComponent> scList) {
        STATIC_COMPONENTS = scList;
    }

}
