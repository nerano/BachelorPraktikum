package mm.controller.net;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.util.LinkedList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import mm.controller.modeling.Experiment;

import mm.controller.modeling.VLan;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ControllerNetGet {

	private ClientConfig config = new ClientConfig();
	private Client client = ClientBuilder.newClient(config);
	private WebTarget target = client.target(getBaseUri());

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/* -- PUBLIC METHODS -- */
	
	
	public VLan getVlan(int id) {

		/**
		 * String sentence =
		 * target.path(Integer.toString(id)).request().get(String.class);
		 * 
		 * Gson gson = new GsonBuilder().setPrettyPrinting().create();
		 * 
		 * VLan vlan = gson.fromJson(sentence, VLan.class);
		 * 
		 * return vlan;
		 **/

		return getVLanFromId(id).getFirst();

	}

	public LinkedList<VLan> getVLanFromId(int id) {

		LinkedList<VLan> returnList;
		String vlanString, parameter;

		parameter = Integer.toString(id) + ";end";

		vlanString = target.path(parameter).request().get(String.class);

		Type type = new TypeToken<LinkedList<VLan>>() {}.getType();

		returnList = gson.fromJson(vlanString, type);

		return returnList;
	}

	public LinkedList<VLan> getVLanFromExperiment(Experiment exp)
			throws UnsupportedEncodingException {

		LinkedList<VLan> returnList;

		String vlanString, parameter;

		parameter = turnExperimentToVLanIdString(exp);

		vlanString = target.path(parameter).request().get(String.class);

		Type type = new TypeToken<LinkedList<VLan>>() {}.getType();

		returnList = gson.fromJson(vlanString, type);

		return returnList;

	}

	
	
	/* -- PRIVATE METHODS -- */
	
	private String turnExperimentToVLanIdString(Experiment exp)
			throws UnsupportedEncodingException {

		StringBuffer buffer = new StringBuffer();

		for (VLan vlan : exp.getVLans()) {

			buffer.append(new String(Integer.toString(vlan.getId())));

			if (buffer.length() > 0) {
				if (buffer.charAt(buffer.length()-1) != ';') {
					buffer.append(";");
				}
			}
		}

		buffer.append("end");

		return URLEncoder.encode(buffer.toString(), "UTF-8");

	}

	private URI getBaseUri() {
		return UriBuilder.fromUri("http://localhost:8080/mm.net/rest/get")
				.build();
	}

}
