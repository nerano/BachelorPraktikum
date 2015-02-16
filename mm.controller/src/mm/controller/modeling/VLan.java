package mm.controller.modeling;

import java.util.LinkedList;

public class VLan {

	private final int id;
	private LinkedList<String> portList;

	public VLan(int id) {

		this.id = id;

	}

	public void addPorts(LinkedList<String> list) {

		if (portList == null) {
			portList = new LinkedList<String>();
			portList.addAll(list);
		} else {
			portList.addAll(list);
		}

	}

	public LinkedList<String> getPortList() {
		return portList;
	}
	/**
	
	public void setPortList(LinkedList<String> portList) {
		this.portList = portList;
	} **/

	public int getId() {
		return id;
	}

}
