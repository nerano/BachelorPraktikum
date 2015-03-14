package mm.net.main;

import java.util.HashMap;
import java.util.Iterator;
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
        
        /* Parsing Network Components*/
        parser.parseXml(NetComponentPath);
        HashMap<String, NetComponent> netComponentMap = parser.getNetComponents();
        
        /* Parsing VLan Configurations*/
        parser.parseXml(VLanConfigPath);
        int[] vlanInfo = parser.getVLanInfo();
        
        LinkedList<VLan> globalVlans = createGlobalVLans(vlanInfo);
        LinkedList<VLan> localVlans = createLocalVlans(vlanInfo);
        
        /* Parsing Static Components */
        parser.parseXml(StaticComponentPath);
        LinkedList<StaticComponent> scList = parser.getStaticComponents();
        
        /* Creating NetData */
        new NetData(netComponentMap, vlanInfo, globalVlans, localVlans, scList);
        
        /* Initializing Static VLans */
        initializeStaticVlans();
        addExpExample();
    }


    public void contextDestroyed(ServletContextEvent arg0) 
    {
             
    }//end constextDestroyed method

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
        
        int localMin = vlanInfo[2];
        int localMax = vlanInfo[3];
        
        for (int i = localMin; i <= localMax; i++) {
            vlanList.add(new VLan(i, false));
        }
   
        System.out.println("Reading local VLans finished");
        
        return vlanList;
    }
    
    private static void initializeStaticVlans() {
        
        for (StaticComponent sc : NetData.getStaticComponents()) {
            sc.setStaticVLan();
        }

        //TODO topologie vlans setzen
        
    }
    
    public static void addExpExample(){
        
      /** vlan1 = new VLan(123);
       vlan2 = new VLan(124);
       vlan3 = new VLan(125);
        
       String port1 = "NetComponentA.1";
       String port2 = "NetComponentA.2";
       
       String port3 = "NetzKomponenteF.7";
       String port4 = "NetzKomponenteF.8";
       
       LinkedList<String>list1 = new LinkedList<String>();
       LinkedList<String>list2 = new LinkedList<String>();
       LinkedList<String>list3 = new LinkedList<String>();
       
       list1.add(port1);
       list1.add(port2);
       list1.add(port3);
       list1.add(port4);
       
       list2.add(port3);
       list2.add(port4);
       
       list3.add(port1);
       list3.add(port2);
       
       vlan1.addPorts(list1);
       vlan2.addPorts(list2);
       vlan3.addPorts(list3);
    
       NetData.addVLan(vlan1);
       NetData.addVLan(vlan2);
       NetData.addVLan(vlan3); **/
    
    
    }
    
   
    
    }