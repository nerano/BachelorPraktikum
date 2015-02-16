package mm.power.implementation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import mm.power.exceptions.SocketDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;
import mm.power.modeling.PowerSupply;

/**
 * This class describes methods, inherited from the PowerSupply interface, which are suited
 * to control an "Anel-Elektronik HOME" outlet. For this purpose a IP and the User
 * and password is needed. If both are correct, there are two options: You can establish
 * a connection via the UDP protocl or the HTTP protocol. If you are going to use the
 * UDP protocol you have to specify the corresponding port from the outlet. 
 * @author julian
 * 
 *
 */
public class AEHome implements PowerSupply {

  private static final int socket = 3; // Three sockets on a Anel Elektronik Home version
  private String id;
  //private InetAddress ip;
  private String host;
  private String type;
  private long lastStatus = 0l;
  private String states;

  private final String TEST_URL_STRG = "http://192.168.178.21/strg.cfg";
  private static final String TEST_URL_CTRL = "http://192.168.178.21/ctrl.htm";
  private static final String TEST_USER_BASE64 = "Basic YWRtaW46YW5lbA==";
  private static final String TEST_USER = "adminanel";
  private final long CACHE_TIME = 5000;
  /**
   * Constructor for a power outlet "Anel Elektronik Home", which posses 3 toggable 
   * sockets.
   * @param id field for identifying a outlet
   * @param type type of the PowerSupply
   * @param host hostname or IP of the PowerSupply
   */
  public AEHome(String id, String type, String host) {

    this.id = id;
    this.type= type;
    this.host = host;
    
  }

