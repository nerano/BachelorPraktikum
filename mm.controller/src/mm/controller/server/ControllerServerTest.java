package mm.controller.server;

import static org.junit.Assert.*;

import org.junit.Test;

public class ControllerServerTest {

  
  @Test
  public void testGetCreateStartStopDelete() {
    String instance = "controllertest.seemoo.tu-darmstadt.de";
    String createInstance = "{\"name\":\"" + instance + "\",\"template\":\"Instanz2\",\"bridge\":\"br0\",\"size\":5012,\"ip\":\"10.10.11.4\"}";
    try {
      assertEquals("Die Instanz " + instance + " ist nicht auf dem Server",
          false, ControllerServer.getInstances("ganeti").contains(instance));
      ControllerServer.createInstance("ganeti", instance, "Instanz2", 5012, 0, "10.10.11.4");
      
      Thread.sleep(10000);
      
      assertEquals("Die Instanz " + instance + " ist auf dem Server",
          true, ControllerServer.getInstances("ganeti").contains(instance));
      
      assertEquals("Die Instanz " + instance + " wurde nicht gestartet",
          false, ControllerServer.getInstanceInfoParam("ganeti", instance, "status")
          .contains("running"));
      
      ControllerServer.startInstance("ganeti", instance, "");
      
      Thread.sleep(10000);
      
      assertEquals("Die Instanz " + instance + " wurde gestartet",
         true, ControllerServer.getInstanceInfoParam("ganeti", instance, "status")
         .contains("running"));
      
      ControllerServer.stopInstance("ganeti", instance, "");
      
      Thread.sleep(10000);
      
      assertEquals("Die Instanz " + instance + " wurde nicht gestartet",
        false, ControllerServer.getInstanceInfoParam("ganeti", instance, "status")
        .contains("running"));
      
      ControllerServer.deleteInstance("ganeti", instance);
      
      Thread.sleep(10000);
      
      assertEquals("Die Instanz " + instance + " ist auf dem Server",
          false, ControllerServer.getInstances("ganeti").contains(instance));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}