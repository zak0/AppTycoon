package jaakko.jaaska.apptycoon.ui.fragment;

import android.view.View;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.ui.MainActivity;

/**
 * Fragment for managing IT assets
 */

public class ItFragment extends AppTycoonFragment {

    public ItFragment() {
        super("IT", R.layout.fragment_it);
    }

    @Override
    protected void onContentCreateView(View view) {
        setBackTargetFragment(MainActivity.FRAGMENT_ASSETS);
    }
}
