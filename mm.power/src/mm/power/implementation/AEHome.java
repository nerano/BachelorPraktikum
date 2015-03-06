package mm.power.implementation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.ws.rs.core.Response;

import mm.power.modeling.PowerSupply;

/**
 * This class describes methods, inherited from the PowerSupply interface, which
 * are suited to control an "Anel-Elektronik HOME" outlet. For this purpose a IP
 * and the User and password is needed. If both are correct, there are two
 * options: You can establish a connection via the UDP protocl or the HTTP
 * protocol. If you are going to use the UDP protocol you have to specify the
 * corresponding port from the outlet.
 * 
 *
 */
public class AEHome implements PowerSupply {

	private static final int socket = 3; // Three sockets on a Anel Elektronik
											// Home version
	private String id;
	private String host, type;
	private String states;

	private String URL_STRG;
	private String URL_CTRL;
	private String USER_BASE64;
	// private String TEST_USER = "adminanel";

	/**
	 * Constructor for a power outlet "Anel Elektronik Home", which posses 3
	 * toggable sockets.
	 * 
	 * @param id
	 *            field for identifying a outlet
	 * @param type
	 *            type of the PowerSupply
	 * @param host
	 *            hostname or IP of the PowerSupply
	 */
	public AEHome(String id, String type, String host) {

		this.id = id;

		this.type = type;
		this.host = host;

		URL_STRG = "http://" + host + "/strg.cfg";
		URL_CTRL = "http://" + host + "/ctrl.htm";
		USER_BASE64 = "Basic YWRtaW46YW5lbA==";
	}

	/**
	 * Establishes a HTTP connection to the power outlet to get the
	 * strg.cfg to extract the states of the sockets located on the outlet 
	 * Then a ResponseObject is returned, which contains a String with the states
	 * in the following form: "[0/1][0/1][0/1]". If some error occurred the status of the
	 * response is 500(Internal Error) otherwise it is 200 (OK).
	 * 
	 * @return Response with the states of all three sockets
	 */
	private Response getStates() {

		String states, responseString;
		StringBuffer response = new StringBuffer();
	
		try {
			
			URL url = new URL(URL_STRG);

			HttpURLConnection connection = null;

			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");

			connection.setRequestProperty("Authorization", USER_BASE64);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		
			String[] variables;

			variables = response.toString().split(";");

			if (variables[variables.length - 2].equals("end")) {
				states = variables[20] + variables[21] + variables[22];
			} else {
				responseString = "TransferNotComplete in PowerSupply: "
						+ this.toString() + "Response: " + response + "%n";
				return Response.status(500).entity(responseString).build();
			}

			return Response.status(200).entity(states).build();

		
		} 
		
		catch (java.net.UnknownHostException e) {
			responseString = "UnknownHostException in PowerSupply: "
					+ this.toString();
			e.printStackTrace();
			
			return Response.status(500).entity(responseString).build();
		
		}   catch (MalformedURLException e) {
			responseString = "MalformedURLException in PowerSupply: "
					+ this.toString();
			e.printStackTrace();
			return Response.status(500).entity(responseString).build();
		
		} catch (ProtocolException e) {
			responseString = "ProtocolException in PowerSupply: "
					+ this.toString();
			e.printStackTrace();
			return Response.status(500).entity(responseString).build();
		
		} catch (IOException e) {
			responseString = "IOException in PowerSupply: " + this.toString();
			e.printStackTrace();
			return Response.status(500).entity(responseString).build();
		} 

	}

	/**
	 * This method toggles the given socket. So if Socket was 0 it is 1 after
	 * and vice versa. To do this it establishes a HTTP connection.
	 * 
	 * @param socket
	 *            A number from the Range of {1, 2 , 3}
	 * @return boolean true if the toggle was successful and false if not
	 * @throws ProtocolException .
	 * @throws IOException .
	 */
	public Response toggle(int socket)  {

		try {
		
		if (socket > AEHome.socket) {
			String responseString = "Socketnumber exceeds existing sockets on: " + this.toString();
			return Response.status(500).entity(responseString).build();
		}
		 URL url = new URL(URL_CTRL);
		
		HttpURLConnection connection;
		
		connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", "MM");

		connection.setRequestProperty("Content-type", "text/plain");
		connection.setRequestProperty("Authorization", USER_BASE64);

		String urlParameters = "F" + (socket - 1) + "=T";

		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		
		int responseCode = connection.getResponseCode();
		
		if (responseCode == 200) {
			return Response.ok().build();
		} else {
			return Response.status(500).build();
		}
		
		}
		
		catch (MalformedURLException e) {
			String responseString = "MalformedURLException in PowerSupply: "
				+ this.toString();
		e.printStackTrace();
		return Response.status(500).entity(responseString).build();
	
		} catch (ProtocolException e) {
			String responseString = "ProtocolException in PowerSupply: "
				+ this.toString();
		e.printStackTrace();
		return Response.status(500).entity(responseString).build();
	
		} catch (IOException e) {
			String responseString = "IOException in PowerSupply: " + this.toString();
		e.printStackTrace();
		return Response.status(500).entity(responseString).build();
		}
	

	}

