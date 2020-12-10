package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private static class SingletonHolder {
        private static Diary instance = new Diary();
    }

    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    // TODO: do we need to enforce that each method is called by the one that is allowed to call it

    private Diary(){
        totalAttacks = new AtomicInteger(0);
    }

    public static Diary getInstance(){
        return SingletonHolder.instance;
    }

    public void incrementTotalAttacks(){
        totalAttacks.getAndIncrement(); // incrementing by 1 atomically to make it threaded safe
        // TODO: make sure we can use this and don't need to implement in ourselves
    }

    public void stampHanSoloFinish(){
        HanSoloFinish = getTimeStampInMillis();
    }

    public void stampC3POFinish(){
        C3POFinish = getTimeStampInMillis();
    }

    public void stampR2D2Deactivate(){
        R2D2Deactivate = getTimeStampInMillis();
    }

    public void stampLeiaTerminate(){
        LeiaTerminate = getTimeStampInMillis();
    }

    public void stampHanSoloTerminate(){
        HanSoloTerminate = getTimeStampInMillis();
    }

    public void stampC3POTerminate(){
        C3POTerminate = getTimeStampInMillis();
    }

    public void stampR2D2Terminate(){
        R2D2Terminate = getTimeStampInMillis();
    }

    public void stampLandoTerminate(){
        LandoTerminate = getTimeStampInMillis();
    }

    private static long getTimeStampInMillis(){
        return System.currentTimeMillis();
    }
}
