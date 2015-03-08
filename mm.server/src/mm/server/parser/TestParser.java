package mm.server.parser;

import java.util.HashMap;

import mm.server.instance.Instances;

public class TestParser {

  XmlParser parser = new XmlParser("/home/benedikt/git/BachelorPraktikum/xml/VM.xml");
  HashMap<String, Instances> map = parser.parse();
  
  public static void main(String[] args) {
    TestParser tp = new TestParser();
    String name = "test";
    System.out.println(tp.map.keySet().toString());
    System.out.println(tp.map.get("Instanz1").toString());
    String create = tp.map.get("Instanz1").toString();
    create = create.substring(0, create.length() - 1);
    create = create.concat(",\"instance_name\":\"" + name + "\"}");
    System.out.println(create);
    System.out.println(tp.map.get("Instanz3").toString());
    System.out.println(tp.map.get("Instanz2").toString());
  }
}
