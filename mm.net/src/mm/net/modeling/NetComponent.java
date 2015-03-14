package mm.net.modeling;

import java.util.LinkedList;

import javax.ws.rs.core.Response;

public interface NetComponent {

	public Response start();
	public Response stop();
	public Response setVLan(int port, boolean global, int vlanId, String name);
	public Response setVLan(LinkedList<Integer> ports, boolean global, int vlanId, String name);
	public Response getPVID(int port);

}
