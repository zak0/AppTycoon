package jaakko.jaaska.apptycoon.ui;

import android.os.Bundle;

/**
 * Interface for updating the UI.
 *
 * Created by jaakko on 23.4.2017.
 */

public interface UiUpdater {

    /**
     * Perform an action in the UI.
     * @param action ID of the cation that launched the update.
     * @param args Optional arguments that are passed to the class doing the UI update.
     */
    public void updateUi(int action, Bundle args);
}
