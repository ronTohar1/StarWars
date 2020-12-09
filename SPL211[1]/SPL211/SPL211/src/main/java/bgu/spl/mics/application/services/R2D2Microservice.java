package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.LinkedList;
import java.util.Queue;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long sleepDuration;
    public R2D2Microservice(long duration) {
        super("R2D2");
        sleepDuration=duration;
    }

    @Override
    protected void initialize() {
        Callback<DeactivationEvent> deactivationEventCallback=(DeactivationEvent)->{
            try {
                Thread.sleep(sleepDuration);
                //Informing the diary of the changes
                Diary.getInstance().stampR2D2Deactivate();
                complete(DeactivationEvent, true); // TODO: should it be like this?
            } catch (InterruptedException e) {
                e.printStackTrace();
                complete(DeactivationEvent, false); // TODO: should it be like this?
            }
        };
        subscribeEvent(DeactivationEvent.class,deactivationEventCallback);

        //Termination event registration
        Callback<TerminationBroadcast> terminationCallback=(terminationBroadcast)->{
            //Informing the diary of the termination.
            Diary.getInstance().stampR2D2Terminate();
            terminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);

    }
}
