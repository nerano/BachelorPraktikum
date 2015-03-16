package mm.controller.server;

import static org.junit.Assert.*;

import org.junit.Test;

public class ControllerServerTest {

  ControllerServer conser = new ControllerServer();
  
  @Test
  public void testGetCreateDelete() {
    String createInstance = "{\"__version__\":1,"
        + "\"name_check\":false,"
        + "\"pnode\":\"pxhost01.seemoo.tu-darmstadt.de\","
        + "\"disk_template\":\"plain\","
        + "\"conflicts_check\":false,"
        + "\"ip_check\":false,"
        + "\"instance_name\":\"unittestcontroller.seemoo.tu-darmstadt.de\","
        + "\"nics\":[{\"link\":\"br0\"},{\"mode\":\"bridged\"},{\"ip\":\"10.10.11.3\"}],"
        + "\"disks\":[{\"size\":5120}],"
        + "\"os_type\":\"debootstrap+wheezy\","
        + "\"mode\":\"create\","
        + "\"start\":false}";
    try {
      assertEquals("Die Instanz \"unittestcontroller.seemoo.tu-darmstadt.de\" ist nicht auf dem Server",
          false, conser.getInstances().contains("unittestcontroller.seemoo.tu-darmstadt.de"));
      conser.createInstance(createInstance);
      Thread.sleep(10000);
      assertEquals("Die Instanz \"unittestcontroller.seemoo.tu-darmstadt.de\" ist auf dem Server",
          true, conser.getInstances().contains("unittestcontroller.seemoo.tu-darmstadt.de"));
      conser.deleteInstance("unittestcontroller.seemoo.tu-darmstadt.de");
      Thread.sleep(10000);
      assertEquals("Die Instanz \"unittestcontroller.seemoo.tu-darmstadt.de\" ist auf dem Server",
          false, conser.getInstances().contains("unittestcontroller.seemoo.tu-darmstadt.de"));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testGetInfoStartStop() {
    try {
     assertEquals("Die Instanz \"benvm.seemoo.tu-darmstadt.de\" wurde nicht gestartet",
          false, conser.getInstanceInfoParam("benvm.seemoo.tu-darmstadt.de", "status")
          .contains("running"));
     conser.startInstance("benvm.seemoo.tu-darmstadt.de", "");
     Thread.sleep(10000);
     assertEquals("Die Instanz \"benvm.seemoo.tu-darmstadt.de\" wurde gestartet",
         true, conser.getInstanceInfoParam("benvm.seemoo.tu-darmstadt.de", "status")
         .contains("running"));
    conser.stopInstance("benvm.seemoo.tu-darmstadt.de", "");
    Thread.sleep(10000);
    assertEquals("Die Instanz \"benvm.seemoo.tu-darmstadt.de\" wurde nicht gestartet",
        false, conser.getInstanceInfoParam("benvm.seemoo.tu-darmstadt.de", "status")
        .contains("running"));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
