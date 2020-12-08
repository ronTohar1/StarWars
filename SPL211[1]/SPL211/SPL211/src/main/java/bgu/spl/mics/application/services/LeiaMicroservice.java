package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.FinishedMissionBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Future<Boolean>[] attackFutures;
	private int futuresCounter;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		attackFutures= new Future[attacks.length];
		futuresCounter=0;
    }

    @Override
    protected void initialize() {

        Callback<FinishedMissionBroadcast> terminationCallback=(f)->{
            terminate();
        };

        subscribeBroadcast(FinishedMissionBroadcast.class,terminationCallback);

        //Sleeping so everyone can subscribe.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        for (Attack a : attacks) {
            Future<Boolean> attackFuture = this.sendEvent(new AttackEvent(a));
            attackFutures[futuresCounter]=attackFuture;
        }
        for(int i=0;i<attackFutures.length;i++){
            Boolean futureIsResolved=false;
            while(!futureIsResolved) {
                futureIsResolved = attackFutures[i].get();
                // TODO: 08/12/2020  check if need to send again.
                //Re-sending an attack
                if(!futureIsResolved)
                    attackFutures[i] = sendEvent(new AttackEvent(attacks[i]));
            }
        }

        //Deactivating the shield.
        Future<Boolean> deactivationFuture= this.sendEvent(new DeactivationEvent());
        Boolean deactivated=false;
        //Re-sending the event if it wasn't completed successfully.
        while(!deactivated){
            deactivated=deactivationFuture.get();
            if(!deactivated)
                deactivationFuture=sendEvent(new DeactivationEvent());
        }

        //After deactivating the shield-> sending a broadcast to bomb the star
        //Bombing the star.
        Future<Boolean> bombDestructionFuture= sendEvent(new BombDestroyerEvent());
        Boolean bombLanded=false;
        //Re-sending the event if it wasn't completed successfully.
        while(!bombLanded){
            bombLanded=bombDestructionFuture.get();
            if(!bombLanded)
                bombDestructionFuture=sendEvent(new BombDestroyerEvent());
        }

        //Terminating every thread.
        sendBroadcast(new FinishedMissionBroadcast());
    }
}
