package jaakko.jaaska.apptycoon.ui.listener;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import jaakko.jaaska.apptycoon.ui.Action;

/**
 * Utility class to simplify binding listeners for clickable TextViews that are inside the fragment
 * content area.
 */

public class TextViewListenerBinder {

    /**
     * Sets an OnClickListener that when clicked, performs the action given as param.
     * Also, sets an OnTouchListener that changes the color of the TextView when it's touched
     * to simulate the feel of an object that can be interacted with.
     *
     * @param textView TextView to set these listeners to
     * @param action Action to perform when the TextView is clicked
     */
    public static void bindActionToTextView(TextView textView, final Action action) {
        textView.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.GRAY,
                textView.getCurrentTextColor()));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.doAction();
            }
        });
    }

}
