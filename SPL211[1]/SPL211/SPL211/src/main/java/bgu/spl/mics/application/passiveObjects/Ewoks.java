package bgu.spl.mics.application.passiveObjects;


import org.graalvm.compiler.nodes.calc.ObjectEqualsNode;

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

    private static Ewoks instance = new Ewoks(); // TODO: change to better threaded safe singleton

    private Ewok[] ewoks;

    private Ewoks(){
        ewoks = null;
    }

    /**
     * This method is not threaded safe, and should be called once. It initializes this class with the
     * given number of ewoks
     * @param numberOfEwoks the number of Ewoks to initialize the class with
     */
    public void initialize(int numberOfEwoks){ // TODO: is it okay to use this
        ewoks = new Ewok[numberOfEwoks];
        for (int index = 0; index < ewoks.length; index++)
            ewoks[index] = new Ewok(toSerialNumber(index)); // Creates a non available Ewok
    }

    public static Ewoks getInstance(){
        return instance;
    }

    /**
     * Acquires the ewoks in the given serial numbers. This method is blocking- if the required ewoks aren't available,
     * it waits untill they are
     * @param ewoksSerialNumbers the serial numbers of the ewoks to acquire
     * @throws InterruptedException in case of an interruption
     */
    public void acquire(List<Integer> ewoksSerialNumbers) throws InterruptedException{ // TODO: is it ok to use this. Check their version
        Collections.sort(ewoksSerialNumbers); // to avoid deadlocks
        for (int ewokSerialNumber : ewoksSerialNumbers){
            getEwokOfSerialNumber(ewokSerialNumber).acquire();
        }
    }

    /**
     * Releases the ewoks in the given serial numbers
     * @param ewoksSerialNumbers A list of the serial numbers of the ewoks to release
     */
    public void release(List<Integer> ewoksSerialNumbers){
        for (int ewokSerialNumber : ewoksSerialNumbers){
            getEwokOfSerialNumber(ewokSerialNumber).release();
        }
    }

    /**
     * Converts an index in ewoks to its Ewok's serial number
     * @param index the index in Ewoks to convert to its Ewok's serial number
     * @return the serial number of the Ewok in the given index in ewoks
     */
    private int toSerialNumber(int index){
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
    private int toIndex(int serialNumber){
        return serialNumber - 1; // the ewoks' serial numbers start from 1
    }
}
