package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

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
        Callback<BombDestroyerEvent> bombDestroyerEventCallback=(bombDestroyerEvent)->{
            MessageBus messageBus = MessageBusImpl.getInstance();
            try {
                Thread.sleep(duration);
                messageBus.complete(bombDestroyerEvent, true); // TODO: should it be like this?
            } catch (InterruptedException e) {
                e.printStackTrace();
                messageBus.complete(bombDestroyerEvent, false); // TODO: should it be like this?
            }
            //Informing the diary of the changes
            Diary.getInstance().stampLandoTerminate();
        };
        subscribeEvent(BombDestroyerEvent.class,bombDestroyerEventCallback);

        //Termination event registration
        Callback<TerminationBroadcast> terminationCallback=(terminationBroadcast)->{
            Diary.getInstance().stampLandoTerminate();
            terminate();
            //Informing the diary of the termination.
            Diary.getInstance().stampLandoTerminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);
       
    }
}
