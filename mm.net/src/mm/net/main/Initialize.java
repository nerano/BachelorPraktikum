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

public class Initialize implements ServletContextListener
{

    public static VLan vlan1;
    public static VLan vlan2;
    public static VLan vlan3;

    /**
     * !-- Initialize everything for the NetService here --!
     */
    public void contextInitialized(ServletContextEvent contextEvent)
    {

        XmlParser parser = new XmlParser();
        ServletContext context = contextEvent.getServletContext();
        String NetComponentPath = context.getRealPath("/NetComponents.xml");
        String VLanConfigPath = context.getRealPath("/vlan.xml");
        String StaticComponentPath = context.getRealPath("/staticComponents.xml");
        System.out.println("NetComponent Path: " + NetComponentPath);

        /* Parsing VLan Configurations */
        parser.parseXml(VLanConfigPath);
        int[] vlanInfo = parser.getVLanInfo();

        /* Parsing Network Components */
        parser.parseXml(NetComponentPath);
        HashMap<String, NetComponent> netComponentMap = parser.getNetComponents();

        LinkedList<VLan> globalVlans = createGlobalVLans(vlanInfo);

        /* Parsing Static Components */
        parser.parseXml(StaticComponentPath);
        LinkedList<StaticComponent> scList = parser.getStaticComponents();

        /* Creating NetData */
        new NetData(netComponentMap, vlanInfo, globalVlans, scList);

        /* Initializing Static VLans */
        initializeStaticVlans();

        /* Creating local Vlans for all NetComponents */
        createLocalVlans();

        System.out.println(NetData.getAllNetComponents());

    }

    public void contextDestroyed(ServletContextEvent context)
    {

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

    private static void createLocalVlans() {

        System.out.println("Reading Local VLans");
        for (NetComponent nc : NetData.getAllNetComponents()) {
            nc.createLocalVlans();
        }
        System.out.println("Reading local VLans finished");

    }

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