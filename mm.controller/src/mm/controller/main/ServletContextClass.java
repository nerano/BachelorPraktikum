package mm.controller.main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



public class ServletContextClass implements ServletContextListener
    {
           
    
    
    public static int counter;

   /**
    * !-- Initialize everything for the Controller here --!
    */
    public void contextInitialized(ServletContextEvent arg0) 
    {
       counter = 3;     
       ExpData expdata = new ExpData();
       
    
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
    
}