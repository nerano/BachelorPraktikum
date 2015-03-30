package mm.net.implementation;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ConnectionToSwitch {

  public static final String NEW_LINE = System.getProperty("line.separator");
  private static ByteArrayOutputStream output = new ByteArrayOutputStream();
  
  static private byte[] getCommand(String command, String pw) {
    String temp = command + ConnectionToSwitch.NEW_LINE + pw;
    return temp.getBytes();
  }
  
  private static byte[] getCommand(String command) {
    String temp = command;
    return temp.getBytes();
  }
  
  public static void main(String[] args) throws JSchException, IOException {
   
    JSch jsh = new JSch();
    
    Session session = jsh.getSession("seemoo", "10.10.10.11", 22);
    
    session.setConfig("StrictHostKeyChecking", "no");
    session.setPassword("s33m00".getBytes());
    
    
    session.connect();
    
    ChannelExec channel = (ChannelExec) session.openChannel("shell");
    
   // InputStream inputStream = channel.getInputStream();
    //ByteArrayInputStream os = new ByteArrayInputStream(ConnectionToSwitch.getCommand("enable", "s33m00"));
    //ByteArrayInputStream os = new ByteArrayInputStream(ConnectionToSwitch.getCommand("show vlan"));
    //OutputStream out = new ByteArrayOutputStream();
    //out.write(ConnectionToSwitch.getCommand("show vlan"));
    //System.out.println(out.toString());
    
    //InputStream is = new ByteArrayInputStream(ConnectionToSwitch.getCommand("enable", "s33m00"));
    //channel.setOutputStream(out);
    
    channel.setCommand("show vlan");
  
    channel.setOutputStream(output);
    channel.setCommand("enable");
   
    channel.setCommand("s33m00");
    
    channel.setInputStream(System.in);
    
   

    /**channel.setInputStream(is);
    channel.connect();
    channel.setOutputStream(System.out);*/
    /*channel.setInputStream(System.in);
    channel.setOutputStream(System.out);
    //channel.setCommand("show vlan  show vlan");
    channel.connect();*/
    
    
    
   //outputStream.write(getCommand("enable", "s33m00"));
    
    
    
  // is.write("show vlan".getBytes());
    
    
    
  /**   try {
      String hello = new Shell.Plain(
          new SSH("10.10.10.11", 22, "seemoo", "s33m00")
      ).exec("echo 'Hello, World!'");
    } catch (IOException e) {
      e.printStackTrace();
    } **/
  }
}
