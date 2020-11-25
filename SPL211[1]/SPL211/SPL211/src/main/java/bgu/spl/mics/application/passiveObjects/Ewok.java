package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */

public class Ewok {
	int serialNumber;
	boolean available;

    /**
     * A constructor that creates an Ewok with the given serial number and availability
     * @param serialNumber the serial number of the Ewok to create
     * @param available will the created Ewok be available
     */
	public Ewok(int serialNumber, boolean available){
	    this.serialNumber = serialNumber;
	    this.available = available;
    }

    /**
     * Acquires an Ewok if available, otherwise throws an exception
     * @throws IllegalStateException if the Ewok is already unavailable
     */
    public void acquire() {
		if (!isAvailable())
		    throw new IllegalStateException("Can't acquire an unavailable Ewok");
		available = false;
    }

    /**
     * release an Ewok if unavailable, otherwise throws an exception
     * @throws IllegalStateException if the Ewok is already available
     */
    public void release() {
    	if (isAvailable())
    	    throw new IllegalStateException("Can't release an available Ewok");
    	available = true;
    }

    /**
     * this method checks if the Ewok is available
     * @return true if the Ewok is available, or false otherwise
     */
    public boolean isAvailable(){
        return available;
    }
}
