package jaakko.jaaska.apptycoon.unlocks;

/**
 * This is a condition that an object implementing an Unlockable must fulfill before it becomes
 * unlocked.
 */
public interface UnlockCondition {

    /**
     * Constructs a human readable description of the requirements for this condition to
     * be fulfilled.
     *
     * @return A description of the condition.
     */
    String getDescription();

    /**
     * Checks if the condition is fulfilled.
     * @return
     */
    boolean isFulfilled();

}