    /**
     * This method establishes a http connection to the power outlet to get the
     * strg.cfg to extract the states of the sockets located on the outlet Then
     * a String in the form "[0/1][0/1][0/1]" is returned, where 0 is off and 1
     * is on and the first number describes the state of the first socket and so
     * forth.
     * 
     * @return String with the states of all three sockets
     * @throws ProtocolException .
     * @throws TransferNotCompleteException .
     */
  private String getStates() throws IOException, ProtocolException,
            MalformedURLException, TransferNotCompleteException {

    
    String states;
    URL url = new URL(TEST_URL_STRG);
    HttpURLConnection connection = null;

    connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("GET");

    connection.setRequestProperty("Authorization", TEST_USER_BASE64);
      
    BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));

    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
   
    String[] variables = new String[59];
    variables = response.toString().split(";");
       
    if (variables[58].equals("end")) {
      states = variables[20] + variables[21] + variables[22];
    } else {
      throw new TransferNotCompleteException("Transmission not complete on Outlet " + this.id);
    }

    return states;

  }

    /**
     * This method toggles the given socket. So if Socket was 0 it is 1 after
     * and vice versa. To do this it establishes a HTTP connection.
     * 
     * @param socket A number from the Range of {1, 2 , 3}
     * @return boolean true if the toggle was successful and false if not
     * @throws ProtocolException .
     * @throws IOException .
     */
  public boolean toggle(int socket) throws IOException, MalformedURLException, 
        ProtocolException, SocketDoesNotExistException {

    if (socket > AEHome.socket) {
      throw new SocketDoesNotExistException("Socketnumber exceeds existing sockets on: " + this.id);
    }
        
    URL url = new URL(TEST_URL_CTRL);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("POST");
    connection.setRequestProperty("User-Agent", "MM");

    connection.setRequestProperty("Content-type", "text/plain");
    connection.setRequestProperty("Authorization", TEST_USER_BASE64);

    String urlParameters = "F" + (socket - 1) + "=T";

    connection.setDoOutput(true);
    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
    wr.writeBytes(urlParameters);
    wr.flush();
    wr.close();

    int responseCode = connection.getResponseCode();

    lastStatus = 0l;
    if (responseCode == 200) {
      return true;
    } else {
      return false;
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
  public boolean turnOff(int socket) throws IOException, 
      TransferNotCompleteException, SocketDoesNotExistException {
    if (socket > AEHome.socket) {
      throw new SocketDoesNotExistException("Socketnumber exceeds existing sockets on: " + this.id);
    }
        
        
    String state = status(socket);
    System.out.println(state);
    boolean bool = false;
    switch (state) {
      case "1":
        bool = toggle(socket);
        break;
      case "0":
        bool = true;
        break;
      default:
        break;
    }

    lastStatus = 0l;
    return bool;

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
  public boolean turnOn(int socket) throws IOException, 
      TransferNotCompleteException, SocketDoesNotExistException {

    if (socket > AEHome.socket) {
      throw new SocketDoesNotExistException("Socketnumber exceeds existing sockets on: " + this.id);
    }
        
    boolean bool = false;
    String sentence = status(socket);

    switch (sentence) {
      case "0":
        bool = toggle(socket);
        break;
      case "1":
        bool = true;
        break;

      default:
        break;
    }

    lastStatus = 0l;
    return bool;

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
  public String status() throws ProtocolException, 
     MalformedURLException, IOException, TransferNotCompleteException {

    if (java.lang.System.currentTimeMillis() - lastStatus > CACHE_TIME) {
    	 states = getStates();
    	 lastStatus = java.lang.System.currentTimeMillis();
    	 return states;
    } else {
		return states;
	}

  }

    /**
     * Returns the state of a given socket "[0/1]" where 0 is off
     * and 1 is on.
     * 
     * @param socket
     *            one from the following values [1/2/3]
     * @return A String with the state of the socket
     * @throws ProtocolException .
     * @throws IOException .
     * @throws TransferNotCompleteException 
     * 
     */
  public String status(int socket) throws ProtocolException, 
      MalformedURLException, IOException, TransferNotCompleteException, SocketDoesNotExistException 
  {

    if (socket > AEHome.socket) {
      throw new SocketDoesNotExistException("Socketnumber exceeds existing sockets on: " + this.id);
    }

    if (java.lang.System.currentTimeMillis() - lastStatus > CACHE_TIME) {
   	 states = getStates();
   	 lastStatus = java.lang.System.currentTimeMillis();
    } 

    String sentence = null; 
   
    if (states.charAt(0) == '0' || states.charAt(0) == '1') {

      sentence = states.substring((socket - 1), (socket - 1) + 1);

    }
    
    
    return sentence;

  }

    /**
     * If this method is called the given socket is turned on. If the socket is
     * already turned on the state does not change. If the socket does not exist
     * on the hardware false is returned.
     * This method uses a UDP Connection
     * 
     * @param socket
     *            The socket to turn on, one from the following values[1/2/3]
     * @return True if socket is turned on or was already turned on. False if
     *         the socket does not exist
     * @throws IOException .
     */
/**  public boolean turnOnUdp(int socket) throws IOException, SocketDoesNotExistException {

    if (socket > AEHome.socket) {
      throw new SocketDoesNotExistException("Socketnumber exceeds existing sockets on: " + this.id);
    }

    String sentence = "Sw_on" + socket + TEST_USER;
    byte[] sendData = sentence.getBytes();

    DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
                ip, port);


    udpSocket.send(packet);
        
    return true;
  } **/

    /**
     * If this method is called the given socket is turned off. If the socket is
     * already turned off the state does not change. If the socket does not
     * exist on the hardware false is returned.
     * This method uses a UDP connection.
     *
     * @param socket
     *            The socket to turn off, one from the following values[1/2/3]
     * @return True if socket is turned off or was already turned off. False if
     *         the socket does not exist
     * @throws IOException .
     */
  /**public boolean turnOffUdp(int socket) throws IOException, SocketDoesNotExistException {

    if (socket > AEHome.socket) {
      throw 
           new SocketDoesNotExistException("Socketnumber exceeds existing sockets on : " + this.id);
    }

    String sentence = "Sw_off" + socket + TEST_USER;
    byte[] sendData = sentence.getBytes();

    DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
                ip, port);
        
    udpSocket.send(packet);

    return true;

  } **/

 
  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }


}
