package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;


/**
 * A class that represents an AttackEvent.
 * The attack field contains the resources that the {@link bgu.spl.mics.MicroService}
 * that executes the attack must acquire, and the duration of the attack.
 * (simulated by sleeping)
 */
public class AttackEvent implements Event<Boolean> {
    private Attack attack;

    public AttackEvent(Attack a){
        attack=a;
    }

    /**
     * Getter for the attack field
     * @return 'attack' field.
     */
    public Attack getAttack(){
        return  attack;
    }
	
}
