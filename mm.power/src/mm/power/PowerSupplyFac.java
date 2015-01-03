package mm.power;

public class PowerSupplyFac {

  static PowerSupply getByModel(int model) {
    PowerSupply power = null;
    if (model == 1) {
      power = new SteckdoseHOME(8);
    }
    if (model == 2){
      power = new SteckdosePRO(8);
    }  
    if (model == 3){
      power = new PowerOverEthernetCisco(8);
    }
    if (power == null) {
      System.out.println("MODEL NICHT GEFUNDEN");
    }
    return power;
  }
}