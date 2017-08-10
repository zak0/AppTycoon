package jaakko.jaaska.apptycoon.console;

/**
 * Created by jaakko on 27.3.2017.
 */

public abstract class Action {

    private String mCommand; // Command that starts the action.
    private String mDescription; // Description what the Action does. This is shown on the menu page.
                                // Describe the possible params here.

    public Action(String command, String description) {
        mCommand = command;
        mDescription = description;
    }


    public String getCommand() {
        return mCommand;
    }

    public String getDescription() {
        return mDescription;
    }

    /**
     * This is what the action does.
     * @param params params that the action needs
     */
    public abstract void doAction(Object... params);



}
