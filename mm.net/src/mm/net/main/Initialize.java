package mm.net.main;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.net.modeling.NetComponent;
import mm.net.modeling.StaticComponent;
import mm.net.modeling.VLan;
import mm.net.parser.XmlParser;

public class Initialize implements ServletContextListener {


  /**
   * !-- Initialize everything for the NetService here --!
   */
  public void contextInitialized(ServletContextEvent contextEvent) {

    XmlParser parser = new XmlParser();
    ServletContext context = contextEvent.getServletContext();
    String netComponentPath = context.getRealPath("/NetComponents.xml");
    String vLanConfigPath = context.getRealPath("/vlan.xml");
    String staticComponentPath = context.getRealPath("/staticComponents.xml");
    System.out.println("NetComponent Path: " + netComponentPath);

    /* Parsing VLan Configurations */
    parser.parseXml(vLanConfigPath);
    int[] vlanInfo = parser.getVLanInfo();

    /* Parsing Network Components */
    parser.parseXml(netComponentPath);
    HashMap<String, NetComponent> netComponentMap = parser.getNetComponents();

    LinkedList<VLan> globalVlans = createGlobalVLans(vlanInfo);
    LinkedList<VLan> localVlans = createLocalVlans(vlanInfo);

    /* Parsing Static Components */
    parser.parseXml(staticComponentPath);
    LinkedList<StaticComponent> scList = parser.getStaticComponents();

    /* Creating NetData */
    new NetData(netComponentMap, vlanInfo, globalVlans, localVlans, scList);

    /* Initializing Static VLans */
    initializeStaticVlans();

    /* Creating local Vlans for all NetComponents */

    System.out.println(NetData.getAllNetComponents());

  }

  public void contextDestroyed(ServletContextEvent context) {

  }

  private static LinkedList<VLan> createGlobalVLans(int[] vlanInfo) {
    System.out.println("Reading global VLans");

    LinkedList<VLan> vlanList = new LinkedList<VLan>();

    int globalMin = vlanInfo[0];
    int globalMax = vlanInfo[1];

    for (int i = globalMin; i <= globalMax; i++) {
      vlanList.add(new VLan(i, true));
    }

    System.out.println("Reading global VLans finished");

    return vlanList;
  }

  private static LinkedList<VLan> createLocalVlans(int[] vlanInfo) {

    System.out.println("Reading Local VLans");
    LinkedList<VLan> vlanList = new LinkedList<VLan>();

    int globalMin = vlanInfo[2];
    int globalMax = vlanInfo[3];

    for (int i = globalMin; i <= globalMax; i++) {
      vlanList.add(new VLan(i, false));
    }
    System.out.println("Reading local VLans finished");

    return vlanList;

  }
 
  /**
   * Initializes all static VLANS: management and power
   */
  private static void initializeStaticVlans() {

    for (NetComponent nc : NetData.getAllNetComponents()) {

      nc.start();

      nc.setTrunkPort(nc.getTrunks(),
          NetData.getMANAGE_VLAN_ID(),
          NetData.getMANAGE_VLAN_NAME());

      nc.setTrunkPort(nc.getTrunks(),
          NetData.getPOWER_VLAN_ID(),
          NetData.getPOWER_VLAN_NAME());

      nc.stop();

    }

    for (StaticComponent sc : NetData.getStaticComponents()) {
      sc.setStaticVLan();
    }
  }

}