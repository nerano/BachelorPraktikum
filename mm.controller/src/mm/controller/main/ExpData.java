package mm.controller.main;

import java.util.LinkedList;

import mm.controller.modeling.Experiment;

public class ExpData {

  private static LinkedList<Experiment> expList;


  protected ExpData(){
     ExpData.expList = new LinkedList<Experiment>();
   }  
  
  public LinkedList<Experiment> getExpList() {
    return expList;
  }

  static public Experiment getById(int id){
    
    Experiment exp = null;
    
    for (Experiment experiment : expList) {
        if(experiment.getId() == id){
            exp = experiment;
        }
    }
    
    return exp;


}

static public void addExp(Experiment exp){
    expList.add(exp);
}

public void removeExp(Experiment exp){
    
    for (Experiment experiment : expList) {
        if(exp.equals(experiment)){
            expList.remove(experiment);
        }
    }
}

public static boolean exists(int id){
	 
	 boolean bool = false;
	 
	 for (Experiment experiment : expList) {
		if(experiment.getId() == id){
			bool = true;
		}
	}
	 
	 return bool;
	 
}

}
