package mm.net.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;


public class NodeObjects {

	private String id;
	private String typeName;
	private String config;

	public NodeObjects() {
	}

	public NodeObjects(String id) {
		this.id = id;
	}
	

	

	

	

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNodeType() {
		return typeName;
	}

	public void setNodeType(String nodeType) {
		this.typeName = nodeType;
	}



}
