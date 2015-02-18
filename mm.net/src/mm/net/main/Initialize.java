package mm.net.main;

import java.util.LinkedList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.net.modeling.VLan;


public class Initialize implements ServletContextListener
    {
           
    
    public static VLan vlan1;
    public static VLan vlan2;
    public static VLan vlan3;

    public NetData netData;
   /**
    * !-- Initialize everything for the NetService here --!
    */
    public void contextInitialized(ServletContextEvent arg0) 
    {
       netData = new NetData();  
      
       addExpExample();
    }


    public void contextDestroyed(ServletContextEvent arg0) 
    {
             
    }//end constextDestroyed method

    
    public static void addExpExample(){
        
       vlan1 = new VLan(123);
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
       NetData.addVLan(vlan3);
    
    
    }
    
   
    
    }