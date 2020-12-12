package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
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

        //Subscribing to the BombDestroyerEvent and for the Termination broadcast.
        subscribeBombDestoyingEvent();
        subscribeTermination();
       
    }
    /**
     * Subscribing to the {@link TerminationBroadcast}.
     * When receiving this broadcast-> calling the current {@link MicroService}
     * termination method and informing the {@link Diary} of the termination.
     */
    private void subscribeTermination(){
        //Termination event subscription
        Callback<TerminationBroadcast> terminationCallback=(terminationBroadcast)->{
            //Informing the diary of the changes
            Diary.getInstance().stampLandoTerminate();
            terminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);
    }

    /**
     * Subscribing to the {@link BombDestroyerEvent}.
     * When receiving this event-> calling the {@link Callback}
     * associated with this event.
     * Informing the {@link Diary} of the event completion.
     */
    private void subscribeBombDestoyingEvent(){
        Callback<BombDestroyerEvent> bombDestroyerEventCallback=(bombDestroyerEvent)->{
            try {
                Thread.sleep(duration);
                complete(bombDestroyerEvent, true); // TODO: should it be like this?
            } catch (InterruptedException e) {
                e.printStackTrace();
                complete(bombDestroyerEvent, false); // TODO: should it be like this?
            }
        };
        subscribeEvent(BombDestroyerEvent.class,bombDestroyerEventCallback);
    }
}