	/**
	 * If this method is called the given socket is turned off. If the socket is
	 * already turned off the state does not change. If the socket could not
	 * turned off false is returned.
	 * 
	 * @param socket
	 *            The socket to turn off, one from the following values[1/2/3]
	 * @return true if the socket is on afterwards, false if not.
	 * @throws IOException .
	 * @throws TransferNotCompleteException
	 * 
	 */
	public Response turnOff(int socket)  {
		
		
		if (socket > AEHome.socket) {
			String responseString = "Socketnumber exceeds existing sockets on: " + this.toString();
			return Response.status(500).entity(responseString).build();
		}

		Response stateResponse = status(socket);
		
		if(stateResponse.getStatus() != 200) {
			return stateResponse;
		}
		
		String state = (String) stateResponse.getEntity();
		
		Response r;
		
		switch (state) {
		case "1":
			r = toggle(socket);
			if(r.getStatus() == 200) {
				return Response.ok().build();
			} else {
				return r;
			}
		case "0":
			return Response.ok().build();
		default:
			return Response.status(500).build();
	
		}


	}

	/**
	 * If this method is called the given socket is turned on. If the socket is
	 * already turned on the state does not change. If the socket could not
	 * turned on false is returned.
	 * 
	 * @param socket
	 *            The socket to turn on, one from the following values[1/2/3]
	 * @return true if the socket is on afterwards, false if not.
	 * @throws IOException .
	 * @throws TransferNotCompleteException
	 * 
	 */
	public Response turnOn(int socket) {

		
		if (socket > AEHome.socket) {
			String responseString = "Socketnumber exceeds existing sockets on: " + this.toString();
			return Response.status(500).entity(responseString).build();
		}
		
		Response stateResponse = status(socket);
		
		if(stateResponse.getStatus() != 200) {
			return stateResponse;
		}
		
		String state = (String) stateResponse.getEntity();
		
		Response r;
		
		
		switch (state) {
		case "0":
			r = toggle(socket);
			if(r.getStatus() == 200) {
				return Response.ok().build();
			} else {
				return r;
			}
		case "1":
			return Response.ok().build();
		default:
			return Response.status(500).build();
	
		}
	}

	/**
	 * Returns states from all sockets of the outlet in the form
	 * "[0/1][0/1][0/1]", where 0 is off and 1 is on.
	 * 
	 * @return A String with all states from the sockets
	 * @throws ProtocolException .
	 * @throws IOException .
	 * @throws TransferNotCompleteException .
	 */
	public Response status() {
		return getStates();
	}

	/**
	 * Returns the state of a given socket "[0/1]" where 0 is off and 1 is on.
	 * 
	 * @param socket
	 *            one from the following values [1/2/3]
	 * @return A String with the state of the socket
	 * @throws ProtocolException .
	 * @throws IOException .
	 * @throws TransferNotCompleteException
	 * 
	 */
	public Response status(int socket) {

		String responseString, sentence;
		if (socket > AEHome.socket) {
			responseString = "Socketnumber exceeds existing sockets on: " + this.toString();
			return Response.status(500).entity(responseString).build();
		}
		
		Response r = getStates();
		
		if(r.getStatus() == 200){
			states = (String) r.getEntity();
		} else {
			return r;
		}

		if (states.charAt(0) == '0' || states.charAt(0) == '1') {

			sentence = states.substring((socket - 1), (socket - 1) + 1);
			return Response.ok(sentence).build();
		
		} else {
			return Response.status(500).entity("StateString was not valid in:" + this.toString() + "StateString: " + states).build();
		}

		

	}

	/**
	 * If this method is called the given socket is turned on. If the socket is
	 * already turned on the state does not change. If the socket does not exist
	 * on the hardware false is returned. This method uses a UDP Connection
	 * 
	 * @param socket
	 *            The socket to turn on, one from the following values[1/2/3]
	 * @return True if socket is turned on or was already turned on. False if
	 *         the socket does not exist
	 * @throws IOException .
	 */
	/**
	 * public boolean turnOnUdp(int socket) throws IOException,
	 * SocketDoesNotExistException {
	 * 
	 * if (socket > AEHome.socket) { throw new SocketDoesNotExistException(
	 * "Socketnumber exceeds existing sockets on: " + this.id); }
	 * 
	 * String sentence = "Sw_on" + socket + TEST_USER; byte[] sendData =
	 * sentence.getBytes();
	 * 
	 * DatagramPacket packet = new DatagramPacket(sendData, sendData.length, ip,
	 * port);
	 * 
	 * 
	 * udpSocket.send(packet);
	 * 
	 * return true; }
	 **/

	/**
	 * If this method is called the given socket is turned off. If the socket is
	 * already turned off the state does not change. If the socket does not
	 * exist on the hardware false is returned. This method uses a UDP
	 * connection.
	 *
	 * @param socket
	 *            The socket to turn off, one from the following values[1/2/3]
	 * @return True if socket is turned off or was already turned off. False if
	 *         the socket does not exist
	 * @throws IOException .
	 */
	/**
	 * public boolean turnOffUdp(int socket) throws IOException,
	 * SocketDoesNotExistException {
	 * 
	 * if (socket > AEHome.socket) { throw new SocketDoesNotExistException(
	 * "Socketnumber exceeds existing sockets on : " + this.id); }
	 * 
	 * String sentence = "Sw_off" + socket + TEST_USER; byte[] sendData =
	 * sentence.getBytes();
	 * 
	 * DatagramPacket packet = new DatagramPacket(sendData, sendData.length, ip,
	 * port);
	 * 
	 * udpSocket.send(packet);
	 * 
	 * return true;
	 * 
	 * }
	 **/

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String toString() {

		StringBuffer b = new StringBuffer();
		b.append("\n");
		b.append("ID: " + this.id + " \n");
		b.append("Type: " + this.type + "\n");
		b.append("Host " + this.host + "\n");
		return b.toString();
	}

}
