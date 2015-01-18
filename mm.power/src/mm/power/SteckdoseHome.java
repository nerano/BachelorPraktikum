package mm.power;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SteckdoseHome implements PowerSupply {

  private int id;
  private InetAddress ip;
  private DatagramSocket socket;
  private int port;

  /**
   * initiate the power sockets.
   * @param stecker ..
   * @param id ...
   * @param ip ...
   * @param socket ...
   * @param port ...
   */
  public SteckdoseHome(int stecker, int id, InetAddress ip,
      DatagramSocket socket, int port) {

    this.id = id;
    this.ip = ip;
    this.socket = socket;
    this.port = port;

  }

  /** turns on a power socket.
   * @return true if turning on was successful, otherwise false
   */
  public boolean turnOn(int dose) throws IOException {

    String sentence = "Sw_on" + dose + "adminanel";
    byte[] sendData = sentence.getBytes();

    DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
        ip, port);
    socket.send(packet);

    return true;
  }

  /** turns off a power socket.
   * @return true if turning off was successful, otherwise false
   */
  public boolean turnOff(int dose) throws IOException {

    String sentence = "Sw_off" + dose + "adminanel";
    byte[] sendData = sentence.getBytes();

    DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
        ip, port);
    socket.send(packet);

    return true;
  }

  public boolean reset() {
    System.out.println("Steckdose");
    return true;
  }

  /**
   * get type of power supply.
   * @return the type of the power supply
   */
  public PowerSupply getType() {
    // TODO Auto-generated method stub
    return null;
  }
}