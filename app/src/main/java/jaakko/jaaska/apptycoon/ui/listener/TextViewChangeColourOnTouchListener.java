package jaakko.jaaska.apptycoon.ui.listener;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * An universal listener for TextViews to change the text color when the view
 * is touched.
 *
 * Created by jaakko on 8.5.2017.
 */

public class TextViewChangeColourOnTouchListener implements View.OnTouchListener {

    private int mOnTouchColor;
    private int mOriginalColor;

    /**
     * Constructor.
     *
     * Parameter colors are {@link android.graphics.Color Color} colors.
     *
     * @param onTouchColor Color of the text when touched.
     * @param originalColor Original color of the text.
     */
    public TextViewChangeColourOnTouchListener(int onTouchColor, int originalColor) {
        mOnTouchColor = onTouchColor;
        mOriginalColor = originalColor;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // The view is ALWAYS a TextViews.
        TextView tv = (TextView) v;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tv.setTextColor(mOnTouchColor);
                break;
            default:
                tv.setTextColor(mOriginalColor);
        }
        return false;
    }
}
