package jaakko.jaaska.softwaretycoon.ui.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jaakko.jaaska.softwaretycoon.R;
import jaakko.jaaska.softwaretycoon.SoftwareTycoonApp;
import jaakko.jaaska.softwaretycoon.engine.core.GameEngine;
import jaakko.jaaska.softwaretycoon.engine.project.ContractingProject;
import jaakko.jaaska.softwaretycoon.engine.project.ProjectSlot;
import jaakko.jaaska.softwaretycoon.ui.MainActivity;
import jaakko.jaaska.softwaretycoon.ui.UiUpdateHandler;
import jaakko.jaaska.softwaretycoon.ui.UiUpdater;
import jaakko.jaaska.softwaretycoon.utils.Utils;

/**
 * Created by jaakko on 16.4.2017.
 */

public class ProjectsFragment extends Fragment implements UiUpdater {
    private static final String TAG = "ProjectsFragment";

    private ProjectsRecyclerViewAdapter mRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        RecyclerView recyclerViewProjects = (RecyclerView) view.findViewById(R.id.recyclerViewProjects);
        mRecyclerViewAdapter = new ProjectsRecyclerViewAdapter();

        recyclerViewProjects.setHasFixedSize(true); // performance boost when content does not change the size of RecyclerView
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProjects.setAdapter(mRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //UiUpdateHandler.getInstance().registerUpdater(this, UiUpdateHandler.ACTION_REFRESH_UI);
        UiUpdateHandler.getInstance().registerUpdater(mRecyclerViewAdapter, UiUpdateHandler.ACTION_REFRESH_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        UiUpdateHandler.getInstance().unRegisterUpdater(this);
        UiUpdateHandler.getInstance().unRegisterUpdater(mRecyclerViewAdapter);
    }

    @Override
    public void updateUi(int action, Bundle args) {
        //Log.d(TAG, "updateUi() - start");

        // Moved UI updating into view adapter. Doing it this way got the entire UI stuck.
        //mRecyclerViewAdapter.notifyDataSetChanged();
    }

    public class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<ProjectsRecyclerViewAdapter.ViewHolder> implements UiUpdater{

        List<ProjectSlot> mSlots;

        List<Pair<ViewHolder, ProjectSlot>> mViedHolderSlots;
        @Override
        public void updateUi(int action, Bundle args) {
            // Do the UI update here so that we can only update the views that need updating.
            for (Pair<ViewHolder, ProjectSlot> pair : mViedHolderSlots) {
                ViewHolder vh = pair.first;
                ProjectSlot ps = pair.second;
                setupViews(vh, ps);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project_slot, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.layoutParent = view;
            viewHolder.layoutProgress = view.findViewById(R.id.layoutProjectProgress);
            viewHolder.textViewProjectTime = (TextView) view.findViewById(R.id.textViewProjectTime);
            viewHolder.textViewProjectTitle = (TextView) view.findViewById(R.id.textViewProjectTitle);
            viewHolder.textViewProjectInfo = (TextView) view.findViewById(R.id.textViewProjectInfo);
            viewHolder.textViewProjectProgress = (TextView) view.findViewById(R.id.textViewProjectProgress);
            viewHolder.textViewProjectBugs = (TextView) view.findViewById(R.id.textViewProjectNewBugs);
            viewHolder.textViewProjectQuality = (TextView) view.findViewById(R.id.textViewProjectQuality);

            return viewHolder;
        }



        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ProjectSlot projectSlot = mSlots.get(position);

            // Set on click listener based on what is currently in the project slot.
            // Or if it's empty.
            setOnClickListenerForSlot(holder.layoutParent, projectSlot, position);

            // Hide / display data based on the current slot state.
            setupViews(holder, projectSlot);

            // Store the ViewHolder and ProjectSlot to allow smooth UI updates.
            mViedHolderSlots.add(new Pair(holder, projectSlot));
        }

        private void setOnClickListenerForSlot(View slotView, ProjectSlot projectSlot, final int position) {
            // If slot is empty, launch new project setup.
            if (projectSlot.isFree()) {
                slotView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message msg = UiUpdateHandler.obtainReplaceFragmentMessage(MainActivity.FRAGMENT_NEW_PROJECT);
                        msg.getData().putInt(UiUpdateHandler.ARG_PROJECT_SLOT_INDEX, position);
                        msg.sendToTarget();
                    }
                });
            }
        }

        private void setupViews(ViewHolder holder, ProjectSlot slot) {
            if (slot.isFree()) {
                holder.layoutProgress.setVisibility(View.INVISIBLE);
                holder.textViewProjectTime.setVisibility(View.INVISIBLE);
            }

            else if (slot.getProject() instanceof ContractingProject) {
                ContractingProject project = (ContractingProject) slot.getProject();

                Resources res = SoftwareTycoonApp.getContext().getResources();
                String strProjectInfo = res.getString(R.string.project_slot_contracting_project_info,
                                                    project.getCustomer());
                String strProjectProgress = res.getString(R.string.project_slot_progress,
                                                    Utils.largeNumberToNiceString(project.getWorkProgress(), 2),
                                                    Utils.largeNumberToNiceString(project.getWorkAmount(), 2));
                String strProjectQuality = res.getString(R.string.project_slot_contracting_project_quality,
                                                    Utils.largeNumberToNiceString(project.getQuality(), 2),
                                                    Utils.largeNumberToNiceString(project.getRequiredQuality(), 2));
                String strReadyToDeliver = res.getString(R.string.project_slot_ready_to_deliver);
                String strFailedToDeliver = res.getString(R.string.project_slot_failed_to_deliver);

                holder.textViewProjectTitle.setText(project.getName());
                holder.textViewProjectInfo.setText(strProjectInfo);
                holder.textViewProjectTime.setText(Utils.millisecondsToTimeString(project.getTimeLeft()));
                holder.textViewProjectProgress.setText(strProjectProgress);
                holder.textViewProjectQuality.setText(strProjectQuality);

                // Color text views red/green when the project is finished according whether the requirements
                // were met or not.
                if (project.isReady() || project.isSuccessful()) {
                    int colorRed = ContextCompat.getColor(SoftwareTycoonApp.getContext(), R.color.red);
                    int colorGreen = ContextCompat.getColor(SoftwareTycoonApp.getContext(), R.color.green);

                    //holder.textViewProjectTime.setText(project.isFinished() ? strReadyToDeliver : strFailedToDeliver);
                    //holder.textViewProjectTime.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                    //holder.textViewProjectTitle.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                    holder.textViewProjectProgress.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                    holder.textViewProjectQuality.setTextColor(project.isSuccessful() ? colorGreen : colorRed);
                }
            }
        }

        public ProjectsRecyclerViewAdapter() {
            mSlots = GameEngine.getInstance().getGameState().getCompany().getProjectSlots();
            mViedHolderSlots = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return mSlots.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            /** The parent of the entire card */
            View layoutParent;

            /** Layout containing the progress views */
            View layoutProgress;

            /** Project deadline or elapsed time */
            TextView textViewProjectTime;

            TextView textViewProjectStatus;

            TextView textViewProjectTitle;
            TextView textViewProjectInfo;

            TextView textViewProjectProgress;
            TextView textViewProjectBugs;
            TextView textViewProjectQuality;

            public ViewHolder(View v) {
                super(v);
            }
        }
    }
}
