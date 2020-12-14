package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    // for the threaded safe singleton
    private static class SingletonHolder {
        private static Diary instance = new Diary();
    }

    private AtomicInteger totalAttacks; // using AtomicInteger to make it threaded safe
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;



    /**
     * A private constructor that creates a Diary with no time values yet, and 0 attacks
     * It is private for the singleton design pattern
     */
    private Diary(){
        totalAttacks = new AtomicInteger(0); // starting with 0 attacks
    }

    /**
     * A getter for the instance of the singleton. If the instance does not exist yet, it creates it
     * @return The instance of the singleton (creates a new one if doesn't exist yet)
     */
    public static Diary getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * This method increments the number of attacks by 1
     */
    public void incrementTotalAttacks(){
        totalAttacks.getAndIncrement(); // incrementing by 1 atomically to make it threaded safe
    }

    /**
     * This method stamps the finish time of HanSolo to be now
     */
    public void stampHanSoloFinish(){
        HanSoloFinish = getTimeStampInMillis();
    }

    /**
     * This method stamps the finish time of C3PO to be now
     */
    public void stampC3POFinish(){
        C3POFinish = getTimeStampInMillis();
    }

    /**
     * This method stamps the deactivation time of R2D2 to be now
     */
    public void stampR2D2Deactivate(){
        R2D2Deactivate = getTimeStampInMillis();
    }

    /**
     * This method stamps the termination time of Leia to be now
     */
    public void stampLeiaTerminate(){
        LeiaTerminate = getTimeStampInMillis();
    }

    /**
     * This method stamps the termination time of HanSolo to be now
     */
    public void stampHanSoloTerminate(){
        HanSoloTerminate = getTimeStampInMillis();
    }

    /**
     * This method stamps the termination time of C3PO to be now
     */
    public void stampC3POTerminate(){
        C3POTerminate = getTimeStampInMillis();
    }

    /**
     * This method stamps the termination time of R2D2 to be now
     */
    public void stampR2D2Terminate(){
        R2D2Terminate = getTimeStampInMillis();
    }

    /**
     * This method stamps the termination time of Lando to be now
     */
    public void stampLandoTerminate(){
        LandoTerminate = getTimeStampInMillis();
    }

    /**
     * This private static method returns the current time stamp in milliseconds
     * @return The current time stamp in milliSeconds
     */
    private static long getTimeStampInMillis(){
        return System.currentTimeMillis();
    }
}
