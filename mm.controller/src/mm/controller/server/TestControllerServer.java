package mm.controller.server;

public class TestControllerServer {

  public static void main(String[] args) {
    ControllerServer conser = new ControllerServer();
    String createJson = "{\"__version__\":1,"
        + "\"name_check\":false,"
        + "\"pnode\":\"pxhost01.seemoo.tu-darmstadt.de\","
        + "\"disk_template\":\"plain\","
        + "\"conflicts_check\":false,"
        + "\"ip_check\":false,"
        + "\"instance_name\":\"testController2.seemoo.tu-darmstadt.de\","
        + "\"nics\":[{\"link\":\"br0\"},{\"mode\":\"bridged\"},{\"ip\":\"10.10.11.3\"}],"
        + "\"disks\":[{\"size\":5120}],"
        + "\"os_type\":\"debootstrap+wheezy\","
        + "\"mode\":\"create\","
        + "\"start\":false}";
    String newName = "{\"new_name\":\"testController23.seemoo.tu-darmstadt.de\","
        + "\"name_check\":false,"
        + "\"ip_check\":false}";
    System.out.println(conser.getInstances());
    //conser.createInstance(createJson);
    //conser.startInstance("testvm.seemoo.tu-darmstadt.de","");
    //conser.stopInstance("testvm.seemoo.tu-darmstadt.de","");
    //conser.renameInstance("testController2.seemoo.tu-darmstadt.de", newName);
    //conser.deleteInstance("testController.seemoo.tu-darmstadt.de");
    //conser.rebootInstance("testController2.seemoo.tu-darmstadt.de", "");
  }
}
