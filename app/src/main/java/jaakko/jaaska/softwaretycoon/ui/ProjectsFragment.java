package jaakko.jaaska.softwaretycoon.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jaakko.jaaska.softwaretycoon.R;
import jaakko.jaaska.softwaretycoon.engine.core.GameEngine;
import jaakko.jaaska.softwaretycoon.utils.Utils;

/**
 * Created by jaakko on 16.4.2017.
 */

public class ProjectsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        return view;
    }
}
