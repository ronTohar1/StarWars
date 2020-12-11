package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

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

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		attackFutures= new Future[attacks.length];
    }

    @Override
    protected void initialize() {

        //Subscribing to the termination broadcast.
        subscribeTermination();

        //Sleeping so everyone can subscribe.
        try {
            // System.out.println("Sleeping so everyone can subscribe");
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
     * Subscribing to the {@link TerminationBroadcast}.
     * When receiving this broadcast-> calling the current {@link MicroService}
     * termination method and informing the {@link Diary} of the termination.
     */
    private void subscribeTermination(){
        //Subscribing to Termination Broadcast.
        Callback<TerminationBroadcast> terminationCallback=(terminationBroadcast)->{
            //Informing the diary of the termination.
            Diary.getInstance().stampLeiaTerminate();
            terminate();
        };
        subscribeBroadcast(TerminationBroadcast.class,terminationCallback);
    }

    /**
     * Executing all of the attack events.
     * Sending each attack to a {@link MicroService} to handle it.
     * After
     */
    private void executeAttackEvents() {
        for (int attackIndex = 0; attackIndex < attacks.length; attackIndex++) {
            Future<Boolean> attackFuture = this.sendEvent(new AttackEvent(attacks[attackIndex]));
            attackFutures[attackIndex] = attackFuture;
        }
        for (int i = 0; i < attackFutures.length; i++) {
            Boolean futureIsResolved = false;
            while (attackFutures[i] == null || !attackFutures[i].get()){
                // TODO: 08/12/2020  check if need to send again.
                //Re-sending an attack
                attackFutures[i] = sendEvent(new AttackEvent(attacks[i]));
            }
        }
    }

    /**
     * Sending a termination broadcast to all {@link MicroService}
     * who are subscribed.
     */
    private void sendTerminationSignal(){
        //Terminating every thread.
        sendBroadcast(new TerminationBroadcast());
    }

    /**
     * Sending the {@link DeactivationEvent} to a {@link MicroService}
     * who is subscribed (Round Robing Manner).
     * Waiting for the event to succeed, meaning for the future
     * of the event to return True.
     * (If the future returns false, we are sending the event again untill it is executed successfully)s
     */
    private void destroyShieldEvent(){
        //Deactivating the shield.
        Future<Boolean> deactivationFuture= this.sendEvent(new DeactivationEvent());

        // Re-sending the event until it is completed successfully:
        while(deactivationFuture == null || !deactivationFuture.get()) {
            deactivationFuture = sendEvent(new DeactivationEvent());
        }
    }

    /**
     * Sending the {@link BombDestroyerEvent} to a {@link MicroService}
     * who is subscribed (Round Robing Manner).
     * Waiting for the event to succeed, meaning for the future
     * of the event to return True.
     * (If the future returns false, we are sending the event again untill it is executed successfully)
     */
    private void bombEvent() {
        //After deactivating the shield-> sending a broadcast to bomb the star
        //Bombing the star.
        Future<Boolean> bombDestructionFuture = sendEvent(new BombDestroyerEvent());
        //Re-sending the event until it is completed successfully:
        while (bombDestructionFuture == null || !bombDestructionFuture.get()) {
            bombDestructionFuture = sendEvent(new BombDestroyerEvent());
        }
    }
}
