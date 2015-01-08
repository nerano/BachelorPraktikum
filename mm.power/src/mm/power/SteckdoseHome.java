package mm.power;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SteckdoseHome implements PowerSupply {

	private int ID;
	private InetAddress ip;
	private DatagramSocket socket;
	private int port;

	public SteckdoseHome(int stecker, int ID, InetAddress ip,
			DatagramSocket socket, int port) {

		this.ID = ID;
		this.ip = ip;
		this.socket = socket;
		this.port = port;

	}

	public boolean turnOn(int dose) throws IOException {

		String sentence = "Sw_on" + dose + "adminanel";
		byte[] sendData = sentence.getBytes();

		DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
				ip, port);
		socket.send(packet);

		return true;
	}

	public boolean turnOff(int dose) throws IOException {

		String sentence = "Sw_off" + dose + "adminanel";
		byte[] sendData = sentence.getBytes();

		DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
				ip, port);
		socket.send(packet);

		return true;
	}

	public boolean reset() {
		System.out.println("Steckdose ");
		return true;
	}

	public PowerSupply getType() {
		// TODO Auto-generated method stub
		return null;

	}
}