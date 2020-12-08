package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishedMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
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
        Callback<AttackEvent> attackEventCallback= (a)->{
            Attack attack=a.getAttack();
            Ewoks ewoks = Ewoks.getInstance();
            ewoks.aquire(attack.getSerials());
            try {
                Thread.sleep(attack.getDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ewoks.releas(attack.getSerials());

        };
        subscribeEvent(AttackEvent.class,attackEventCallback);

        //Termination event registration
        Callback<FinishedMissionBroadcast> terminationCallback=(f)->{
            terminate();
        };
        subscribeBroadcast(FinishedMissionBroadcast.class,terminationCallback);
    }
}
