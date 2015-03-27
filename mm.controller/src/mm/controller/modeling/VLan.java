package mm.controller.modeling;

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

        this.id = id;
    }

    
    public boolean isGlobal() {
        return this.global;
    }
    
    
    public void removePorts(LinkedList<String> list) {
        portList.removeAll(list);
    }
    
    public void removePorts(Set<String> list) {
        portList.removeAll(list);
    }
    
    public void addPorts(LinkedList<String> list) {

        portList.addAll(list);

    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void clear() {
        this.name = "";
        this.portList = new HashSet<String>();
    }

    public LinkedList<String> getPortList() {
        return new LinkedList<String>(portList);
    }

    public void setPortList(Set<String> portList) {
        this.portList = portList;
    }

    public int getId() {
        return id;
    }

}
