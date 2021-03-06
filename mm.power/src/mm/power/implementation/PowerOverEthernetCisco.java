package mm.power.implementation;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import mm.power.modeling.PowerSupply;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.core.Response;

public class PowerOverEthernetCisco implements PowerSupply{

  private String id;
  private String host;
  private String type;
  
  /* Use this if you have to log in to the Cisco switch. 
   * For test case the variables are set to a default value. */
  private String user = "seemoo";
  private int port = 22;
  
  private static final String CONFIG_MODE = "configure terminal";
  
  PowerOverEthernetCisco(String id, String host, String type) {
    this.id = id;
    this.host = host;
    this.type = type;
  }
  
  PowerOverEthernetCisco(String id, String host, String user,
      String type, int connectionPort) {
    this.id = id;
    this.host = host;
    this.type = type;
    this.user = user;
    this.port = connectionPort;
  }

  @Override
  public Response turnOn(int socket) {
    
    JSch jsh = new JSch();
    try {
      Session session = jsh.getSession(user, host, port);
      /* Do not use this in regular activity, very precarious.
       * Use the host password for regular activity.
       */
      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword("s33m00");
      session.connect();
      
      /* Use this channel to pass your commands */
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      
      /* Pass all commands with channel.setCommand(command), followed by
       * channel.connect(). */
      channel.setCommand(PowerOverEthernetCisco.CONFIG_MODE);
      channel.connect();
      
      /* If there is more than 1 switch in a row, you can toggle more than the 12
       * ports. To identify the right socket, the modulus operator is used.
       */
      int outlet = socket % 12;
      int switchNum = socket / 12;
      channel.setCommand("interface fastethernet" + switchNum + "/" + outlet);
      channel.connect();
      
      channel.setCommand("logging event power-inline-status");
      channel.connect();
      
      channel.setCommand("power inline auto");
      channel.connect();

      /* Disconnect the session */
      channel.setCommand("end");
      channel.connect();
      channel.disconnect();
      session.disconnect();
      
    } catch (JSchException e) {
      String respondeString = "Connection to host: " + this.toString() + "failed!";
      e.printStackTrace();
      return Response.status(500).entity(respondeString).build();
    }
    
    return Response.ok("PoE for socket: " + socket + " is on.").build();
  }

  @Override
  public Response turnOff(int socket) {
    
    JSch jsh = new JSch();
    try {
      Session session = jsh.getSession(user, host, port);
      /* Do not use this in regular activity, very precarious.
       * Use the host password for regular activity.
       */
      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword("s33m00");
      session.connect();
      
      /* Use this channel to pass your commands */
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      
      /* Pass all commands with channel.setCommand(command), followed by
       * channel.connect(). */
      channel.setCommand(PowerOverEthernetCisco.CONFIG_MODE);
      channel.connect();
      
      /* If there is more than 1 switch in a row, you can toggle more than the 12
       * ports. To identify the right socket, the modulus operator is used.
       */
      int outlet = socket % 12;
      int switchNum = socket / 12;
      channel.setCommand("interface fastethernet" + switchNum + "/" + outlet);
      channel.connect();
      
      channel.setCommand("logging event power-inline-status");
      channel.connect();
      
      channel.setCommand("power inline nerver");
      channel.connect();
      
      /* Disconnect the session */
      channel.setCommand("end");
      channel.connect();
      channel.disconnect();
      session.disconnect();

    } catch (JSchException e) {
      String respondeString = "Connection to host: " + this.toString() + "failed!";
      e.printStackTrace();
      return Response.status(500).entity(respondeString).build();
    }
    
    return Response.ok("PoE for socket: " + socket + " is off.").build();
  }

  @Override
  public Response status() {
    
    JSch jsh = new JSch();
    String responseString = "";
    try {
      Session session = jsh.getSession(user, host, port);
      /* Do not use this in regular activity, very precarious.
       * Use the host password for regular activity.
       */
      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword("s33m00");
      session.connect();
      
      /* Use this channel to pass your commands */
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      
      /* Pass all commands with channel.setCommand(command), followed by
       * channel.connect(). */
      channel.setCommand(PowerOverEthernetCisco.CONFIG_MODE);
      channel.connect();
      
      /* Get status of all nodes. */
      channel.setCommand("show power inline");
      OutputStream os = channel.getOutputStream();
      channel.connect();
      
      /* Use outputStream to get the PoE status. */
      responseString = os.toString();
      //TODO get just the information for PoE status, should be more right now.
      
      /* Disconnect the session */
      channel.setCommand("end");
      channel.connect();
      channel.disconnect();
      session.disconnect();
      
    } catch (JSchException e) {
      String response = "Connection to host: " + this.toString() + " failed!";
      e.printStackTrace();
      return Response.status(500).entity(response).build();
    } catch (IOException e) {
      String response = "Connection to host: " + this.toString() + " throws IOException!";
      e.printStackTrace();
      return Response.status(500).entity(response).build();
    }
    
    return Response.ok(responseString).build();
  }

  @Override
  public Response status(int socket) {
    
    JSch jsh = new JSch();
    String responseString = "";
    
    try {
      Session session = jsh.getSession(user, host, port);
      /* Do not use this in regular activity, very precarious.
       * Use the host password for regular activity.
       */
      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword("s33m00");
      session.connect();
      
      /* Use this channel to pass your commands */
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      
      /* Pass all commands with channel.setCommand(command), followed by
       * channel.connect(). */
      channel.setCommand(PowerOverEthernetCisco.CONFIG_MODE);
      channel.connect();
      
      /* If there is more than 1 switch in a row, you can toggle more than the 12
       * ports. To identify the right socket, the modulus operator is used.
       */
      int outlet = socket % 12;
      int switchNum = socket / 12;
      channel.setCommand("show power inline fastethernet" + switchNum + "/" + outlet);
      OutputStream os = channel.getOutputStream();
      channel.connect();
      
      /* Use outputStream to get the PoE status. */
      responseString = os.toString();
      //TODO get just the information for PoE status, should be more right now.
      
      /* Disconnect the session */
      channel.setCommand("end");
      channel.connect();
      channel.disconnect();
      session.disconnect();
      
    } catch (JSchException e) {
      String responde = "Connection to host: " + this.toString() + " failed!";
      e.printStackTrace();
      return Response.status(500).entity(responde).build();
    } catch (IOException e) {
      String response = "Connection to host: " + this.toString() + " throws IOException!";
      e.printStackTrace();
      return Response.status(500).entity(response).build();
    }
    
    return Response.ok(responseString).build();
  }

  @Override
  public String getId() {
    return this.id;
  }
  
  public String getType() {
    return this.type;
  }
  
  @Override
  public String toString() {

    StringBuffer sb = new StringBuffer();
    sb.append("\n");
    sb.append("ID: " + this.id + " \n");
    sb.append("Type: " + this.type + "\n");
    sb.append("Host " + this.host + "\n");
    return sb.toString();
  }
}
