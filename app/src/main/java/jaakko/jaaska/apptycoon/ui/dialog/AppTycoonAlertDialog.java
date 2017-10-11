package jaakko.jaaska.apptycoon.ui.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.R;

/**
 * Builds an alert dialog that respects the look and feel of the rest of the UI.
 *
 * By default, the dialog has an "OK" button, which can be overridden by calling setOkAction()
 * and setting up a custom handler for a positive user response.
 */

public class AppTycoonAlertDialog extends AppTycoonDialog {

    public AppTycoonAlertDialog(Context context, String title, String alertText) {
        super(context, R.layout.dialog_alert, title);
        TextView textViewAlert = (TextView) findViewById(R.id.textViewAlertText);
        textViewAlert.setText(alertText);

        Resources res = getContext().getResources();
        String stringOk = res.getString(R.string.dialog_generic_alert_dialog_ok);
        setOkAction(stringOk, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
