package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
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
            Ewoks ewoks = Ewoks.getInstance();
            try {
                ewoks.acquire(attack.getSerials());
                Thread.sleep(attack.getDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ewoks.release(attack.getSerials());
        };
        subscribeEvent(AttackEvent.class,attackEventCallback);

        //Termination event registration
        Callback<TerminationBroadcast> terminationCallback=(terminationBroadcast)->{
            terminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);
    }
}
