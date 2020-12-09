package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
	
    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize() {
        Callback<AttackEvent> attackEventCallback= (attackEvent)->{
            Attack attack=attackEvent.getAttack();
            MessageBus messageBus = MessageBusImpl.getInstance();
            Diary diary = Diary.getInstance();
            Ewoks ewoks = Ewoks.getInstance();
            try {
                ewoks.acquire(attack.getSerials());
                Thread.sleep(attack.getDuration());
                //Informing the diary of the changes.
                diary.incrementTotalAttacks();
                diary.stampC3POFinish();
                messageBus.complete(attackEvent, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
                messageBus.complete(attackEvent, false);
            }
            ewoks.release(attack.getSerials());
            // TODO: And check if it is okay to do it here, and the stamps an all before. I don't think so
        };
        subscribeEvent(AttackEvent.class,attackEventCallback);

        //Termination event registration
        Callback<TerminationBroadcast> terminationCallback=(terminationBroadcast)->{
            //Informing the diary of the termination.
            Diary.getInstance().stampC3POTerminate();
            terminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);
    }
}
