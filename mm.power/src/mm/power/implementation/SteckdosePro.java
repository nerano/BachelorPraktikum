package mm.power.implementation;


import javax.ws.rs.core.Response;

import mm.power.modeling.PowerSupply;

public class SteckdosePro implements PowerSupply {

  int dosen;
  private String id;
  SteckdosePro(int dosen) {
    this.dosen = dosen;
  }

  public Response turnOn(int dose) {
    System.out.println("PRO Dose " + dose + " wurde angeschaltet");
    return null;
  }

  public Response turnOff(int dose) {
    System.out.println("PRO Dose " + dose + " wurde ausgeschaltet");
    return null;
  }

  public boolean reset() {
    System.out.println("PRO Steckdose resettet ");
    return true;
  }


  public PowerSupply getType() {
    return null;
  }

@Override
public Response status() {
    return null;
}

@Override
public Response status(int socket) {
    return null;
}

@Override
public String getId() {
	return this.id;
}
}