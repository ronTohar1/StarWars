package bgu.spl.mics.application.passiveObjects;

public class Input {
    private Attack[] attacks;
    int R2D2;
    int Lando;
    int Ewoks;

    /**
     * A getter for the number of ewoks
     * @return the number of ewoks
     */
    public int getEwoks() {
        return Ewoks;
    }

    /**
     * A getter for the time in milliseconds that takes Lando to bomb the star destroyer
     * @return the time in milliseconds that takes Lando to bomb the star destroyer
     */
    public int getLando() {
        return Lando;
    }

    /**
     * A getter for the time in milliseconds that takes R2D2 to deactivate the shield
     * @return the time in milliseconds that takes R2D2 to deactivate the shield
     */
    public int getR2D2() {
        return R2D2;
    }

    /**
     * A getter for the Attacks to execute
     * @return An array of the Attacks to execute
     */
    public Attack[] getAttacks() {
        return attacks;
    }

}