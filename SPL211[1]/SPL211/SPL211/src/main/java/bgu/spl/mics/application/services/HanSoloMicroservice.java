package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        Callback<AttackEvent> c= (attackEvent)->{ // TODO: maybe create callback in a different, private, method
            Attack attack=attackEvent.getAttack();
            Diary diary = Diary.getInstance();
            Ewoks ewoks = Ewoks.getInstance();
            try {
                ewoks.acquire(attack.getSerials());
                Thread.sleep(attack.getDuration());
                //Informing the diary of the changes.
                diary.incrementTotalAttacks();
                diary.stampHanSoloFinish();
                complete(attackEvent, true); // TODO: should it be like this?
            } catch (InterruptedException e) {
                e.printStackTrace();
                complete(attackEvent, false); // TODO: should it be like this?
            }
            ewoks.release(attack.getSerials()); // TODO: remove the exception from release of Ewok to prevent problems here
            // TODO: And check if it is okay to do it here, and the stamps an all before. I don't think so
        };
        subscribeEvent(AttackEvent.class,c);

        //Termination event registration
        Callback<TerminationBroadcast> terminationCallback=(f)->{
            //Informing the diary of the termination.
            Diary.getInstance().stampHanSoloTerminate();
            terminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);
    }


}
