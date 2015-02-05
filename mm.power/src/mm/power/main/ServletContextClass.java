package mm.power.main;


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
        
        exp1();
        
        
    }
    
    public static void exp1(){
        
    }
    
    
    
    }