package mm.power.main;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;
import mm.power.implementation.AEHome;
import mm.power.modeling.PowerSupply;
import mm.power.parser.XmlParser;

public class Initialize implements ServletContextListener {

 // public PowerData  psdata;
  

  /**
   * !-- Initialize everything for the PowerService here --!
   */
  public void contextInitialized(ServletContextEvent arg0) {

    /*String id = "TESTAEHOME#1";
    String type = "AE HOME";
    String host = "";

    PowerSupply ps1 = new AEHome(id, type, host);
    
    HashMap<String, PowerSupply> map = new HashMap<String, PowerSupply>();
    map.put("TESTAEHOME#1", ps1);
    
    //psdata = new PowerData(map);
    PowerData.addPs(ps1);*/
    XmlParser parser = new XmlParser();
   // parser.parseXml("C:/Users/Sebastian/git/BachelorPraktikum/mm.power/PowerSupply.xml");
    parser.parseXml("C:/Users/milton/git/BachelorPraktikum/mm.power/PowerSupply.xml");
    HashMap<String, PowerSupply> map = parser.getPowerSupply();
    System.out.println(map.get("AeHome2").toString());
    new PowerData(map);
   // PowerData.addPs(ps1);

    /** try {
      map.get("AeHome#2").turnOn(1);
      map.get("AeHome#2").turnOn(3);
      map.get("AeHome#2").turnOff(1);
      Thread.sleep(5000);
      map.get("AeHome#2").turnOff(3);
      
    } catch (IOException | TransferNotCompleteException
        | EntryDoesNotExistException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } **/
    
    
  }

  public void contextDestroyed(ServletContextEvent arg0) {

  }// end constextDestroyed method


}