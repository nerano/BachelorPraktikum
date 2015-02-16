package mm.power.main;

import java.util.LinkedList;




import mm.power.implementation.AEHome;
import mm.power.modeling.PowerSupply;

public class PowerData {


	private static LinkedList<PowerSupply> POWERSUPPLY_LIST;



	public PowerData(){
		POWERSUPPLY_LIST = new LinkedList<PowerSupply>();
		
	}
	
	public PowerData(LinkedList<PowerSupply> powerList){
		POWERSUPPLY_LIST = powerList;
	}

	public LinkedList<PowerSupply> getPowerList() {
	    return POWERSUPPLY_LIST;
	  }

	  
	  /**
	   * Returns the PowerSupply with the given ID.
	   * @param id ID of the PowerSupply
	   * @return PowerSupply with the ID, null if no PowerSupply was found
	   */
	  static public PowerSupply getById(String id){
	    
		  PowerSupply ps = null;
	    for (PowerSupply powerSupply : POWERSUPPLY_LIST) {
	        if(powerSupply.getId().equals(id)){
	            ps = powerSupply;
	        }
	    }
	    
	   
	    return ps;
	  }

	  /**
	   * Adds a PowerSupply to the global data.
	   * @param ps PowerSupply to add
	   */
	  static public void addPs(PowerSupply ps){
	    POWERSUPPLY_LIST.add(ps);
	  }
	  
	  /**
	   * Removes a PowerSupply from the global data.
	   * @param ps PowerSupply to remove
	   * @return bool true if PowerSupply was in the list and was removed, false if PowerSupply was not in the list
	   */
	  public static boolean removePs(PowerSupply ps){
	    
		  boolean bool = false;
		  for (PowerSupply powerSupply : POWERSUPPLY_LIST) {
	        if(ps == powerSupply){
	            POWERSUPPLY_LIST.remove(powerSupply);
	            bool = true;
	        }
	    }
		  return bool;
	  }

	  
	  /**
	   * Removes a PowerSupply from the global data
	   * @param id ID of the PowerSupply to remove
	   * @return bool true if PowerSupply was in the list and was removed, false if PowerSupply was not in the list
	   */
	  public static boolean removePs(String id){
		  boolean bool = false;
		  
		  for (PowerSupply ps : POWERSUPPLY_LIST) {
			if(ps.getId().equals(id)){
				POWERSUPPLY_LIST.remove(ps);
				bool = true;
			}
		}
		  
		  return bool;
	  }
	  
	  
	  
	  /**
	   * Returns if a PowerSupply with a given ID exists in the global data
	   * @param id PowerSupply ID to look for
	   * @return false if the PowerSupply does not exist, true if it does
	   */
	  public static boolean exists(String id){
		 
		 boolean bool = false;
		 
		 for (PowerSupply ps : POWERSUPPLY_LIST) {
			if(ps.getId().equals(id)){
				bool = true;
			}
		}
		 return bool;
	  }
}
