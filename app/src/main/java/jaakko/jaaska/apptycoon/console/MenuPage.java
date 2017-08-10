package jaakko.jaaska.apptycoon.console;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaakko on 27.3.2017.
 */

public class MenuPage {

    private String mTitle;
    private Map<String, Action> mActions;

    public MenuPage(String title) {
        mTitle = title;
        mActions = new HashMap<>();
    }

    public void addAction(Action action) {
        if (mActions.containsKey(action.getCommand())) {
            Console.printError("Trying to add a duplicate action into a page '" + mTitle + "'.");
            return;
        }

        mActions.put(action.getCommand(), action);
    }

    public Action getAction(String command) {
        return mActions.get(command);
    }

    /**
     * Use this to print the menu.
     * @return the menu in a nice form suitable for a console UI
     */
    @Override
    public String toString() {
        String ret = "\n" + mTitle + "\n";

        for (String cmd : mActions.keySet()) {
            ret += cmd + " - " + mActions.get(cmd).getDescription() + "\n";
        }

        ret += "back - Go back.\n";

        return ret;
    }
}
