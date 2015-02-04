package mm.controller.main;

import java.util.LinkedList;

import mm.controller.modeling.Experiment;

public class ExpData {

  private static LinkedList<Experiment> expList;


 protected ExpData(){
     super();
    }  
  
public LinkedList<Experiment> getExpList() {
    return expList;
}

public Experiment getById(int id){
    
    Experiment exp = null;
    
    for (Experiment experiment : expList) {
        if(experiment.getId() == id){
            exp = experiment;
        }
    }
    
    return exp;


}

public void addExp(Experiment exp){
    expList.add(exp);
}

public void remExp(Experiment exp){
    
    for (Experiment experiment : expList) {
        if(exp.equals(experiment)){
            expList.remove(experiment);
        }
    }
}


}