package jaakko.jaaska.apptycoon.ui.fragment;

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

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.core.GameState;
import jaakko.jaaska.apptycoon.engine.people.EmployeeType;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.dialog.ActionSelectDialogBuilder;

/**
 * Fragment for listing and managing hired employees.
 */

public class EmployeesFragment extends Fragment {

    RecyclerView mRecyclerView;
    EmployeeRecyclerViewAdapter mRecyclerViewAdapter;

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

        // Show all employee types here (both that company already has and the ones that it doesn't).
        mRecyclerViewAdapter = new EmployeeRecyclerViewAdapter(EmployeeType.getAllTypes());

        mRecyclerView.setHasFixedSize(true); // performance boost when content does not change the size of RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

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

            viewHolder.layoutEmployeeStats = view.findViewById(R.id.layoutEmployeeStats);
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

            Resources res = AppTycoonApp.getContext().getResources();

            String stringPayrollCount = res.getString(R.string.employee_list_item_n_on_payroll, employeeType.getCount());
            String stringCodeGain = res.getString(R.string.employee_list_item_code_gain, employeeType.getCpsGain());
            String stringQualityGain = res.getString(R.string.employee_list_item_quality_gain, employeeType.getQualityGain());
            String stringSalary = res.getString(R.string.employee_list_item_salary, employeeType.getSalary() * 3600f);

            holder.textViewEmployeeType.setText(employeeType.getTitle());
            holder.textViewEmployeeCount.setText(stringPayrollCount);

            if (employeeType.isUnlocked()) {

                holder.textViewEmployeeDescription.setText(employeeType.getDescription());

                holder.layoutEmployeeStats.setVisibility(View.VISIBLE);
                holder.textViewEmployeeCodeGain.setText(stringCodeGain);
                holder.textViewEmployeeQualityGain.setText(stringQualityGain);
                holder.textViewEmployeeSalary.setText(stringSalary);

                // Now clicking the card hires one more of the selected employee.
                holder.containerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGameState.getCompany().addEmployee(employeeType.getType(), 1);
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });

                // Long-clicking fires an employee.
                holder.containerView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mGameState.getCompany().removeEmployee(employeeType.getType(), 1);
                        mRecyclerViewAdapter.notifyDataSetChanged();
                        return true; // do not pass to OnClickListener after this
                    }
                });
            } else {
                holder.textViewEmployeeDescription.setText(employeeType.getConditionsString());
                holder.layoutEmployeeStats.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return mEmployees.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View containerView;
            View layoutEmployeeStats;

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