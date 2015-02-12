package mm.power.implementation;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;

@Path("/powermain")
public class PowerMain {

  /**
   * 
   * @param outlet
   * @param state
   * @return
   * @throws SocketException
   * @throws UnknownHostException
 * @throws EntryDoesNotExistException 
 * @throws TransferNotCompleteException 
   */
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  public String turnTest(@QueryParam("outlet") int outlet, @QueryParam("state") String state)
      throws SocketException, UnknownHostException, TransferNotCompleteException, EntryDoesNotExistException {

    DatagramSocket apiSocket = new DatagramSocket(8340);

    byte[] ipByte = { (byte) 192, (byte) 168, (byte) 178, 21 };

    try {
      InetAddress ip = java.net.InetAddress.getByAddress(ipByte);
      System.out.println("IPAdress : " + ip.toString());

     // PowerSupply homeTest = new AEHome(17, ip, apiSocket, 75);

    //  homeTest.turnOn(3);
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