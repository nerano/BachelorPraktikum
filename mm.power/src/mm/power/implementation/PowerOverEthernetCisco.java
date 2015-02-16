package mm.power.implementation;

import java.io.IOException;

import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;
import mm.power.modeling.PowerSupply;

public class PowerOverEthernetCisco implements PowerSupply{

  @SuppressWarnings("unused")
private int ports;
  private String id;
  
  PowerOverEthernetCisco(int ports) {
    this.ports = ports;
  }

  public boolean turnOn(int dose) {
    System.out.println("Buchse " + dose + " wurde angeschaltet");
    return true;
  }

  public boolean turnOff(int dose) {
    System.out.println("Buchse " + dose + " wurde ausgeschaltet");
    return true;
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
public String status() throws IOException, TransferNotCompleteException {
    // TODO Auto-generated method stub
    return null;
}

@Override
public String status(int socket) throws IOException,
        TransferNotCompleteException, EntryDoesNotExistException {
    // TODO Auto-generated method stub
    return null;
}

@Override
public boolean toggle(int socket) throws IOException,
        TransferNotCompleteException, EntryDoesNotExistException {
    // TODO Auto-generated method stub
    return false;
}

@Override
public String getId() {
	return this.id;
	
}
}
