package jaakko.jaaska.softwaretycoon.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jaakko.jaaska.softwaretycoon.R;

/**
 * Created by jaakko on 16.4.2017.
 */

public class EmployeesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        return view;
    }
}
