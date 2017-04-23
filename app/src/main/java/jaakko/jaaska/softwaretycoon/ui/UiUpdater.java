package jaakko.jaaska.softwaretycoon.ui;

import android.os.Bundle;

/**
 * Interface for updating the UI.
 *
 * Created by jaakko on 23.4.2017.
 */

public interface UiUpdater {

    /**
     * Perform an action in the UI.
     * @param args Optional arguments that are passed to the class doing the UI update.
     */
    public void updateUi(Bundle args);
}
