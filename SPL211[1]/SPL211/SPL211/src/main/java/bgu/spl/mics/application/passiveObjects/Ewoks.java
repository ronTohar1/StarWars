package bgu.spl.mics.application.passiveObjects;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    // for the threaded safe singleton
    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks();
    }

    private Ewok[] ewoks;

    /**
     * A constructor that doesn't initialize the ewoks yet (the initialization occurs in the static initialize method)
     * private for the singleton design pattern
     */
    private Ewoks(){
        ewoks = null; // initializing the array in the initialize static method
    }

    /**
     * Gets the singleton Ewoks instance
     * @return the instance of the singleton Ewoks
     */
    public static Ewoks getInstance(){
        return SingletonHolder.instance; // according to the threaded safe singleton implementation
    }

    /**
     * This method is not threaded safe, and should be called once. It initializes the singleton instance with the
     * given number of ewoks
     * @param numberOfEwoks the number of Ewoks to initialize the class with
     */
    public static void initialize(int numberOfEwoks){ // TODO: is it okay to use this
        Ewoks instance = getInstance();
        instance.ewoks = new Ewok[numberOfEwoks];
        // initializing each Ewok:
        for (int index = 0; index < instance.ewoks.length; index++)
            instance.ewoks[index] = new Ewok(toSerialNumber(index)); // Creates a non available Ewok
    }

    /**
     * Acquires the ewoks in the given serial numbers. This method is blocking- if the required ewoks aren't available,
     * it waits until they are
     * @param ewoksSerialNumbers the serial numbers of the ewoks to acquire
     * @throws InterruptedException in case of an interruption
     */
    public void acquire(List<Integer> ewoksSerialNumbers) throws InterruptedException{
        Collections.sort(ewoksSerialNumbers); // to avoid deadlocks
        for (int ewokSerialNumber : ewoksSerialNumbers){
            getEwokOfSerialNumber(ewokSerialNumber).acquire(); // if not available, waits until it is
        }
    }

    /**
     * Releases the unavailable ewoks in the given serial numbers
     * @param ewoksSerialNumbers A list of the serial numbers of the ewoks to release
     */
    public void release(List<Integer> ewoksSerialNumbers){
        for (int ewokSerialNumber : ewoksSerialNumbers){
            getEwokOfSerialNumber(ewokSerialNumber).release(); // releases if not already available
        }
    }

    /**
     * Converts an index in ewoks to its Ewok's serial number
     * @param index the index in Ewoks to convert to its Ewok's serial number
     * @return the serial number of the Ewok in the given index in ewoks
     */
    private static int toSerialNumber(int index){
        return index + 1; // the ewoks' serial numbers start from 1
    }

    /**
     * Gets the Ewok of the given serial number
     * @param serialNumber the serial number of the Ewok to return
     * @return the Ewok in of the given serial number
     */
    private Ewok getEwokOfSerialNumber(int serialNumber){
        return ewoks[toIndex(serialNumber)];
    }

    /**
     * Converts a serial number of an ewok to its index in Ewoks
     * @param serialNumber the serial number of the ewok to return its index in ewoks
     * @return the index of the ewok of the given serial number
     */
    private static int toIndex(int serialNumber){
        return serialNumber - 1; // the ewoks' serial numbers start from 1
    }
}
