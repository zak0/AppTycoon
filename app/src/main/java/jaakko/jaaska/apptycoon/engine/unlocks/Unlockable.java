package jaakko.jaaska.apptycoon.engine.unlocks;

import java.util.ArrayList;
import java.util.List;

/**
 * Unlockables are anything that will not be available from the beginning but require some condition
 * or conditions to be fulfilled before they become accessible - or unlocked.
 */

public abstract class Unlockable {

    private List<UnlockCondition> mConditions;

    public Unlockable() {
        mConditions = new ArrayList<>();
    }

    /**
     * Builds a human readable string that describes the conditions to unlock this item.
     * Can also include progress information on quantitative conditions.
     *
     * @return A textual information on how to unlock this item.
     */
    public String getConditionsString() {
        // Now just concatenates the descriptions of conditions.
        String ret = "";

        for (UnlockCondition condition : mConditions) {
            String description = condition.getDescription();
            if (description != null && description.length() > 0) {
                ret += "* " + condition.getDescription() + " \n";
            }
        }

        return ret;
    }

    /**
     * Checks if the conditions for unlocking this item are filled.
     *
     * @return True when this item is ready to be unlocked.
     */
    protected boolean conditionsFulfilled() {

        // If even one condition is not met, then the unlockable cannot be unlocked.
        for (UnlockCondition condition : mConditions) {
            if (!condition.isFulfilled()) {
                return false;
            }
        }

        // All the conditions were fulfilled if we got this far.
        return true;
    }

    /**
     * Determines if this item is unlocked. Method {@link #conditionsFulfilled()} can be used here,
     * but for complex (i.e. computationally heavy) checking
     * consider storing the unlocked status locally after the item is unlocked and returning the
     * locked/unlocked state from that instead.
     *
     * @return True when this item is unlocked.
     */
    public abstract boolean isUnlocked();

    public List<UnlockCondition> getUnlockConditions() {
        return mConditions;
    }

    public void addUnlockCondition(UnlockCondition condition) {
        mConditions.add(condition);
    }
}
