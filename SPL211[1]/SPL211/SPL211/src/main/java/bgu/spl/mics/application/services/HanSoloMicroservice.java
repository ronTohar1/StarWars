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
        subscribeAttackEvent();
        subscribeTermination();
    }


    /**
     * Subscribing to the {@link TerminationBroadcast}.
     * When receiving this broadcast-> calling the current {@link MicroService}
     * termination method and informing the {@link Diary} of the termination.
     */
    private void subscribeTermination(){
        //Termination event subscription
        Callback<TerminationBroadcast> terminationCallback=(f)->{
            //Informing the diary of the termination.
            Diary.getInstance().stampHanSoloTerminate();
            terminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);
    }

    /**
     * Subscribing to the {@link AttackEvent}.
     * When receiving this event-> calling the {@link Callback}
     * associated with this event.
     * Informing the {@link Diary} of the event completion.
     *
     * Whenever an {@link AttackEvent} is called, the current {@link MicroService}
     * tries to acquire the resources that are needed, sleeps to simulate attacking,
     * and finally releasing the resources.
     */
    private void subscribeAttackEvent(){

        Callback<AttackEvent> c= (attackEvent)->{
            Attack attack=attackEvent.getAttack();
            Diary diary = Diary.getInstance();
            Ewoks ewoks = Ewoks.getInstance();
            try {
                //acquiring the required resources for the attack.
                ewoks.acquire(attack.getSerials());
                //simultaing the attack by sleeping.
                Thread.sleep(attack.getDuration());
                //Informing the diary of the changes.
                diary.incrementTotalAttacks();
                diary.stampHanSoloFinish();
                //Event completed:
                complete(attackEvent, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
                complete(attackEvent, false);
            }
            //Releasing back the resources after finishing attacking.
            ewoks.release(attack.getSerials());
        };
        subscribeEvent(AttackEvent.class,c);
    }


}
