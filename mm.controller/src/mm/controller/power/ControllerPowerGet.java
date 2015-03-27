package mm.controller.power;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.util.LinkedList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import mm.controller.modeling.Component;
import mm.controller.modeling.Experiment;
import mm.controller.modeling.NodeObjects;
import mm.controller.modeling.PowerSource;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ControllerPowerGet {

	private static ClientConfig config = new ClientConfig();
	private static Client client = ClientBuilder.newClient(config);
	private static WebTarget powerTarget = client.target(getBaseUri());

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public LinkedList<PowerSource> status(NodeObjects node)
			throws UnsupportedEncodingException {

		LinkedList<NodeObjects> list = new LinkedList<NodeObjects>();
		list.add(node);
		return status(list);

	}

	public LinkedList<PowerSource> status(Experiment exp)
			throws UnsupportedEncodingException {

		LinkedList<NodeObjects> list = exp.getList();
		return status(list);
	}

	public static LinkedList<PowerSource> status(LinkedList<NodeObjects> nodes)
			 {

		LinkedList<PowerSource> returnList = new LinkedList<PowerSource>();

		String parameter = turnNodeListToStatusString(nodes);

		String powerString = powerTarget.path("get").path(parameter).request()
				.get(String.class);

		Type type = new TypeToken<LinkedList<PowerSource>>() {}.getType();

		returnList = gson.fromJson(powerString, type);

		return returnList;

	}

	private static String turnNodeListToStatusString(LinkedList<NodeObjects> list) {

		StringBuffer buffer = new StringBuffer();

		LinkedList<Component> compList;

		for (NodeObjects nodeObject : list) {

			compList = nodeObject.getComponents();

			for (Component component : compList) {

				buffer.append(component.getPowerSource());

				if (buffer.length() > 0) {
					if (buffer.charAt(buffer.length()-1) != ';') {
						buffer.append(";");
					}
				}
			}
		}
		buffer.append("end");

		try {
            return URLEncoder.encode(buffer.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "This will probably never happen, thanks to the JVM";
        }
	}

	private static URI getBaseUri() {
		return UriBuilder.fromUri("http://localhost:8080/mm.power/rest/")
				.build();
	}
	
	
	
	
	
	
	

}
