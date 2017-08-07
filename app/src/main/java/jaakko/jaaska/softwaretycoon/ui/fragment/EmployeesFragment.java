package jaakko.jaaska.softwaretycoon.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import jaakko.jaaska.softwaretycoon.R;
import jaakko.jaaska.softwaretycoon.SoftwareTycoonApp;
import jaakko.jaaska.softwaretycoon.engine.core.GameEngine;
import jaakko.jaaska.softwaretycoon.engine.core.GameState;
import jaakko.jaaska.softwaretycoon.engine.people.EmployeeType;
import jaakko.jaaska.softwaretycoon.ui.MainActivity;
import jaakko.jaaska.softwaretycoon.ui.dialog.ActionSelectDialogBuilder;

/**
 * Fragment for listing and managing hired employees.
 */

public class EmployeesFragment extends Fragment {

    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        RelativeLayout titleLayout = (RelativeLayout) view.findViewById(R.id.layoutFragmentTitle);
        titleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionSelectDialogBuilder builder = new ActionSelectDialogBuilder(getActivity());
                builder.addNavigationEntry("Browse open applications", MainActivity.FRAGMENT_PROJECTS);
                builder.addNavigationEntry("Browse employees", -1);
                builder.getDialog().show();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewEmployees);
        GameState gameState = GameEngine.getInstance().getGameState();
        EmployeeRecyclerViewAdapter adapter = new EmployeeRecyclerViewAdapter(gameState.getCompany().getEmployees());

        mRecyclerView.setHasFixedSize(true); // performance boost when content does not change the size of RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    private class EmployeeRecyclerViewAdapter extends RecyclerView.Adapter<EmployeeRecyclerViewAdapter.ViewHolder> {

        private List<EmployeeType> mEmployees;
        private GameState mGameState;

        private EmployeeRecyclerViewAdapter(List<EmployeeType> employees) {
            mEmployees = employees;
            mGameState = GameEngine.getInstance().getGameState();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_employee, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.containerView = view;

            viewHolder.textViewEmployeeType = (TextView) view.findViewById(R.id.textViewEmployeeType);
            viewHolder.textViewEmployeeCount = (TextView) view.findViewById(R.id.textViewEmployeeCount);
            viewHolder.textViewEmployeeDescription = (TextView) view.findViewById(R.id.textViewEmployeeDescription);
            viewHolder.textViewEmployeeCodeGain = (TextView) view.findViewById(R.id.textViewEmployeeCodeGain);
            viewHolder.textViewEmployeeQualityGain = (TextView) view.findViewById(R.id.textViewEmployeeQualityGain);
            viewHolder.textViewEmployeeSalary = (TextView) view.findViewById(R.id.textViewEmployeeSalary);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final EmployeeType employeeType = mEmployees.get(position);

            Resources res = SoftwareTycoonApp.getContext().getResources();

            String stringPayrollCount = res.getString(R.string.employee_list_item_n_on_payroll, employeeType.getCount());
            String stringCodeGain = res.getString(R.string.employee_list_item_code_gain, employeeType.getCpsGain());
            String stringQualityGain = res.getString(R.string.employee_list_item_quality_gain, employeeType.getQualityGain());
            String stringSalary = res.getString(R.string.employee_list_item_salary, employeeType.getSalary() * 3600f);

            holder.textViewEmployeeType.setText(employeeType.getTitle());
            holder.textViewEmployeeCount.setText(stringPayrollCount);
            holder.textViewEmployeeDescription.setText(employeeType.getDescription());
            holder.textViewEmployeeCodeGain.setText(stringCodeGain);
            holder.textViewEmployeeQualityGain.setText(stringQualityGain);
            holder.textViewEmployeeSalary.setText(stringSalary);

            // Now clicking the card hires one more of the selected employee.
            holder.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGameState.getCompany().addEmployee(employeeType.getType(), 1);
                    mRecyclerView.invalidate();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEmployees.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View containerView;

            TextView textViewEmployeeType;
            TextView textViewEmployeeCount;
            TextView textViewEmployeeDescription;
            TextView textViewEmployeeCodeGain;
            TextView textViewEmployeeQualityGain;
            TextView textViewEmployeeSalary;

            private ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
