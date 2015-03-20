package mm.server.main;

import mm.server.parser.XmlParser;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Initialize implements ServletContextListener {


  /**
   * !-- Initialize everything for the ServerService here --!.
   */
  public void contextInitialized(ServletContextEvent contextEvent) {
    String path = contextEvent.getServletContext().getRealPath("/VM.xml");
    XmlParser parser = new XmlParser(path);
    
    new ServerData(parser);
  }

  public void contextDestroyed(ServletContextEvent arg0) {

  }// end constextDestroyed method
}