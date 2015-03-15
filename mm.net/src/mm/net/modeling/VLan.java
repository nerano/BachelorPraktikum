package mm.net.modeling;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class VLan {

    String               name;
    private int          id;
    private Set<String> portList = new HashSet<String>();
    private boolean      global;

    public VLan(int id, boolean global) {

        this.id = id;
        this.global = global;

    }

    public VLan(int id) {
        this.id = id;
    }

    public VLan(String name, int id, boolean global) {
        this.name = name;
        this.id = id;
        this.global = global;
    }

    public void addPorts(LinkedList<String> list) {

        portList.addAll(list);

    }

    public void clear() {
        this.name = "";
        this.portList = new HashSet<String>();
    }

    public LinkedList<String> getPortList() {
        return new LinkedList<String>(portList);
    }

    public void setPortList(LinkedList<String> portList2) {
        for (String port : portList2) {
            portList.add(port);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }
    
    public boolean isGlobal() {
        return global;
    }
    

}
