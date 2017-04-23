package jaakko.jaaska.softwaretycoon.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import jaakko.jaaska.softwaretycoon.R;
import jaakko.jaaska.softwaretycoon.SoftwareTycoonApp;
import jaakko.jaaska.softwaretycoon.ui.UiUpdateHandler;

/**
 * Builds a Dialog for navigating to sub-fragments.
 *
 * First add all the menu items with addEntry(..), then get
 * the dialog by calling getDialog().
 *
 * Created by jaakko on 22.4.2017.
 */

public class NavigationDialogBuilder {

    private static final String TAG = "NavigationDialogBuilder";

    private Context mContext;

    /** Maps sub-fragment (and menu item) titles to sub-fragments.
     * Value is the ID of the fragment as defined in MainActivity constants. */
    private Map<String, Integer> mEntriesToFragments;

    /** The list of menu items in the dialog. The order in this list is the order
     * that the items appears on the NavigationDialog.
     */
    private List<String> mMenuEntries;

    public NavigationDialogBuilder(Context context) {
        mEntriesToFragments = new HashMap<>();
        mMenuEntries = new ArrayList<>();

        mContext = context;
    }

    /**
     * Adds an entry into the navigation dialog.
     * Add the items in the order you want them to appear in the dialog.
     * @param entry Title of the entry.
     * @param targetFragment ID of the fragment to navigate to. See MainActivity FRAGMENT_* constants.
     */
    public void addEntry(String entry, int targetFragment) {
        mEntriesToFragments.put(entry, targetFragment);
        mMenuEntries.add(entry);
    }

    public Dialog getDialog() {
        Log.d(TAG, "getDialog() - num of entries = " + mMenuEntries.size());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        // Custom adapter for the list items so that the custom list item layout can be used.
        ListAdapter listAdapter = new ArrayAdapter<String>(
                SoftwareTycoonApp.getContext(), R.layout.list_item_navigation_dialog, mMenuEntries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) SoftwareTycoonApp
                        .getContext()
                        .getSystemService(SoftwareTycoonApp.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.list_item_navigation_dialog, null);

                TextView title = (TextView) convertView.findViewById(R.id.textView);
                title.setText(mMenuEntries.get(position));

                return convertView;
            }
        };

        builder.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = UiUpdateHandler.getInstance().obtainMessage(UiUpdateHandler.ACTION_REPLACE_FRAGMENT);
                Bundle data = new Bundle();
                data.putInt(UiUpdateHandler.ARG_TARGET_FRAGMENT, mEntriesToFragments.get(mMenuEntries.get(which)));
                msg.setData(data);
                msg.sendToTarget();

                dialog.dismiss();
            }
        });

        return builder.create();
    }

}
