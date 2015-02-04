package mm.net;

import java.util.LinkedList;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.net.modeling.VLan;


public class ServletContextClass implements ServletContextListener
    {
           
    
    public static VLan vlan1;
    public static VLan vlan2;
    public static int counter;

   /**
    * !-- Initialize everything for the NetService here --!
    */
    public void contextInitialized(ServletContextEvent arg0) 
    {
       counter = 3;     
      
       addExpExample();
    }


    public void contextDestroyed(ServletContextEvent arg0) 
    {
             
    }//end constextDestroyed method

    public static int getCounter(){
        return counter;
    }
  
    public static void counterp(){
        counter++;
    }
    
    public static void addExpExample(){
        
       vlan1 = new VLan(123);
       vlan2 = new VLan(124);
        
       String port1 = "NetComponentA.1";
       String port2 = "NetComponentA.2";
       
       String port3 = "NetzKomponenteB.7";
       String port4 = "NetzKomponenteB.8";
       
       LinkedList<String>list1 = new LinkedList<String>();
       LinkedList<String>list2 = new LinkedList<String>();
       
       list1.add(port1);
       list1.add(port2);
       
       list2.add(port3);
       list2.add(port4);
       
       vlan1.addPorts(list1);
       vlan2.addPorts(list2);
    }
    
   
    
    }