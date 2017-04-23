package jaakko.jaaska.softwaretycoon.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import jaakko.jaaska.softwaretycoon.R;
import jaakko.jaaska.softwaretycoon.ui.MainActivity;
import jaakko.jaaska.softwaretycoon.ui.dialog.NavigationDialogBuilder;

/**
 * Created by jaakko on 16.4.2017.
 */

public class EmployeesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        RelativeLayout titleLayout = (RelativeLayout) view.findViewById(R.id.layoutFragmentTitle);
        titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationDialogBuilder builder = new NavigationDialogBuilder(getActivity());
                builder.addEntry("Browse open applications", MainActivity.FRAGMENT_PROJECTS);
                builder.addEntry("Browse employees", -1);
                builder.getDialog().show();
            }
        });

        return view;
    }
}
