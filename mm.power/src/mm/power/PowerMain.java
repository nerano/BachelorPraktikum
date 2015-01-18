package mm.power;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/powermain")
public class PowerMain {

  /**
   * 
   * @param outlet
   * @param state
   * @return
   * @throws SocketException
   * @throws UnknownHostException
   */
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  public String turnTest(@QueryParam("outlet") int outlet, @QueryParam("state") String state)
      throws SocketException, UnknownHostException {

    DatagramSocket apiSocket = new DatagramSocket(8340);

    byte[] ipByte = { (byte) 192, (byte) 168, (byte) 178, 21 };

    try {
      InetAddress ip = java.net.InetAddress.getByAddress(ipByte);
      System.out.println("IPAdress : " + ip.toString());

      PowerSupply homeTest = new SteckdoseHome(4, 17, ip, apiSocket, 75);

      homeTest.turnOn(2);
      // steckdose.turnOn(3);
      // steckdose.turnOn(4);

    } catch (IOException e) {
      System.out.println("IP Address of illegal length");
      e.printStackTrace();
    }

    apiSocket.close();

    return outlet + "turned" + state;

  }
}