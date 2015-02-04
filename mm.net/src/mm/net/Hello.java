package mm.net;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class Hello {

  private String[][] vLan = {{"1", "05", "04", "03", "02", "01"}, {"2", "06", "07", "08", "09", "00"}};
  
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayPlainTextHello() {
    return "Hello there!";
  }
  
  @GET
  @Path("/VLAN1")
  @Produces(MediaType.TEXT_PLAIN)
  public String answerToVlan() {
    String param = "";
    for(int i = 1; i < this.vLan[0].length; i++) {
      param += vLan[0][i]+" ";
    }
    return param;
  }
  
  /* In der ResponseKlasse (in unserem Fall der Controller) werden die GET Methoden, in den mm.xx Klassen, �ber einen Befehl �hnlich zu diesem
   target.path("rest").path("hello").request().accept(MediaType.TEXT_PLAIN).get(String.class)
   aufgerufen. Die entsprechenden Methoden errechnen die Ergebnisse bzw. schicken einen weiteren Request an die n�chste Schicht und geben
   die Ergebnisse �ber einen einfachen return Befehl zur�ck. Durch den return bekommt der Controller die Ergebnisse �bergeben und kann diese 
   �ber das Webinterface ausgeben... */
}
