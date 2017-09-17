package jaakko.jaaska.apptycoon.ui.dialog;

import android.content.Context;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.R;

/**
 * Builds an alert dialog that respects the look and feel of the rest of the UI.
 */

public class AppTycoonAlertDialog extends AppTycoonDialog {

    public AppTycoonAlertDialog(Context context, String title, String alertText) {
        super(context, R.layout.dialog_alert, title);
        TextView textViewAlert = (TextView) findViewById(R.id.textViewAlertText);
        textViewAlert.setText(alertText);
    }

}
