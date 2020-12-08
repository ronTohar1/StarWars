package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

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

        //Subscribing to Termination Broadcast.
        Callback<TerminationBroadcast> terminationCallback=(terminationBroadcast)->{
            terminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);

        //Sleeping so everyone can subscribe.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Executing attacks
        executeAttackEvents();
        //After executing attacks, destroying shields.
        destroyShieldEvent();
        //After destroying shield, executing bombing event.
        bombEvent();
        //After executing the bomb event, sending the termination broadcast
        sendTerminationSignal();
    }

    /**
     * Executing all of the attack events.
     * Sending each attack to a {@Link MicroService} to handle it.
     * After
     */
    private void executeAttackEvents(){
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
    }

    private void sendTerminationSignal(){
        //Terminating every thread.
        sendBroadcast(new TerminationBroadcast());
    }

    private void destroyShieldEvent(){
        //Deactivating the shield.
        Future<Boolean> deactivationFuture= this.sendEvent(new DeactivationEvent());
        Boolean deactivated=false;
        //Re-sending the event if it wasn't completed successfully.
        while(!deactivated){
            deactivated=deactivationFuture.get();
            if(!deactivated)
                deactivationFuture=sendEvent(new DeactivationEvent());
        }
    }

    private void bombEvent(){
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
    }
}
