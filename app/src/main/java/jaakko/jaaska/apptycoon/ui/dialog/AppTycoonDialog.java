package jaakko.jaaska.apptycoon.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.ui.listener.TextViewChangeColourOnTouchListener;

/**
 * An extension of Dialog class for easier building of dialogs with consistent look
 * to the rest of the game.
 *
 * The top bar and the two action buttons on it are handled within this class. Rest of the
 * stuff - namely the stuff specific to the dialog in question - are implemented elsewhere.
 * The Views are accessed similarly as with a plain Dialog.
 */
public class AppTycoonDialog extends Dialog {

    private TextView mTextViewOk;
    private TextView mTextViewCancel;

    /**
     * Constructor.
     *
     * @param context Current activity context.
     * @param layout ID of the content layout resource for the dialog.
     * @param dialogTitle Title of the dialog.
     */
    public AppTycoonDialog(Context context,
                           int layout,
                           String dialogTitle) {
        this(context, LayoutInflater.from(context).inflate(layout, null), dialogTitle);
    }

    /**
     * Constructor.
     *
     * @param context Current activity context.
     * @param contentView View to be placed as the content of the dialog.
     * @param dialogTitle Title of the dialog.
     */
    public AppTycoonDialog(Context context,
                           View contentView,
                           String dialogTitle) {
        super(context);
        setContentView(R.layout.dialog_apptycoon_dialog);

        RelativeLayout layoutContentContainer = (RelativeLayout) findViewById(R.id.layoutDialogContent);
        layoutContentContainer.addView(contentView);

        setTitle(dialogTitle);

        // Action buttons are hidden until an action for them is set.
        mTextViewOk = (TextView) findViewById(R.id.textViewDialogActionOk);
        mTextViewCancel = (TextView) findViewById(R.id.textViewDialogActionCancel);
        mTextViewOk.setVisibility(View.INVISIBLE);
        mTextViewCancel.setVisibility(View.INVISIBLE);
    }

    /**
     * Sets the title for the dialog.
     *
     * @param title New title for the dialog.
     */
    @Override
    public void setTitle(@Nullable CharSequence title) {
        ((TextView) findViewById(R.id.textViewDialogTitle)).setText(title);
    }

    private void setActionTextViewListener(TextView view, View.OnClickListener listener) {
        view.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK,
                view.getCurrentTextColor()));
        view.setOnClickListener(listener);
    }

    public void setOkAction(View.OnClickListener onOkListener) {
        mTextViewOk.setVisibility(View.VISIBLE);
        setActionTextViewListener(mTextViewOk, onOkListener);
    }

    public void setCancelAction(View.OnClickListener onCancelListener) {
        mTextViewCancel.setVisibility(View.VISIBLE);
        setActionTextViewListener(mTextViewCancel, onCancelListener);
    }

}
