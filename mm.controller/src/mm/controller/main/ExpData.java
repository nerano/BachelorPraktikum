package mm.controller.main;

import java.util.LinkedList;

import mm.controller.modeling.Experiment;
/**
 * Holds a static List with all experiments. Works as the central data point for the experiments
 * with various methods of manipulation
 * @author john
 *
 */
public class ExpData {
	
	/* !-- Global List of Experiments --! */
  private static LinkedList<Experiment> expList;

  protected ExpData(){
     ExpData.expList = new LinkedList<Experiment>();
   }  
  
  protected ExpData(LinkedList<Experiment> expList){
	     ExpData.expList = expList;
	   } 
  
  
  
  
  public LinkedList<Experiment> getExpList() {
    return expList;
  }

  
  /**
   * Returns the Experiment with the given ID.
   * @param id ID of the Experiment
   * @return Experiment with the ID, null if no experiment was found
   */
  static public Experiment getById(int id){
    
    Experiment exp = null;
    for (Experiment experiment : expList) {
        if(experiment.getId() == id){
            exp = experiment;
        }
    }
    return exp;
  }

  /**
   * Adds a experiment to the global data.
   * @param exp experiment to add
   */
  static public void addExp(Experiment exp){
    expList.add(exp);
  }
  
  /**
   * Removes a experiment from the global data.
   * @param exp experiment to remove
   * @return bool true if experiment was in the list and was removed, false if experiment was not in the list
   */
  public static boolean removeExp(Experiment exp){
    
	  boolean bool = false;
	  for (Experiment experiment : expList) {
        if(exp.equals(experiment)){
            expList.remove(experiment);
            bool = true;
        }
    }
	  return bool;
  }

  
  /**
   * Removes a experiment from the global data
   * @param id ID of the experiment to remove
   * @return bool true if experiment was in the list and was removed, false if experiment was not in the list
   */
  public static boolean removeExp(int id){
	  boolean bool = false;
	  
	  for (Experiment experiment : expList) {
		if(experiment.getId() == id){
			expList.remove(experiment);
			bool = true;
		}
	}
	  
	  
	  return bool;
  }
  
  
  
  /**
   * Returns if a experiment with a given ID exists in the global data
   * @param id experiment ID to look for
   * @return false if the experiment does not exist, true if it does
   */
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
