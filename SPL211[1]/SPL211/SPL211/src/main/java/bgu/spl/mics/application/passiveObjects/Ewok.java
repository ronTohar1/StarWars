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
     * A constructor that creates an Ewok with the given serial number, and makes it available
     * @param serialNumber the serial number of the Ewok to create
     */
	public Ewok(int serialNumber){
        this.serialNumber = serialNumber;
        this.available = true;
    }

    /**
     * This constructor is used for testing
     * A constructor that creates an Ewok with the given serial number and availability
     * @param serialNumber the serial number of the Ewok to create
     * @param available will the created Ewok be available
     */
	public Ewok(int serialNumber, boolean available){
	    this.serialNumber = serialNumber;
	    this.available = available;
    }

    /**
     * This method acquires the Ewok. If the Ewok is not available, it waits until it is
     * This method is blocking
     * @throws InterruptedException in case of an interruption
     */
    public synchronized void acquire() throws InterruptedException{
        while (!isAvailable()){
            wait();
        }
        // the ewok is available now:
        this.available = false; // acquiring the Ewok. No need to notify, because waiting for acquiring only
    }

    /**
     * This method release the Ewok if unavailable, otherwise does nothing
     */
    public synchronized void release() {
    	available = true;
    	notify(); // waiting only for acquiring the Ewok, so no need to notify all
    }

    /**
     * this method checks if the Ewok is available
     * @return true if the Ewok is available, or false otherwise
     */
    public synchronized boolean isAvailable(){
        return available;
    }
}
