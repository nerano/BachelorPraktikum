package mm.power.implementation;

import java.io.IOException;

import javax.ws.rs.core.Response;

import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;
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