package mm.controller.modeling;

import java.util.LinkedList;

public class VLan {

    private String             name;
    private int                id;
    private LinkedList<String> portList = new LinkedList<String>();

    public VLan(int id, String name) {
        this.name = name;
        this.id = id;

    }

    public void addPorts(LinkedList<String> list) {
        portList.addAll(list);
    }

    public LinkedList<String> getPortList() {
        return portList;
    }

    /**
     * public void setPortList(LinkedList<String> portList) { this.portList =
     * portList; }
     **/

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

}
