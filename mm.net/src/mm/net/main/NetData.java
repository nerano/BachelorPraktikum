package mm.net.main;

import java.util.HashMap;
import java.util.LinkedList;

import mm.net.modeling.NetComponent;
import mm.net.modeling.VLan;

public class NetData {
	
	private static LinkedList<VLan> VLAN_LIST = new LinkedList<VLan>();
	private static HashMap<String, NetComponent> NETCOMPONENT_LIST = new HashMap<String, NetComponent>();
	// private static LinkedList<Config> CONFIG_LIST = new LinkedList<Config>();
	protected NetData(HashMap<String, NetComponent> list ) {
		NETCOMPONENT_LIST = list;

	}

	public LinkedList<VLan> getVLanList() {
		return VLAN_LIST;
	}

	/**
	 * Returns the VLan with the given ID.
	 * 
	 * @param id
	 *            ID of the requested VLan
	 * @return VLan with the ID, null if no VLan was found
	 */
	static public VLan getById(int id) {

		VLan v = null;
		for (VLan vlan : VLAN_LIST) {
			if (vlan.getId() == id) {
				v = vlan;
			}
		}
		return v;
	}

	/**
	 * Adds a VLan to the global data.
	 * 
	 * @param ps
	 *            VLan to add
	 */
	static public void addVLan(VLan vlan) {
		VLAN_LIST.add(vlan);
	}

	/**
	 * Removes a VLan from the global data.
	 * 
	 * @param vlan
	 *            VLan to remove
	 * @return bool true if VLan was in the list and was removed, false
	 *         if VLan was not in the list
	 */
	public static boolean removePs(VLan v) {

		boolean bool = false;
		for (VLan vlan : VLAN_LIST) {
			if (vlan == v) {
				VLAN_LIST.remove(vlan);
				bool = true;
			}
		}
		return bool;
	}

	/**
	 * Removes a VLan from the global data
	 * 
	 * @param id
	 *            ID of the VLan to remove
	 * @return bool true if VLan was in the list and was removed, false
	 *         if VLan was not in the list
	 */
	public static boolean removeVLan(int id) {
		boolean bool = false;

		for (VLan vlan : VLAN_LIST) {
			if (vlan.getId() == id) {
				VLAN_LIST.remove(vlan);
				bool = true;
			}
		}

		return bool;
	}

	/**
	 * Returns if a VLan with a given ID exists in the global data
	 * 
	 * @param id
	 *            VLan ID to look for
	 * @return false if the VLan does not exist, true if it does
	 */
	public static boolean exists(int id) {

		boolean bool = false;

		for (VLan vlan : VLAN_LIST) {
			if (vlan.getId() == id) {
				bool = true;
			}
		}
		return bool;
	}
}
