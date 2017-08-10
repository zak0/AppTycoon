package jaakko.jaaska.apptycoon.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;

/**
 * Builds a Dialog that shows a textual list of custom actions.
 *
 * First add all the menu items with addNavigationEntry(..), then get
 * the dialog by calling getDialog().
 *
 * Created by jaakko on 22.4.2017.
 */

public class ActionSelectDialogBuilder {

    private static final String TAG = "ASDlgBuilder";

    private Context mContext;

    /** Maps action (and menu item) titles to actual actions.
     */
    private Map<String, Action> mEntriesToActions;

    /** The list of menu items in the dialog. The order in this list is the order
     * that the items appears on the dialog.
     */
    private List<String> mMenuEntries;

    public ActionSelectDialogBuilder(Context context) {
        mEntriesToActions = new HashMap<>();
        mMenuEntries = new ArrayList<>();

        mContext = context;
    }

    /**
     * Adds an entry into the dialog that performs a custom action
     * @param entry
     * @param action
     */
    public void addCustomActionEntry(String entry, Action action) {
        mEntriesToActions.put(entry, action);
        mMenuEntries.add(entry);
    }

    public void show() {
        getDialog().show();
    }

    /**
     * Adds an entry into the dialog that simply navigates to a new fragment.
     * Add the items in the order you want them to appear in the dialog.
     * @param entry Title of the entry.
     * @param targetFragment ID of the fragment to navigate to. See MainActivity FRAGMENT_* constants.
     */
    public void addNavigationEntry(String entry, final int targetFragment) {
        Action action = new Action() {
            @Override
            public void doAction() {

                Message msg = UiUpdateHandler.obtainReplaceFragmentMessage(targetFragment);
                msg.sendToTarget();

            }
        };

        mEntriesToActions.put(entry, action);
        mMenuEntries.add(entry);
    }

    public Dialog getDialog() {
        Log.d(TAG, "getDialog() - num of entries = " + mMenuEntries.size());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        // Custom adapter for the list items so that the custom list item layout can be used.
        ListAdapter listAdapter = new ArrayAdapter<String>(
                AppTycoonApp.getContext(), R.layout.list_item_navigation_dialog, mMenuEntries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) AppTycoonApp
                        .getContext()
                        .getSystemService(AppTycoonApp.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.list_item_navigation_dialog, null);

                TextView title = (TextView) convertView.findViewById(R.id.textView);
                title.setText(mMenuEntries.get(position));

                return convertView;
            }
        };

        builder.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mEntriesToActions.get(mMenuEntries.get(which)).doAction();

                dialog.dismiss();
            }
        });

        return builder.create();
    }


    public static abstract class Action {
        public abstract void doAction();
    }

}
