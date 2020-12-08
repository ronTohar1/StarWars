package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishedMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
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
        Callback<AttackEvent> c= (a)->{
            Attack attack=a.getAttack();
            Ewoks ewoks = Ewoks.getInstance();
            for (Integer ewokSerial : attack.getSerials()){
                ewoks.aquireEwok(ewokSerial);
            }
            try {
                Thread.sleep(attack.getDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
        subscribeEvent(AttackEvent.class,c);

        //Termination event registration
        Callback<FinishedMissionBroadcast> terminationCallback=(f)->{
            terminate();
        };
        subscribeBroadcast(FinishedMissionBroadcast.class,terminationCallback);
    }


}
