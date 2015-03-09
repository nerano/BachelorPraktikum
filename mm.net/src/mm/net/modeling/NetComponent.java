package mm.net.modeling;

import javax.ws.rs.core.Response;

public interface NetComponent {

	public Response start();
	public Response stop();
    public Response setVLan(String ports, int vlanId, String pvid);
	public Response setVLan(String config);
	public Response getPVID(int port);

}
