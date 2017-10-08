package jaakko.jaaska.apptycoon.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jaakko.jaaska.apptycoon.R;
import jaakko.jaaska.apptycoon.engine.core.GameEngine;
import jaakko.jaaska.apptycoon.engine.project.ContractingProject;
import jaakko.jaaska.apptycoon.engine.project.Project;
import jaakko.jaaska.apptycoon.engine.project.ProjectSlot;
import jaakko.jaaska.apptycoon.engine.project.ProjectTask;
import jaakko.jaaska.apptycoon.ui.MainActivity;
import jaakko.jaaska.apptycoon.ui.UiUpdateHandler;
import jaakko.jaaska.apptycoon.ui.listener.FragmentBackButtonOnClickListener;
import jaakko.jaaska.apptycoon.ui.listener.TextViewChangeColourOnTouchListener;
import jaakko.jaaska.apptycoon.utils.Utils;

/**
 * A fragment for confirming and finalising setting up a new project for a specific project slot.
 *
 * Created by jaakko on 8.5.2017.
 */

public class NewProjectFragment extends Fragment {

    private static final String TAG = "NewProjectFragment";

    /** Project slot that this new project is for. */
    private ProjectSlot mProjectSlot;

    /** Project that is about to be started. */
    private Project mProject;

    public void setProjectSlot(ProjectSlot projectSlot) {
        mProjectSlot = projectSlot;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_project, container, false);

        TextView viewBack = (TextView) view.findViewById(R.id.textViewBack);
        viewBack.setOnClickListener(new FragmentBackButtonOnClickListener());
        viewBack.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK, viewBack.getCurrentTextColor()));

        TextView viewStartProject = (TextView) view.findViewById(R.id.textViewAction);
        viewStartProject.setOnClickListener(new StartProjectOnClickListener());
        viewStartProject.setOnTouchListener(new TextViewChangeColourOnTouchListener(Color.BLACK, viewBack.getCurrentTextColor()));

        // Load project slot.
        int projectSlotIndex = getArguments().getInt(UiUpdateHandler.ARG_PROJECT_SLOT_INDEX);
        Log.d(TAG, "onCreateView() - projectSlotIndex = " + projectSlotIndex);
        mProjectSlot = GameEngine.getInstance().getGameState()
                .getCompany().getProjectSlots().get(projectSlotIndex);


        // TODO: Now just gets a random ContractingProject for testing purposes
        mProject = ContractingProject.getRandomContractingProject();

        // Get the header layout based on the project type.
        View headerView = new View(getContext());
        if (mProject instanceof ContractingProject) {
            ContractingProject cProject = (ContractingProject) mProject;
            Log.d(TAG, "onCreateView() - project was a ContractingProject");
            headerView = inflater.inflate(R.layout.child_new_project_contracting_header, null);

            TextView twProjectName = (TextView) headerView.findViewById(R.id.textViewProjectName);
            TextView twReward = (TextView) headerView.findViewById(R.id.textViewReward);
            TextView twCustomer = (TextView) headerView.findViewById(R.id.textViewCustomer);
            TextView twComplexity = (TextView) headerView.findViewById(R.id.textViewProductComplexity);
            TextView twQualityReq = (TextView) headerView.findViewById(R.id.textViewQualityReq);
            TextView twTime = (TextView) headerView.findViewById(R.id.textViewTime);

            twProjectName.setText(cProject.getName());
            twReward.setText("$ " + Utils.largeNumberToNiceString(cProject.getReward(), 2));
            twCustomer.setText(cProject.getCustomer());
            twComplexity.setText(Long.toString(cProject.getWorkAmount()));
            twQualityReq.setText(Long.toString(cProject.getRequiredQuality()));
            twTime.setText(Utils.millisecondsToTimeString(cProject.getTimeLeft()));
        }

        LinearLayout headerContainer = (LinearLayout) view.findViewById(R.id.layoutHeaderContainer);
        headerContainer.addView(headerView);

        // Populate project tasks RecyclerView
        RecyclerView recyclerViewProjectTasks = (RecyclerView) view.findViewById(R.id.recyclerViewProjectTasks);
        recyclerViewProjectTasks.setHasFixedSize(true); // performance boot for static content
        recyclerViewProjectTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProjectTasks.setAdapter(new ProjectTasksRecyclerViewAdapter(mProject.getTasks()));
        return view;
    }

    private class StartProjectOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Set the project that is being confiured as the project for the selected slot.
            mProjectSlot.setProject(mProject);

            // Go back to project fragment.
            UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_PROJECTS).sendToTarget();
        }
    }

    public class ProjectTasksRecyclerViewAdapter extends RecyclerView.Adapter<ProjectTasksRecyclerViewAdapter.ViewHolder> {

        private List<ProjectTask> mTasks;

        public ProjectTasksRecyclerViewAdapter(List<ProjectTask> tasks) {
            mTasks = tasks;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project_task, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.textViewTitle = (TextView) view.findViewById(R.id.textViewTaskTitle);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ProjectTask task = mTasks.get(position);
            holder.textViewTitle.setText(task.getTaskTitle());
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewTitle;

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
