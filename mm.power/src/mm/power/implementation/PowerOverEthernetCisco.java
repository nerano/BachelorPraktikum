package mm.power.implementation;



import javax.ws.rs.core.Response;

import mm.power.modeling.PowerSupply;

public class PowerOverEthernetCisco implements PowerSupply{

  @SuppressWarnings("unused")
private int ports;
  private String id;
  
  PowerOverEthernetCisco(int ports) {
    this.ports = ports;
  }

  public Response turnOn(int dose) {
    System.out.println("Buchse " + dose + " wurde angeschaltet");
    return null;
  }

  public Response turnOff(int dose) {
    System.out.println("Buchse " + dose + " wurde ausgeschaltet");
    return null;
  }

  public boolean reset() {
    System.out.println("Buchse PoE Reset ");
    return true;
  }

  public PowerSupply getType() {
    // TODO Auto-generated method stub
    return null;
  }

@Override
public Response status() {
    // TODO Auto-generated method stub
    return null;
}

@Override
public Response status(int socket) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public Response toggle(int socket) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public String getId() {
	return this.id;
	
}
}
