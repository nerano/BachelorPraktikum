package mm.power.main;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.power.implementation.AEHome;
import mm.power.implementation.PowerSupply;
import mm.power.main.*;




public class Initialize implements ServletContextListener
    {
           
    
	public PowerData psdata;
    public static int counter;

   /**
    * !-- Initialize everything for the PowerService here --!
    */
    public void contextInitialized(ServletContextEvent arg0) 
    {
       counter = 3;     
       
       psdata = new PowerData();
      
       addExpExample();
    
       String id = "TESTAEHOME#1";
   		String type = "AE HOME";
   		String host = "";
   
   
   	PowerSupply ps1 = new AEHome(id, type, host);
   	
   	
   	PowerData.addPs(ps1);
    
    
    
    
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
        
        exp1();
        
        
        
    }
    
    public static void exp1(){

    	
    
    
    
    
    }
    	
    
    }