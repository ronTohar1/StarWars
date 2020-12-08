package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.FinishedMissionBroadcast;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;
    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
        Callback<BombDestroyerEvent> bombDestroyerEventCallback=(a)->{
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        subscribeEvent(BombDestroyerEvent.class,bombDestroyerEventCallback);

        //Termination event registration
        Callback<FinishedMissionBroadcast> terminationCallback=(f)->{
            terminate();
        };
        subscribeBroadcast(FinishedMissionBroadcast.class,terminationCallback);
       
    }
}
