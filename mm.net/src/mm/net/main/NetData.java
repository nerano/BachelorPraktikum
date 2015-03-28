package mm.net.main;

import java.util.HashMap;
import java.util.LinkedList;

import mm.net.modeling.NetComponent;
import mm.net.modeling.StaticComponent;
import mm.net.modeling.VLan;

public class NetData {

  private static HashMap<String, NetComponent> ALL_NETCOMPONENT      = new HashMap<String, NetComponent>();
  // private static LinkedList<Config> CONFIG_LIST = new LinkedList<Config>();

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

    ALL_NETCOMPONENT = list;

    GLOBAL_VLAN_RANGE_MIN = vlanInfo[0];
    GLOBAL_VLAN_RANGE_MAX = vlanInfo[1];

    LOCAL_VLAN_RANGE_MIN = vlanInfo[2];
    LOCAL_VLAN_RANGE_MAX = vlanInfo[3];

    POWER_VLAN_ID = vlanInfo[4];
    MANAGE_VLAN_ID = vlanInfo[5];

    GLOBAL_VLAN_LIST = globalVlans;
    LOCAL_VLAN_LIST = localVlans;

    STATIC_COMPONENTS = scList;

    for (StaticComponent sc : scList) {
      System.out.println("ID " + sc.getId());
      System.out.println("PORT " + sc.getPort());
    }

  }

  public static LinkedList<NetComponent> getAllNetComponents() {
    return new LinkedList<NetComponent>(ALL_NETCOMPONENT.values());
  }

  public static VLan getFreeGlobalVlan() {

    VLan vlan = GLOBAL_VLAN_LIST.getLast();
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

    VLan vlan = LOCAL_VLAN_LIST.getFirst();
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
      
      for (NetComponent nc : new LinkedList<NetComponent> (ALL_NETCOMPONENT.values())) {
        
          for(Integer port : nc.getTrunks()) {
              portList.add(nc.getId() + ";" + port);
          }
    }
      
      
      for (StaticComponent sc : STATIC_COMPONENTS) {
        if(sc.getType().equals("power")) {
            portList.add(sc.getPort());
        }
    }
      vlan.addPorts(portList);
      return vlan;
  }
  
  private static VLan getManagementVlan() {
      VLan vlan = new VLan(MANAGE_VLAN_ID);
      LinkedList<String> portList = new LinkedList<String>();
      
      for (NetComponent nc : new LinkedList<NetComponent> (ALL_NETCOMPONENT.values())) {
        
          for(Integer port : nc.getTrunks()) {
              portList.add(nc.getId() + ";" + port);
          }
    }
      
      
      for (StaticComponent sc : STATIC_COMPONENTS) {
        if(sc.getType().equals("management")) {
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

}
