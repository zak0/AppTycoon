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
import android.widget.Toast;

import java.util.List;

import jaakko.jaaska.apptycoon.AppTycoonApp;
import jaakko.jaaska.apptycoon.R;
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

        // Show all employee types here (both that company already has and the ones that it doesn't).
        mRecyclerViewAdapter = new EmployeeRecyclerViewAdapter(EmployeeType.getAllTypes());

        mRecyclerView.setHasFixedSize(true); // performance boost when content does not change the size of RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        return view;
    }

    private class EmployeeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ITEM_TYPE_LOCKED = 1;
        private static final int ITEM_TYPE_UNLOCKED = 2;

        private List<EmployeeType> mEmployees;
        private GameState mGameState;

        private EmployeeRecyclerViewAdapter(List<EmployeeType> employees) {
            mEmployees = employees;
            mGameState = GameEngine.getInstance().getGameState();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == ITEM_TYPE_UNLOCKED) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_employee, parent, false);
                UnlockedEmployeeViewHolder viewHolder = new UnlockedEmployeeViewHolder(view);

                viewHolder.textViewEmployeeType = (TextView) view.findViewById(R.id.textViewEmployeeType);
                viewHolder.textViewEmployeeCount = (TextView) view.findViewById(R.id.textViewEmployeeCount);
                viewHolder.textViewEmployeeDescription = (TextView) view.findViewById(R.id.textViewEmployeeDescription);
                viewHolder.textViewEmployeeCodeGain = (TextView) view.findViewById(R.id.textViewEmployeeCodeGain);
                viewHolder.textViewEmployeeQualityGain = (TextView) view.findViewById(R.id.textViewEmployeeQualityGain);
                viewHolder.textViewEmployeeSalary = (TextView) view.findViewById(R.id.textViewEmployeeSalary);

                return viewHolder;

            } else { // viewType == ITEM_TYPE_LOCKED
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_employee_locked, parent, false);
                LockedEmployeeViewHolder viewHolder = new LockedEmployeeViewHolder(view);

                viewHolder.textViewEmployeeType = (TextView) view.findViewById(R.id.textViewEmployeeType);
                viewHolder.textViewUnlockConditions = (TextView) view.findViewById(R.id.textViewEmployeeUnlockConditions);

                return viewHolder;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mEmployees.get(position).isUnlocked() ? ITEM_TYPE_UNLOCKED : ITEM_TYPE_LOCKED;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
            final EmployeeType employeeType = mEmployees.get(position);

            if (getItemViewType(position) == ITEM_TYPE_LOCKED) {
                LockedEmployeeViewHolder holder = (LockedEmployeeViewHolder) vh;
                holder.textViewEmployeeType.setText(employeeType.getTitle());
                holder.textViewUnlockConditions.setText(employeeType.getConditionsString());

            } else { // viewType == ITEM_TYPE_UNLOCKED
                UnlockedEmployeeViewHolder holder = (UnlockedEmployeeViewHolder) vh;

                // EmployeeType needs to be set to holder in order to have access to it
                // in the OnClickListener set to the card.
                holder.employeeType = employeeType;
                refreshEmployeeCard(holder);
            }
        }

        /**
         * Refreshes the views within a given {@link UnlockedEmployeeViewHolder} to correspond
         * with the current state of the {@link EmployeeType} the card is showing.
         * @param holder {@link UnlockedEmployeeViewHolder} that needs to be refreshed
         */
        private void refreshEmployeeCard(UnlockedEmployeeViewHolder holder) {
            Resources res = AppTycoonApp.getContext().getResources();
            EmployeeType employeeType = holder.employeeType;

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
        }

        @Override
        public int getItemCount() {
            return mEmployees.size();
        }

        class UnlockedEmployeeViewHolder extends RecyclerView.ViewHolder {

            EmployeeType employeeType;

            TextView textViewEmployeeType;
            TextView textViewEmployeeCount;
            TextView textViewEmployeeDescription;
            TextView textViewEmployeeCodeGain;
            TextView textViewEmployeeQualityGain;
            TextView textViewEmployeeSalary;

            UnlockedEmployeeViewHolder(View itemView) {
                super(itemView);

                // Now clicking the card hires one more of the selected employee.
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean success = mGameState.getCompany().addEmployee(employeeType.getType(), 1);

                        if (success) {
                            refreshEmployeeCard(UnlockedEmployeeViewHolder.this);
                            //mRecyclerViewAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(),
                                    "Office is full. Consider moving into a larger office.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                // Long-clicking fires an employee.
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mGameState.getCompany().removeEmployee(employeeType.getType(), 1);
                        refreshEmployeeCard(UnlockedEmployeeViewHolder.this);
                        return true; // do not pass to OnClickListener after this
                    }
                });
            }

        }

        class LockedEmployeeViewHolder extends RecyclerView.ViewHolder {

            TextView textViewEmployeeType;
            TextView textViewUnlockConditions;

            LockedEmployeeViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}