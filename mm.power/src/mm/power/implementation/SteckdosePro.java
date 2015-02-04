package mm.power.implementation;

import java.io.IOException;

import mm.power.exceptions.EntryDoesNotExistException;
import mm.power.exceptions.TransferNotCompleteException;

public class SteckdosePro implements PowerSupply {

  int dosen;

  SteckdosePro(int dosen) {
    this.dosen = dosen;
  }

  public boolean turnOn(int dose) {
    System.out.println("PRO Dose " + dose + " wurde angeschaltet");
    return true;
  }

  public boolean turnOff(int dose) {
    System.out.println("PRO Dose " + dose + " wurde ausgeschaltet");
    return true;
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
}