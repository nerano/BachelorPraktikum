package mm.controller.server;


public class TestMainControllerServer {

  public static void main(String[] args) {
     ControllerServer conser = new ControllerServer();
    //System.out.println(conser.getInstances("ganeti"));
    //System.out.println(conser.getTemplate());
    conser.createInstance("ganeti", "test.seemoo.tu-darmstadt.de", "Instanz1", 5012, 0, "10.10.11.3");
    //conser.createInstance(json.toString());
    //conser.startInstance("ganeti", "testvm.seemoo.tu-darmstadt.de","");
    //conser.stopInstance("ganeti", "testvm.seemoo.tu-darmstadt.de","");
    //conser.renameInstance("ganeti", "testController2.seemoo.tu-darmstadt.de", newName);
    //conser.deleteInstance("ganeti", "benvm2.seemoo.tu-darmstadt.de");
    //conser.rebootInstance("ganeti", "testController2.seemoo.tu-darmstadt.de", "");
    //conser.deleteInstance("ganeti", "unittestcontroller.seemoo.tu-darmstadt.de"); 
  }
}
